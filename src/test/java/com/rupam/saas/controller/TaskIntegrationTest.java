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

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class TaskIntegrationTest {

        private MockMvc mockMvc;

        @Autowired
        private WebApplicationContext context;

        @Autowired
        private UserRepository userRepo;

        @Autowired
        private TaskRepository taskRepo;

        private ObjectMapper objectMapper = new ObjectMapper();

        private String token;

        @BeforeEach
        void setUp() throws Exception {
                mockMvc = MockMvcBuilders
                                .webAppContextSetup(context)
                                .apply(springSecurity())
                                .build();
                taskRepo.deleteAll();
                userRepo.deleteAll();

                // 1. Register User
                RegisterRequest registerReq = new RegisterRequest();
                registerReq.setEmail("taskuser@example.com");
                registerReq.setPassword("password123");
                registerReq.setCompanyName("Task Corp");
                registerReq.setRole("USER");

                mockMvc.perform(post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registerReq)))
                                .andExpect(status().isOk());

                // 2. Login to get Token
                LoginRequest loginReq = new LoginRequest();
                loginReq.setEmail("taskuser@example.com");
                loginReq.setPassword("password123");

                MvcResult result = mockMvc.perform(post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginReq)))
                                .andExpect(status().isOk())
                                .andReturn();

                token = result.getResponse().getContentAsString();
        }

        @Test
        void shouldCreateAndGetTask() throws Exception {
                TaskRequest taskReq = new TaskRequest();
                taskReq.setTitle("New Integration Task");
                taskReq.setDescription("Testing with MockMvc");
                taskReq.setStatus("PENDING");

                // Create Task
                mockMvc.perform(post("/tasks")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(taskReq)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.title").value("New Integration Task"));

                // Get Tasks
                mockMvc.perform(get("/tasks")
                                .header("Authorization", "Bearer " + token))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content[0].title").value("New Integration Task"));
        }
}
