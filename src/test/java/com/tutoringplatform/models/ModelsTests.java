package com.tutoringplatform.models;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EntityTests {

    // Student Tests
    @Test
    @DisplayName("Student should deduct funds correctly")
    void testStudentDeductFunds() throws Exception { 
        Student student = new Student("John Doe", "john@test.com", "password");
        student.setBalance(100.0);

        student.deductFunds(30.0);

        assertEquals(70.0, student.getBalance());
    }

    @Test
    @DisplayName("Student should throw exception for insufficient funds")
    void testStudentInsufficientFunds() {
        Student student = new Student("John Doe", "john@test.com", "password");
        student.setBalance(20.0);

        assertThrows(Exception.class, () -> {
            student.deductFunds(50.0);
        }, "Insufficient funds");
    }

    // Tutor Tests
    @Test
    @DisplayName("Tutor should calculate average rating correctly")
    void testTutorAverageRating() {
        Tutor tutor = new Tutor("Jane Smith", "jane@tutor.com", "password", 50.0, "Math tutor");

        assertEquals(0.0, tutor.getAverageRating());

        tutor.addReview(new Review("student1", tutor.getId(), "booking1", 5, "Excellent"));
        tutor.addReview(new Review("student2", tutor.getId(), "booking2", 4, "Good"));
        tutor.addReview(new Review("student3", tutor.getId(), "booking3", 5, "Great"));

        assertEquals(4.67, tutor.getAverageRating(), 0.01);
    }

    @Test
    @DisplayName("Tutor availability should work correctly")
    void testTutorAvailability() {
        Tutor tutor = new Tutor("Jane Smith", "jane@tutor.com", "password", 50.0, "Math tutor");

        tutor.addAvailability("Monday", 14);
        tutor.addAvailability("Monday", 15);
        tutor.addAvailability("Wednesday", 10);

        assertTrue(tutor.isAvailable("Monday", 14));
        assertTrue(tutor.isAvailable("Monday", 15));
        assertTrue(tutor.isAvailable("Wednesday", 10));
        assertFalse(tutor.isAvailable("Monday", 16));
        assertFalse(tutor.isAvailable("Tuesday", 14));

        tutor.removeAvailability("Monday", 14);
        assertFalse(tutor.isAvailable("Monday", 14));
    }

    @Test
    @DisplayName("Tutor should not add duplicate availability")
    void testTutorDuplicateAvailability() {
        Tutor tutor = new Tutor("Jane Smith", "jane@tutor.com", "password", 50.0, "Math tutor");

        tutor.addAvailability("Monday", 14);
        tutor.addAvailability("Monday", 14);

        assertEquals(1, tutor.getAvailability().get("Monday").size());
    }

    @Test
    @DisplayName("Tutor should add availability from LocalDateTime")
    void testTutorAddAvailabilityFromLocalDateTime() {
        Tutor tutor = new Tutor("Jane Smith", "jane@tutor.com", "password", 50.0, "Math tutor");
        LocalDateTime monday2pm = LocalDateTime.of(2025, 6, 9, 14, 0);

        tutor.addAvailability(monday2pm, 3);

        assertTrue(tutor.isAvailable("Monday", 14));
        assertTrue(tutor.isAvailable("Monday", 15));
        assertTrue(tutor.isAvailable("Monday", 16));
        assertFalse(tutor.isAvailable("Monday", 17));
    }

    // Review Tests
    @Test
    @DisplayName("Review should validate rating range")
    void testReviewRatingValidation() {
        assertDoesNotThrow(() -> {
            new Review("student1", "tutor1", "booking1", 1, "Poor");
            new Review("student2", "tutor2", "booking2", 5, "Excellent");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new Review("student3", "tutor3", "booking3", 0, "Invalid");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new Review("student4", "tutor4", "booking4", 6, "Invalid");
        });
    }

    // Subject Tests
    @Test
    @DisplayName("Subject equality should work correctly")
    void testSubjectEquality() {
        Subject subject1 = new Subject("Math", "Mathematics");
        Subject subject2 = new Subject("Math", "Mathematics");
        Subject subject3 = subject1;

        assertEquals(subject1, subject3);
        assertNotEquals(subject1, subject2);
        assertNotEquals(subject1, null);
        assertNotEquals(subject1, "Not a subject");
    }

    // Booking Tests
    @Test
    @DisplayName("Booking should calculate total cost correctly")
    void testBookingTotalCost() {
        Subject subject = new Subject("Math", "Mathematics");
        LocalDateTime dateTime = LocalDateTime.now().plusDays(1);

        Booking booking = new Booking("student123", "tutor456", subject,
                dateTime, 3, 75.0);

        assertEquals(225.0, booking.getTotalCost());
    }

    // Payment Tests
    @Test
    @DisplayName("Payment should generate transaction ID")
    void testPaymentTransactionId() {
        Payment payment1 = new Payment("booking123", 100.0);
        Payment payment2 = new Payment("booking456", 150.0);

        assertNotNull(payment1.getTransactionId());
        assertNotNull(payment2.getTransactionId());
        assertNotEquals(payment1.getTransactionId(), payment2.getTransactionId());
        assertTrue(payment1.getTransactionId().startsWith("TXN-"));
    }
}