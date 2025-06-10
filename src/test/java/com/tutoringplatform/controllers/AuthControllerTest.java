package com.tutoringplatform.controllers;

import com.tutoringplatform.models.*;
import com.tutoringplatform.services.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @Test
    @DisplayName("Should login successfully")
    void testLoginSuccess() throws Exception {
        // Arrange
        Student student = new Student("John Doe", "john@student.com", "password123");
        when(authService.login("john@student.com", "password123")).thenReturn(student);

        String loginJson = "{\"email\":\"john@student.com\",\"password\":\"password123\"}";

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@student.com"))
                .andExpect(jsonPath("$.userType").value("STUDENT"));
    }

    @Test
    @DisplayName("Should return 401 for invalid credentials")
    void testLoginInvalidCredentials() throws Exception {
        // Arrange
        when(authService.login("invalid@email.com", "wrongpass"))
                .thenThrow(new Exception("Invalid email or password"));

        String loginJson = "{\"email\":\"invalid@email.com\",\"password\":\"wrongpass\"}";

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid email or password"));
    }

    @Test
    @DisplayName("Should signup student successfully")
    void testSignupStudent() throws Exception {
        // Arrange
        Student newStudent = new Student("New Student", "new@student.com", "password123");
        when(authService.signupStudent("New Student", "new@student.com", "password123"))
                .thenReturn(newStudent);

        String signupJson = "{\"name\":\"New Student\",\"email\":\"new@student.com\",\"password\":\"password123\"}";

        // Act & Assert
        mockMvc.perform(post("/api/auth/signup/student")
                .contentType(MediaType.APPLICATION_JSON)
                .content(signupJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Student"))
                .andExpect(jsonPath("$.email").value("new@student.com"))
                .andExpect(jsonPath("$.userType").value("STUDENT"));
    }

    @Test
    @DisplayName("Should signup tutor successfully")
    void testSignupTutor() throws Exception {
        // Arrange
        Tutor newTutor = new Tutor("New Tutor", "new@tutor.com", "password123",
                75.0, "Experienced tutor");
        when(authService.signupTutor("New Tutor", "new@tutor.com", "password123",
                75.0, "Experienced tutor"))
                .thenReturn(newTutor);

        String signupJson = "{\"name\":\"New Tutor\",\"email\":\"new@tutor.com\"," +
                "\"password\":\"password123\",\"hourlyRate\":75.0," +
                "\"description\":\"Experienced tutor\"}";

        // Act & Assert
        mockMvc.perform(post("/api/auth/signup/tutor")
                .contentType(MediaType.APPLICATION_JSON)
                .content(signupJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Tutor"))
                .andExpect(jsonPath("$.email").value("new@tutor.com"))
                .andExpect(jsonPath("$.hourlyRate").value(75.0))
                .andExpect(jsonPath("$.userType").value("TUTOR"));
    }

    @Test
    @DisplayName("Should return 400 for duplicate email")
    void testSignupDuplicateEmail() throws Exception {
        // Arrange
        when(authService.signupStudent("John Doe", "existing@email.com", "password123"))
                .thenThrow(new Exception("Email already exists"));

        String signupJson = "{\"name\":\"John Doe\",\"email\":\"existing@email.com\"," +
                "\"password\":\"password123\"}";

        // Act & Assert
        mockMvc.perform(post("/api/auth/signup/student")
                .contentType(MediaType.APPLICATION_JSON)
                .content(signupJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Email already exists"));
    }
}