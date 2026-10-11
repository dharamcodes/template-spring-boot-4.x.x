package com.tragefox.api.common;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.tragefox.api.security.constant.RoleType;
import com.tragefox.api.security.model.request.AuthenticationRequest;
import com.tragefox.api.security.model.request.LoginRequest;
import com.tragefox.api.security.model.response.LoginResponse;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;
import tools.jackson.databind.ObjectMapper;

@Testcontainers
public abstract class BaseTestSetup {

    static ContainerSetup mongo = ContainerSetup.getInstance();

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.mongodb.uri", mongo::getConnectionString);
        registry.add("spring.data.mongodb.uri", mongo::getConnectionString);
    }

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    protected String authToken;
    protected String uuid;

    @BeforeEach
    void setupUserAndAuth() throws Exception {
        uuid = UUID.randomUUID().toString();
        AuthenticationRequest signupRequest = AuthenticationRequest.builder()
                .userName(uuid)
                .email(uuid + "@example.com")
                .password("TestPassword123")
                .role(RoleType.APP_USER)
                .isActive(true)
                .build();

        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk());

        LoginRequest loginRequest = LoginRequest.builder()
                .userName(uuid)
                .password("TestPassword123")
                .build();

        String loginResponse = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        LoginResponse response = objectMapper.readValue(loginResponse, LoginResponse.class);
        this.authToken = "Bearer " + response.getToken();
    }
}
