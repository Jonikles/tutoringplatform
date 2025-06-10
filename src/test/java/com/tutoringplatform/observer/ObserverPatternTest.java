package com.tutoringplatform.observer;

import com.tutoringplatform.models.*;
import com.tutoringplatform.repositories.interfaces.ITutorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ObserverPatternTest {

    @Mock
    private ITutorRepository tutorRepository;

    private Student testStudent;
    private Tutor testTutor;
    private Booking testBooking;
    private BookingEvent testEvent;

    @BeforeEach
    void setUp() {
        testStudent = new Student("John Doe", "john@student.com", "password123");
        testTutor = new Tutor("Jane Smith", "jane@tutor.com", "password123", 50.0, "Math tutor");

        Subject subject = new Subject("Math", "Mathematics");
        testBooking = new Booking("student123", "tutor456", subject,
                LocalDateTime.of(2025, 6, 10, 15, 0), 2, 50.0);

        testEvent = new BookingEvent(BookingEvent.EventType.CREATED,
                testBooking, testStudent, testTutor);
    }

    @Test
    @DisplayName("BookingLogger should log events")
    void testBookingLogger() {
        BookingLogger logger = new BookingLogger();

        logger.update(testEvent);
        logger.update(new BookingEvent(BookingEvent.EventType.CONFIRMED,
                testBooking, testStudent, testTutor));

        assertEquals(2, logger.getLogs().size());
        assertTrue(logger.getLogs().get(0).contains("CREATED"));
        assertTrue(logger.getLogs().get(1).contains("CONFIRMED"));
        assertTrue(logger.getLogs().get(0).contains("John Doe"));
        assertTrue(logger.getLogs().get(0).contains("Jane Smith"));
    }

    @Test
    @DisplayName("TutorUpdateObserver should remove availability on confirmation")
    void testTutorUpdateObserverConfirmed() {
        TutorUpdateObserver observer = new TutorUpdateObserver(tutorRepository);
        testTutor.addAvailability("Monday", 15);
        testTutor.addAvailability("Monday", 16);

        BookingEvent confirmedEvent = new BookingEvent(BookingEvent.EventType.CONFIRMED,
                testBooking, testStudent, testTutor);

        observer.update(confirmedEvent);

        assertFalse(testTutor.isAvailable("Monday", 15));
        assertFalse(testTutor.isAvailable("Monday", 16));
        verify(tutorRepository).update(testTutor);
    }

    @Test
    @DisplayName("TutorUpdateObserver should restore availability on cancellation")
    void testTutorUpdateObserverCancelled() {
        TutorUpdateObserver observer = new TutorUpdateObserver(tutorRepository);

        BookingEvent cancelledEvent = new BookingEvent(BookingEvent.EventType.CANCELLED,
                testBooking, testStudent, testTutor);

        observer.update(cancelledEvent);

        assertTrue(testTutor.isAvailable("Monday", 15));
        assertTrue(testTutor.isAvailable("Monday", 16));
        verify(tutorRepository).update(testTutor);
    }

    @Test
    @DisplayName("TutorUpdateObserver should ignore other event types")
    void testTutorUpdateObserverIgnoresOtherEvents() {
        TutorUpdateObserver observer = new TutorUpdateObserver(tutorRepository);

        BookingEvent completedEvent = new BookingEvent(BookingEvent.EventType.COMPLETED,
                testBooking, testStudent, testTutor);

        observer.update(completedEvent);

        verify(tutorRepository, never()).update(any());
    }

    @Test
    @DisplayName("Multiple observers should all receive notifications")
    void testMultipleObservers() {
        BookingLogger logger = new BookingLogger();
        TutorUpdateObserver tutorObserver = new TutorUpdateObserver(tutorRepository);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        logger.update(testEvent);
        tutorObserver.update(testEvent);

        String output = outputStream.toString();
        assertTrue(output.contains("EMAIL NOTIFICATION"));
        assertEquals(1, logger.getLogs().size());
        verify(tutorRepository, never()).update(any());
    }
}