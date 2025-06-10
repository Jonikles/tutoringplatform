package com.tutoringplatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import com.tutoringplatform.services.SubjectService;
import com.tutoringplatform.util.DataInitializer;
import org.springframework.boot.CommandLineRunner;

@SpringBootApplication
public class TutoringPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(TutoringPlatformApplication.class, args);
    }

    @Bean
    CommandLineRunner init(SubjectService subjectService) {
        return args -> {
            DataInitializer.initializeSubjects(subjectService);
            System.out.println("Application started! Access at http://localhost:8080");
        };
    }
}