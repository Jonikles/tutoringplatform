package com.tutoringplatform.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.tutoringplatform.models.Booking;
import com.tutoringplatform.models.Payment;
import com.tutoringplatform.models.Student;
import com.tutoringplatform.models.Subject;
import com.tutoringplatform.models.Tutor;
import com.tutoringplatform.observer.BookingEvent;
import com.tutoringplatform.observer.BookingObserver;
//import com.tutoringplatform.observer.BookingEvent.EventType;
import com.tutoringplatform.repositories.interfaces.IBookingRepository;
import com.tutoringplatform.repositories.interfaces.IStudentRepository;
import com.tutoringplatform.repositories.interfaces.ITutorRepository;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import javax.annotation.PostConstruct;

@Service
public class BookingService {
    @Autowired
    private IBookingRepository bookingRepository;
    @Autowired
    private IStudentRepository studentRepository;
    @Autowired
    private ITutorRepository tutorRepository;
    private List<BookingObserver> observers;

    @PostConstruct
    public void init() {
        this.observers = new ArrayList<>();
    }

    public BookingService(IBookingRepository bookingRepository,
            IStudentRepository studentRepository,
            ITutorRepository tutorRepository) {
        this.bookingRepository = bookingRepository;
        this.studentRepository = studentRepository;
        this.tutorRepository = tutorRepository;
        this.observers = new ArrayList<>();
    }

    public void addObserver(BookingObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(BookingObserver observer) {
        observers.remove(observer);
    }

    private void notifyObservers(BookingEvent event) {
        for (BookingObserver observer : observers) {
            observer.update(event);
        }
    }

    public Booking createBooking(String studentId, String tutorId, Subject subject,
            LocalDateTime dateTime, int durationHours) throws Exception {
        Student student = studentRepository.findById(studentId);
        if (student == null) {
            throw new Exception("Student not found");
        }

        Tutor tutor = tutorRepository.findById(tutorId);
        if (tutor == null) {
            throw new Exception("Tutor not found");
        }

        if (!tutor.getSubjects().contains(subject)) {
            throw new Exception("Tutor does not teach this subject");
        }

        String day = dateTime.getDayOfWeek().toString().substring(0, 1)
                + dateTime.getDayOfWeek().toString().substring(1).toLowerCase();
        int hour = dateTime.getHour();
        for (int i = 0; i < durationHours; i++) {
            if (!tutor.isAvailable(day, hour + i)) {
                throw new Exception("Tutor is not available at this time");
            }
        }

        List<Booking> tutorBookings = bookingRepository.findByTutorId(tutorId);
        for (Booking b : tutorBookings) {
            if (b.getStatus() != Booking.BookingStatus.CANCELLED &&
                    isTimeConflict(b, dateTime, durationHours)) {
                throw new Exception("Time slot already booked");
            }
        }

        Booking booking = new Booking(studentId, tutorId, subject, dateTime, durationHours, tutor.getHourlyRate());
        bookingRepository.save(booking);

        notifyObservers(new BookingEvent(BookingEvent.EventType.CREATED, booking, student, tutor));

        return booking;
    }

    private boolean isTimeConflict(Booking existing, LocalDateTime newTime, int newDuration) {
        LocalDateTime existingStart = existing.getDateTime();
        LocalDateTime existingEnd = existingStart.plusHours(existing.getDurationHours());

        LocalDateTime newStart = newTime;
        LocalDateTime newEnd = newStart.plusHours(newDuration);

        // A conflict exists if the new booking starts before the existing one ends,
        // AND the new booking ends after the existing one starts.
        return newStart.isBefore(existingEnd) && newEnd.isAfter(existingStart);
    }

    public void confirmBooking(String bookingId, Payment payment) throws Exception {
        Booking booking = bookingRepository.findById(bookingId);
        if (booking == null) {
            throw new Exception("Booking not found");
        }

        if (booking.getStatus() != Booking.BookingStatus.PENDING) {
            throw new Exception("Booking is not in pending status");
        }

        booking.setPayment(payment);
        booking.setStatus(Booking.BookingStatus.CONFIRMED);
        bookingRepository.update(booking);

        Student student = studentRepository.findById(booking.getStudentId());
        Tutor tutor = tutorRepository.findById(booking.getTutorId());

        if (student != null) {
            student.addBooking(booking);
            studentRepository.update(student);
        } else {
            System.err.println("Student with ID " + booking.getStudentId() + " not found during booking confirmation.");
        }

        if (tutor != null) {
            tutor.addBooking(booking);
            tutorRepository.update(tutor);
        } else {
            System.err.println("Tutor with ID " + booking.getTutorId() + " not found during booking confirmation.");
        }

        notifyObservers(new BookingEvent(BookingEvent.EventType.CONFIRMED, booking, student, tutor));
    }

    public void cancelBooking(String bookingId) throws Exception {
        Booking booking = bookingRepository.findById(bookingId);
        if (booking == null) {
            throw new Exception("Booking not found");
        }

        if (booking.getStatus() == Booking.BookingStatus.COMPLETED) {
            throw new Exception("Cannot cancel completed booking");
        }

        booking.setStatus(Booking.BookingStatus.CANCELLED);
        bookingRepository.update(booking);

        Student student = studentRepository.findById(booking.getStudentId());
        Tutor tutor = tutorRepository.findById(booking.getTutorId());

        if (student != null) {
            student.removeBooking(booking); // Assumes User.removeBooking exists and works correctly
            studentRepository.update(student);
        } else {
            System.err.println("Student with ID " + booking.getStudentId() + " not found during booking cancellation.");
        }

        if (tutor != null) {
            tutor.removeBooking(booking); // Assumes User.removeBooking exists and works correctly
            tutorRepository.update(tutor);
        } else {
            System.err.println("Tutor with ID " + booking.getTutorId() + " not found during booking cancellation.");
        }

        notifyObservers(new BookingEvent(BookingEvent.EventType.CANCELLED, booking, student, tutor));
    }

    public void completeBooking(String bookingId) throws Exception {
        Booking booking = bookingRepository.findById(bookingId);
        if (booking == null) {
            throw new Exception("Booking not found");
        }

        if (booking.getStatus() != Booking.BookingStatus.CONFIRMED) {
            throw new Exception("Booking must be confirmed first");
        }

        booking.setStatus(Booking.BookingStatus.COMPLETED);
        bookingRepository.update(booking);

        Tutor tutor = tutorRepository.findById(booking.getTutorId());
        tutor.addEarnings(booking.getTotalCost());
        tutorRepository.update(tutor);

        Student student = studentRepository.findById(booking.getStudentId());

        notifyObservers(new BookingEvent(BookingEvent.EventType.COMPLETED, booking, student, tutor));
    }

    public Booking findById(String id) throws Exception {
        Booking booking = bookingRepository.findById(id);
        if (booking == null) {
            throw new Exception("Booking not found");
        }
        return booking;
    }

    public List<Booking> findByStudent(String studentId) {
        return bookingRepository.findByStudentId(studentId);
    }

    public List<Booking> findByTutor(String tutorId) {
        return bookingRepository.findByTutorId(tutorId);
    }
}