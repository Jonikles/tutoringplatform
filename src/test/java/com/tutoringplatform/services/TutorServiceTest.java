package com.tutoringplatform.services;

import com.tutoringplatform.models.Tutor;
import com.tutoringplatform.models.Subject;
import com.tutoringplatform.models.Review;
import com.tutoringplatform.repositories.interfaces.ITutorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TutorServiceTest {

    @Mock
    private ITutorRepository tutorRepository;

    private TutorService tutorService;
    private Tutor testTutor;

    @BeforeEach
    void setUp() {
        tutorService = new TutorService(tutorRepository);
        testTutor = new Tutor("Jane Smith", "jane@tutor.com", "password123",
                50.0, "Experienced math tutor");
    }

    @Test
    @DisplayName("Should register new tutor successfully")
    void testRegisterTutor() throws Exception {
        // Arrange
        when(tutorRepository.findByEmail("jane@tutor.com")).thenReturn(null);

        // Act
        tutorService.register(testTutor);

        // Assert
        verify(tutorRepository).save(testTutor);
    }

    @Test
    @DisplayName("Should throw exception when email already exists")
    void testRegisterTutorEmailExists() {
        // Arrange
        when(tutorRepository.findByEmail("jane@tutor.com")).thenReturn(testTutor);

        // Act & Assert
        Tutor newTutor = new Tutor("Another Jane", "jane@tutor.com", "password", 60.0, "Description");
        assertThrows(Exception.class, () -> {
            tutorService.register(newTutor);
        }, "Email already exists");
    }

    @Test
    @DisplayName("Should throw exception for invalid hourly rate")
    void testRegisterTutorInvalidRate() {
        // Arrange
        Tutor invalidTutor = new Tutor("Bob", "bob@tutor.com", "password", -10.0, "Description");
        when(tutorRepository.findByEmail("bob@tutor.com")).thenReturn(null);

        // Act & Assert
        assertThrows(Exception.class, () -> {
            tutorService.register(invalidTutor);
        }, "Hourly rate must be positive");
    }

    @Test
    @DisplayName("Should search tutors by subject")
    void testSearchTutorsBySubject() {
        // Arrange
        Subject mathSubject = new Subject("Math", "Mathematics");
        testTutor.addSubject(mathSubject);

        Tutor tutor2 = new Tutor("Bob Wilson", "bob@tutor.com", "password", 60.0, "Physics tutor");
        Subject physicsSubject = new Subject("Physics", "Science");
        tutor2.addSubject(physicsSubject);

        List<Tutor> allTutors = Arrays.asList(testTutor, tutor2);
        when(tutorRepository.findAll()).thenReturn(allTutors);

        // Act
        List<Tutor> mathTutors = tutorService.searchTutors(mathSubject, null, null, null, null, null);

        // Assert
        assertEquals(1, mathTutors.size());
        assertEquals("Jane Smith", mathTutors.get(0).getName());
    }

    @Test
    @DisplayName("Should search tutors by price range")
    void testSearchTutorsByPriceRange() {
        // Arrange
        Tutor tutor2 = new Tutor("Bob", "bob@tutor.com", "password", 75.0, "Senior tutor");
        Tutor tutor3 = new Tutor("Alice", "alice@tutor.com", "password", 30.0, "Junior tutor");

        List<Tutor> allTutors = Arrays.asList(testTutor, tutor2, tutor3); // 50, 75, 30
        when(tutorRepository.findAll()).thenReturn(allTutors);

        // Act
        List<Tutor> midRangeTutors = tutorService.searchTutors(null, 40.0, 60.0, null, null, null);

        // Assert
        assertEquals(1, midRangeTutors.size());
        assertEquals(50.0, midRangeTutors.get(0).getHourlyRate());
    }

    @Test
    @DisplayName("Should search tutors by minimum rating")
    void testSearchTutorsByRating() {
        // Arrange
        // Add reviews to give tutor a rating
        testTutor.addReview(
                new Review("student1", testTutor.getId(), "booking1", 5, "Excellent"));
        testTutor.addReview(
                new Review("student2", testTutor.getId(), "booking2", 4, "Good"));

        Tutor lowRatedTutor = new Tutor("Bob", "bob@tutor.com", "password", 40.0, "New tutor");
        lowRatedTutor.addReview(
                new Review("student3", lowRatedTutor.getId(), "booking3", 2, "Poor"));

        List<Tutor> allTutors = Arrays.asList(testTutor, lowRatedTutor);
        when(tutorRepository.findAll()).thenReturn(allTutors);

        // Act
        List<Tutor> highRatedTutors = tutorService.searchTutors(null, null, null, 4.0, null, null);

        // Assert
        assertEquals(1, highRatedTutors.size());
        assertEquals(4.5, highRatedTutors.get(0).getAverageRating());
    }

    @Test
    @DisplayName("Should search tutors by availability")
    void testSearchTutorsByAvailability() {
        // Arrange
        testTutor.addAvailability("Monday", 14);
        testTutor.addAvailability("Monday", 15);

        Tutor tutor2 = new Tutor("Bob", "bob@tutor.com", "password", 60.0, "Evening tutor");
        tutor2.addAvailability("Monday", 18);
        tutor2.addAvailability("Monday", 19);

        List<Tutor> allTutors = Arrays.asList(testTutor, tutor2);
        when(tutorRepository.findAll()).thenReturn(allTutors);

        // Act
        List<Tutor> afternoonTutors = tutorService.searchTutors(null, null, null, null, "Monday", 14);

        // Assert
        assertEquals(1, afternoonTutors.size());
        assertEquals("Jane Smith", afternoonTutors.get(0).getName());
    }

    @Test
    @DisplayName("Should combine multiple search filters")
    void testSearchTutorsMultipleFilters() {
        // Arrange
        Subject mathSubject = new Subject("Math", "Mathematics");
        testTutor.addSubject(mathSubject);
        testTutor.addAvailability("Monday", 14);

        Tutor tutor2 = new Tutor("Bob", "bob@tutor.com", "password", 100.0, "Expensive tutor");
        tutor2.addSubject(mathSubject);
        tutor2.addAvailability("Monday", 14);

        List<Tutor> allTutors = Arrays.asList(testTutor, tutor2);
        when(tutorRepository.findAll()).thenReturn(allTutors);

        // Act - Search for math tutors under $60 available Monday at 2pm
        List<Tutor> filteredTutors = tutorService.searchTutors(mathSubject, null, 60.0, null, "Monday", 14);

        // Assert
        assertEquals(1, filteredTutors.size());
        assertEquals("Jane Smith", filteredTutors.get(0).getName());
    }

    @Test
    @DisplayName("Should update availability successfully")
    void testUpdateAvailability() throws Exception {
        // Arrange
        when(tutorRepository.findById("tutor123")).thenReturn(testTutor);

        // Act - Add availability
        tutorService.updateAvailability("tutor123", "Monday", 14, true);

        // Assert
        assertTrue(testTutor.isAvailable("Monday", 14));
        verify(tutorRepository).update(testTutor);

        // Act - Remove availability
        tutorService.updateAvailability("tutor123", "Monday", 14, false);

        // Assert
        assertFalse(testTutor.isAvailable("Monday", 14));
        verify(tutorRepository, times(2)).update(testTutor);
    }

    @Test
    @DisplayName("Should throw exception when tutor not found for update")
    void testUpdateAvailabilityTutorNotFound() {
        // Arrange
        when(tutorRepository.findById("invalid")).thenReturn(null);

        // Act & Assert
        assertThrows(Exception.class, () -> {
            tutorService.updateAvailability("invalid", "Monday", 14, true);
        }, "User not found with id: invalid");
    }

    @Test
    @DisplayName("Should find tutor by ID")
    void testFindById() throws Exception {
        // Arrange
        when(tutorRepository.findById("tutor123")).thenReturn(testTutor);

        // Act
        Tutor found = tutorService.findById("tutor123");

        // Assert
        assertNotNull(found);
        assertEquals(testTutor.getId(), found.getId());
    }

    @Test
    @DisplayName("Should return empty list when no tutors match criteria")
    void testSearchNoMatches() {
        // Arrange
        when(tutorRepository.findAll()).thenReturn(Arrays.asList(testTutor));

        // Act - Search for tutors over $100 (none exist)
        List<Tutor> expensiveTutors = tutorService.searchTutors(null, 100.0, null, null, null, null);

        // Assert
        assertTrue(expensiveTutors.isEmpty());
    }
}