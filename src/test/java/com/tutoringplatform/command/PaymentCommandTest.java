package com.tutoringplatform.command;

import com.tutoringplatform.models.*;
import com.tutoringplatform.repositories.interfaces.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentCommandTest {

    @Mock
    private IPaymentRepository paymentRepository;

    @Mock
    private IStudentRepository studentRepository;

    private Student testStudent;
    private Payment testPayment;

    @BeforeEach
    void setUp() {
        testStudent = new Student("John Doe", "john@student.com", "password123");
        testStudent.setBalance(200.0);
        testPayment = new Payment("booking123", 100.0);
    }

    @Test
    @DisplayName("ProcessPaymentCommand should execute successfully")
    void testProcessPaymentCommandExecute() throws Exception {
        // Arrange
        ProcessPaymentCommand command = new ProcessPaymentCommand(
                testPayment, testStudent, 100.0, paymentRepository, studentRepository);

        // Act
        command.execute();

        // Assert
        assertEquals(100.0, testStudent.getBalance()); // 200 - 100
        assertEquals(Payment.PaymentStatus.COMPLETED, testPayment.getStatus());
        verify(paymentRepository).save(testPayment);
        verify(studentRepository).update(testStudent);
    }

    @Test
    @DisplayName("ProcessPaymentCommand should throw exception for insufficient funds")
    void testProcessPaymentCommandInsufficientFunds() {
        // Arrange
        testStudent.setBalance(50.0);
        ProcessPaymentCommand command = new ProcessPaymentCommand(
                testPayment, testStudent, 100.0, paymentRepository, studentRepository);

        // Act & Assert
        assertThrows(Exception.class, () -> command.execute(), "Insufficient funds");
    }

    @Test
    @DisplayName("ProcessPaymentCommand should undo successfully")
    void testProcessPaymentCommandUndo() throws Exception {
        // Arrange
        ProcessPaymentCommand command = new ProcessPaymentCommand(
                testPayment, testStudent, 100.0, paymentRepository, studentRepository);

        // Execute first
        command.execute();
        assertEquals(100.0, testStudent.getBalance());

        // Act - Undo
        command.undo();

        // Assert
        assertEquals(200.0, testStudent.getBalance()); // Back to original
        assertEquals(Payment.PaymentStatus.REFUNDED, testPayment.getStatus());
        verify(paymentRepository).update(testPayment);
        verify(studentRepository, times(2)).update(testStudent);
    }

    @Test
    @DisplayName("RefundPaymentCommand should execute successfully")
    void testRefundPaymentCommandExecute() throws Exception {
        // Arrange
        testPayment.setStatus(Payment.PaymentStatus.COMPLETED);
        testStudent.setBalance(100.0); // After payment

        RefundPaymentCommand command = new RefundPaymentCommand(
                testPayment, testStudent, 100.0, paymentRepository, studentRepository);

        // Act
        command.execute();

        // Assert
        assertEquals(200.0, testStudent.getBalance()); // 100 + 100 refund
        assertEquals(Payment.PaymentStatus.REFUNDED, testPayment.getStatus());
        verify(paymentRepository).update(testPayment);
        verify(studentRepository).update(testStudent);
    }

    @Test
    @DisplayName("RefundPaymentCommand should throw exception for non-completed payment")
    void testRefundPaymentCommandNotCompleted() {
        // Arrange
        testPayment.setStatus(Payment.PaymentStatus.PENDING);

        RefundPaymentCommand command = new RefundPaymentCommand(
                testPayment, testStudent, 100.0, paymentRepository, studentRepository);

        // Act & Assert
        assertThrows(Exception.class, () -> command.execute(),
                "Can only refund completed payments");
    }

    @Test
    @DisplayName("RefundPaymentCommand should undo successfully")
    void testRefundPaymentCommandUndo() throws Exception {
        // Arrange
        testPayment.setStatus(Payment.PaymentStatus.COMPLETED);
        testStudent.setBalance(100.0);

        RefundPaymentCommand command = new RefundPaymentCommand(
                testPayment, testStudent, 100.0, paymentRepository, studentRepository);

        // Execute refund first
        command.execute();
        assertEquals(200.0, testStudent.getBalance());

        // Act - Undo refund
        command.undo();

        // Assert
        assertEquals(100.0, testStudent.getBalance()); // Back to post-payment amount
        assertEquals(Payment.PaymentStatus.COMPLETED, testPayment.getStatus());
        verify(paymentRepository, times(2)).update(testPayment);
        verify(studentRepository, times(2)).update(testStudent);
    }

    @Test
    @DisplayName("Commands should return correct payment")
    void testGetPayment() {
        // Arrange
        ProcessPaymentCommand processCommand = new ProcessPaymentCommand(
                testPayment, testStudent, 100.0, paymentRepository, studentRepository);

        RefundPaymentCommand refundCommand = new RefundPaymentCommand(
                testPayment, testStudent, 100.0, paymentRepository, studentRepository);

        // Act & Assert
        assertEquals(testPayment, processCommand.getPayment());
        assertEquals(testPayment, refundCommand.getPayment());
    }
}