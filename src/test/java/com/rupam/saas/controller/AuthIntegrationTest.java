package com.rupam.saas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rupam.saas.dto.LoginRequest;
import com.rupam.saas.dto.RegisterRequest;
import com.rupam.saas.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class AuthIntegrationTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserRepository userRepo;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity())
                .build();
        // Clean up database before each test to ensure a fresh state
        userRepo.deleteAll();
    }

    @Test
    void shouldRegisterSuccessfully() throws Exception {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("test@example.com");
        req.setPassword("password123");
        req.setCompanyName("Test Corp");
        req.setRole("ADMIN");

        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldLoginSuccessfully() throws Exception {
        // First Register
        RegisterRequest registerReq = new RegisterRequest();
        registerReq.setEmail("login@example.com");
        registerReq.setPassword("password123");
        registerReq.setCompanyName("Login Corp");
        registerReq.setRole("ADMIN");

        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerReq)))
                .andExpect(status().isOk());

        // Then Login
        LoginRequest loginReq = new LoginRequest();
        loginReq.setEmail("login@example.com");
        loginReq.setPassword("password123");

        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldFailLoginWithWrongPassword() throws Exception {
        // First Register
        RegisterRequest registerReq = new RegisterRequest();
        registerReq.setEmail("wrongpass@example.com");
        registerReq.setPassword("password123");
        registerReq.setCompanyName("WrongPass Corp");
        registerReq.setRole("ADMIN");

        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerReq)))
                .andExpect(status().isOk());

        // Try Login with wrong password
        LoginRequest loginReq = new LoginRequest();
        loginReq.setEmail("wrongpass@example.com");
        loginReq.setPassword("wrongpassword");

        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isBadRequest()); // Expect global exception handler to convert RuntimeException to
                                                     // 400
    }
}
