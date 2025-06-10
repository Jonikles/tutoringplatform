package com.tutoringplatform.util;

import com.tutoringplatform.models.Subject;
import com.tutoringplatform.models.Tutor;
import com.tutoringplatform.services.SubjectService;
import com.tutoringplatform.services.TutorService;

import org.springframework.stereotype.Component;

@Component
public class DataInitializer {

    
    public static void initializeSubjects(SubjectService subjectService) {
        if (subjectService == null) {
            System.err.println("DataInitializer: SubjectService is null. Cannot initialize subjects.");
            return;
        }
        try {
            subjectService.createSubject("Algebra", "Mathematics");
            subjectService.createSubject("Calculus", "Mathematics");
            subjectService.createSubject("Geometry", "Mathematics");
            subjectService.createSubject("Statistics", "Mathematics");

            subjectService.createSubject("Physics", "Science");
            subjectService.createSubject("Chemistry", "Science");
            subjectService.createSubject("Biology", "Science");

            subjectService.createSubject("English", "Language");
            subjectService.createSubject("Spanish", "Language");
            subjectService.createSubject("French", "Language");

            subjectService.createSubject("Java", "Programming");
            subjectService.createSubject("Python", "Programming");
            subjectService.createSubject("JavaScript", "Programming");

            System.out.println("DataInitializer: Subjects initialized successfully!");
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("Subject already exists")) {
                System.out.println("DataInitializer: Some subjects might already exist, which is okay. Details: "
                        + e.getMessage());
            } else {
                System.err.println("DataInitializer: Error initializing subjects: " + e.getMessage());
            }
        }
    }

    public static void initializeSampleTutors(TutorService tutorService, SubjectService subjectService) {
        if (tutorService == null || subjectService == null) {
            System.err.println("DataInitializer: TutorService or SubjectService is null. Cannot initialize tutors.");
            return;
        }
        try {
            Tutor tutor1 = new Tutor("John Smith", "john@tutor.com", "password123",
                    50.0, "Experienced math tutor with 10 years of experience");
            tutorService.register(tutor1);

            Subject algebra = subjectService.findByName("Algebra");
            Subject calculus = subjectService.findByName("Calculus");
            if (algebra != null)
                tutor1.addSubject(algebra);
            else
                System.err.println("DataInitializer: Could not find Algebra subject for Tutor1");
            if (calculus != null)
                tutor1.addSubject(calculus);
            else
                System.err.println("DataInitializer: Could not find Calculus subject for Tutor1");

            String[] days = { "Monday", "Tuesday", "Wednesday", "Thursday", "Friday" };
            for (String day : days) {
                for (int hour = 14; hour <= 20; hour++) {
                    tutor1.addAvailability(day, hour);
                }
            }
            tutorService.update(tutor1);

            Tutor tutor2 = new Tutor("Sarah Johnson", "sarah@tutor.com", "password123",
                    60.0, "Science specialist focusing on Physics and Chemistry");
            tutorService.register(tutor2);

            Subject physics = subjectService.findByName("Physics");
            Subject chemistry = subjectService.findByName("Chemistry");
            if (physics != null)
                tutor2.addSubject(physics);
            else
                System.err.println("DataInitializer: Could not find Physics subject for Tutor2");
            if (chemistry != null)
                tutor2.addSubject(chemistry);
            else
                System.err.println("DataInitializer: Could not find Chemistry subject for Tutor2");

            for (String day : days) {
                for (int hour = 16; hour <= 21; hour++) {
                    tutor2.addAvailability(day, hour);
                }
            }
            tutorService.update(tutor2);

            System.out.println("DataInitializer: Sample tutors initialized successfully!");
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("Email already exists")) {
                System.out.println(
                        "DataInitializer: Some tutors might already exist, which is okay. Details: " + e.getMessage());
            } else {
                System.err.println("DataInitializer: Error initializing sample tutors: " + e.getMessage());
            }
        }
    }
}