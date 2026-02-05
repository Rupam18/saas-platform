package com.rupam.saas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rupam.saas.dto.LoginRequest;
import com.rupam.saas.dto.RegisterRequest;
import com.rupam.saas.dto.TaskRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class SearchIntegrationTest {

        private MockMvc mockMvc;

        @Autowired
        private WebApplicationContext context;

        @Autowired
        private com.rupam.saas.repository.UserRepository userRepo;

        @Autowired
        private com.rupam.saas.repository.TaskRepository taskRepo;

        @Autowired
        private com.rupam.saas.repository.CompanyRepository companyRepo;

        private ObjectMapper objectMapper = new ObjectMapper();

        private String token;

        @BeforeEach
        void setup() throws Exception {
                mockMvc = org.springframework.test.web.servlet.setup.MockMvcBuilders
                                .webAppContextSetup(context)
                                .apply(org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers
                                                .springSecurity())
                                .build();

                taskRepo.deleteAll();
                userRepo.deleteAll();
                companyRepo.deleteAll();

                // Register & Login to get token
                String email = "searchuser" + System.currentTimeMillis() + "@test.com";
                RegisterRequest registerReq = new RegisterRequest();
                registerReq.setEmail(email);
                registerReq.setPassword("password");
                registerReq.setCompanyName("Search Corp");
                registerReq.setRole("ADMIN");

                mockMvc.perform(post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registerReq)))
                                .andExpect(status().isOk())
                                .andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print());

                LoginRequest loginReq = new LoginRequest();
                loginReq.setEmail(email);
                loginReq.setPassword("password");

                MvcResult result = mockMvc.perform(post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginReq)))
                                .andExpect(status().isOk())
                                .andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
                                .andReturn();

                token = result.getResponse().getContentAsString();

                // Create tasks for testing
                createTask("Task A", "PENDING");
                createTask("Task B", "DONE");
                createTask("Task C", "PENDING");
                createTask("Urgent Task", "IN_PROGRESS");
        }

        private void createTask(String title, String status) throws Exception {
                TaskRequest req = new TaskRequest();
                req.setTitle(title);
                req.setDescription("Desc for " + title);
                req.setStatus(status);

                mockMvc.perform(post("/tasks")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)));
        }

        @Test
        void shouldReturnPagedResults() throws Exception {
                mockMvc.perform(get("/tasks?page=0&size=2")
                                .header("Authorization", "Bearer " + token))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content", hasSize(2)))
                                .andExpect(jsonPath("$.totalElements").value(4));
        }

        @Test
        void shouldFilterByStatus() throws Exception {
                mockMvc.perform(get("/tasks?status=DONE")
                                .header("Authorization", "Bearer " + token))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content", hasSize(1)))
                                .andExpect(jsonPath("$.content[0].title").value("Task B"));
        }

        @Test
        void shouldFilterByTitle() throws Exception {
                mockMvc.perform(get("/tasks?title=Urgent")
                                .header("Authorization", "Bearer " + token))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content", hasSize(1)))
                                .andExpect(jsonPath("$.content[0].title").value("Urgent Task"));
        }
}
