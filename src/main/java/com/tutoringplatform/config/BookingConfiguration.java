package com.tutoringplatform.config;

import com.tutoringplatform.services.BookingService;
import com.tutoringplatform.observer.*;
import com.tutoringplatform.repositories.interfaces.ITutorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import javax.annotation.PostConstruct;

@Configuration
public class BookingConfiguration {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private ITutorRepository tutorRepository;

    @PostConstruct
    public void setupObservers() {
        // Create and add observers
        BookingLogger bookingLogger = new BookingLogger();
        TutorUpdateObserver tutorUpdateObserver = new TutorUpdateObserver(tutorRepository);

        // Register observers with booking service
        bookingService.addObserver(bookingLogger);
        bookingService.addObserver(tutorUpdateObserver);

        System.out.println("✓ Booking observers configured: Logger and TutorUpdater registered");
    }
}