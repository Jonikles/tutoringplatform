package com.tutoringplatform.repository.impl;

import com.tutoringplatform.repositories.impl.StudentRepository;
import com.tutoringplatform.models.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StudentRepositoryTest {

    private StudentRepository repository;
    private Student testStudent;

    @BeforeEach
    void setUp() {
        repository = new StudentRepository();
        testStudent = new Student("John Doe", "john@test.com", "password123");
    }

    @Test
    @DisplayName("Should save and find student by ID")
    void testSaveAndFindById() {
        repository.save(testStudent);
        Student found = repository.findById(testStudent.getId());

        assertNotNull(found);
        assertEquals(testStudent.getId(), found.getId());
        assertEquals("John Doe", found.getName());
        assertEquals("john@test.com", found.getEmail());
    }

    @Test
    @DisplayName("Should return null when student not found")
    void testFindByIdNotFound() {
        Student found = repository.findById("non-existent-id");

        assertNull(found);
    }

    @Test
    @DisplayName("Should find student by email")
    void testFindByEmail() {
        repository.save(testStudent);

        Student found = repository.findByEmail("john@test.com");

        assertNotNull(found);
        assertEquals(testStudent.getId(), found.getId());
    }

    @Test
    @DisplayName("Should return null when email not found")
    void testFindByEmailNotFound() {
        Student found = repository.findByEmail("notfound@test.com");

        assertNull(found);
    }

    @Test
    @DisplayName("Should find all students")
    void testFindAll() {
        Student student2 = new Student("Jane Doe", "jane@test.com", "password123");
        repository.save(testStudent);
        repository.save(student2);

        List<Student> students = repository.findAll();

        assertEquals(2, students.size());
        assertTrue(students.stream().anyMatch(s -> s.getEmail().equals("john@test.com")));
        assertTrue(students.stream().anyMatch(s -> s.getEmail().equals("jane@test.com")));
    }

    @Test
    @DisplayName("Should update student")
    void testUpdate() {
        repository.save(testStudent);
        testStudent.setName("John Updated");
        testStudent.addFunds(100.0);

        repository.update(testStudent);
        Student updated = repository.findById(testStudent.getId());

        assertEquals("John Updated", updated.getName());
        assertEquals(100.0, updated.getBalance());
    }

    @Test
    @DisplayName("Should delete student")
    void testDelete() {
        repository.save(testStudent);

        repository.delete(testStudent.getId());
        Student found = repository.findById(testStudent.getId());

        assertNull(found);
    }

    @Test
    @DisplayName("Should find students by name containing")
    void testFindByNameContaining() {
        Student student2 = new Student("Jane Smith", "jane@test.com", "password123");
        Student student3 = new Student("Bob Johnson", "bob@test.com", "password123");
        repository.save(testStudent);
        repository.save(student2);
        repository.save(student3);

        List<Student> johnsons = repository.findByNameContaining("John");

        assertEquals(2, johnsons.size());
        assertTrue(johnsons.stream().anyMatch(s -> s.getName().equals("John Doe")));
        assertTrue(johnsons.stream().anyMatch(s -> s.getName().equals("Bob Johnson")));
    }

    @Test
    @DisplayName("Should handle case-insensitive name search")
    void testFindByNameContainingCaseInsensitive() {
        repository.save(testStudent);

        List<Student> found = repository.findByNameContaining("john");

        assertEquals(1, found.size());
        assertEquals("John Doe", found.get(0).getName());
    }
}