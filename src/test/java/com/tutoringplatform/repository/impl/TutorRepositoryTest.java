package com.tutoringplatform.repository.impl;

import com.tutoringplatform.models.Tutor;
import com.tutoringplatform.models.Subject;
import com.tutoringplatform.models.Review;
import com.tutoringplatform.repositories.impl.TutorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TutorRepositoryTest {

    private TutorRepository repository;
    private Tutor testTutor;
    private Subject mathSubject;
    private Subject physicsSubject;

    @BeforeEach
    void setUp() {
        repository = new TutorRepository();
        testTutor = new Tutor("Jane Smith", "jane@tutor.com", "password123",
                50.0, "Experienced math tutor");
        mathSubject = new Subject("Math", "Mathematics");
        physicsSubject = new Subject("Physics", "Science");
    }

    @Test
    @DisplayName("Should save and find tutor by ID")
    void testSaveAndFindById() {
        repository.save(testTutor);
        Tutor found = repository.findById(testTutor.getId());

        assertNotNull(found);
        assertEquals(testTutor.getId(), found.getId());
        assertEquals("Jane Smith", found.getName());
        assertEquals(50.0, found.getHourlyRate());
    }

    @Test
    @DisplayName("Should find tutors by subject")
    void testFindBySubject() {
        testTutor.addSubject(mathSubject);
        Tutor tutor2 = new Tutor("Bob Wilson", "bob@tutor.com", "password123",
                60.0, "Physics expert");
        tutor2.addSubject(physicsSubject);

        repository.save(testTutor);
        repository.save(tutor2);

        List<Tutor> mathTutors = repository.findBySubject(mathSubject);

        assertEquals(1, mathTutors.size());
        assertEquals("Jane Smith", mathTutors.get(0).getName());
    }

    @Test
    @DisplayName("Should find tutors by hourly rate range")
    void testFindByHourlyRateBetween() {
        Tutor tutor2 = new Tutor("Bob Wilson", "bob@tutor.com", "password123", 75.0, "Senior tutor");
        Tutor tutor3 = new Tutor("Alice Brown", "alice@tutor.com", "password123", 30.0, "Junior tutor");

        repository.save(testTutor);
        repository.save(tutor2);
        repository.save(tutor3);

        List<Tutor> midRangeTutors = repository.findByHourlyRateBetween(40.0, 60.0);

        assertEquals(1, midRangeTutors.size());
        assertEquals("Jane Smith", midRangeTutors.get(0).getName());
    }

    @Test
    @DisplayName("Should find tutors by minimum rating")
    void testFindByMinimumRating() {
        repository.save(testTutor);

        Review review1 = new Review("student1", testTutor.getId(), "booking1", 5, "Excellent!");
        Review review2 = new Review("student2", testTutor.getId(), "booking2", 4, "Very good");
        testTutor.addReview(review1);
        testTutor.addReview(review2);
        repository.update(testTutor);

        List<Tutor> highRatedTutors = repository.findByMinimumRating(4.0);

        assertEquals(1, highRatedTutors.size());
        assertEquals(4.5, highRatedTutors.get(0).getAverageRating());
    }

    @Test
    @DisplayName("Should handle tutors with no reviews")
    void testFindByMinimumRatingNoReviews() {
        repository.save(testTutor);

        List<Tutor> tutors = repository.findByMinimumRating(1.0);

        assertEquals(0, tutors.size());
    }

    @Test
    @DisplayName("Should update tutor availability")
    void testUpdateAvailability() {
        repository.save(testTutor);
        testTutor.addAvailability("Monday", 14);
        testTutor.addAvailability("Monday", 15);
        testTutor.addAvailability("Wednesday", 16);

        repository.update(testTutor);
        Tutor updated = repository.findById(testTutor.getId());

        assertTrue(updated.isAvailable("Monday", 14));
        assertTrue(updated.isAvailable("Monday", 15));
        assertTrue(updated.isAvailable("Wednesday", 16));
        assertFalse(updated.isAvailable("Tuesday", 14));
    }
}