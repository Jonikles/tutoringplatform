package com.tutoringplatform.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.tutoringplatform.services.BookingService;
import com.tutoringplatform.services.PaymentService;
import com.tutoringplatform.services.ReviewService;
import com.tutoringplatform.services.StudentService;
import com.tutoringplatform.services.SubjectService;
import com.tutoringplatform.services.TutorService;
import com.tutoringplatform.util.DataInitializer;

@Configuration
@Profile("!test")
public class DataInitializationConfiguration {

    @Bean
    CommandLineRunner init(SubjectService subjectService, TutorService tutorService, StudentService studentService,
            BookingService bookingService, PaymentService paymentService, ReviewService reviewService) {
        return args -> {
            DataInitializer.initializeAllData(subjectService, tutorService, studentService, bookingService,
                    paymentService, reviewService);
            System.out.println("Application started! Access at http://localhost:8080");
        };
    }
}