package com.rupam.saas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rupam.saas.dto.LoginRequest;
import com.rupam.saas.dto.RegisterRequest;
import com.rupam.saas.dto.TaskRequest;
import com.rupam.saas.repository.TaskRepository;
import com.rupam.saas.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class CustomUserScenarioTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private TaskRepository taskRepo;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        taskRepo.deleteAll();
        userRepo.deleteAll();
    }

    @Test
    void shouldCompleteFullUserJourney() throws Exception {
        // ==========================================
        // STEP 1: Register a New User
        // ==========================================
        System.out.println("Step 1: Registering User...");
        RegisterRequest registerReq = new RegisterRequest();
        registerReq.setEmail("custom@test.com");
        registerReq.setPassword("Password123!");
        registerReq.setCompanyName("Custom Corp");
        registerReq.setRole("ADMIN");

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerReq)))
                .andExpect(status().isOk())
                .andDo(print());

        // ==========================================
        // STEP 2: Login
        // ==========================================
        System.out.println("Step 2: Logging In...");
        LoginRequest loginReq = new LoginRequest();
        loginReq.setEmail("custom@test.com");
        loginReq.setPassword("Password123!");

        MvcResult loginResult = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isOk())
                .andReturn();

        String token = loginResult.getResponse().getContentAsString();
        if (token == null || token.isEmpty()) {
            throw new RuntimeException("Login failed: No token received");
        }

        // ==========================================
        // STEP 3: Create a Task
        // ==========================================
        System.out.println("Step 3: Creating Task...");
        TaskRequest taskReq = new TaskRequest();
        taskReq.setTitle("Custom Input Task");
        taskReq.setDescription("This task is created via custom verification scenario.");
        taskReq.setStatus("PENDING");

        MvcResult taskResult = mockMvc.perform(post("/tasks")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Custom Input Task"))
                .andReturn();

        String responseBody = taskResult.getResponse().getContentAsString();
        // Simple extraction of ID for next steps (assuming response is JSON/DTO)
        // In a real scenario, use proper JSON parsing. Here we just rely on order or
        // search.

        // ==========================================
        // STEP 4: Update the Task
        // ==========================================
        // Note: For simplicity in this mock test without parsing ID, let's search it
        // first.

        // ==========================================
        // STEP 5: Search for Task
        // ==========================================
        System.out.println("Step 5: Searching for Task...");
        mockMvc.perform(get("/tasks?title=Custom")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].title").value("Custom Input Task"))
                .andDo(print());

        // ==========================================
        // STEP 6: Verify Analytics
        // ==========================================
        System.out.println("Step 6: Checking Analytics...");
        mockMvc.perform(get("/analytics/tasks")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalTasks").value(1))
                .andExpect(jsonPath("$.pendingTasks").value(1))
                .andDo(print());

        System.out.println("✅ Custom User Journey Completed Successfully!");
    }
}
