package com.tragefox.api.application.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.tragefox.api.common.BaseTestSetup;
import com.tragefox.api.security.constant.RoleType;
import com.tragefox.api.security.helper.JwtTokenHelper;
import com.tragefox.api.security.model.request.AuthenticationRequest;
import com.tragefox.api.security.model.request.LoginRequest;
import com.tragefox.api.security.model.request.ResetPasswordRequest;
import com.tragefox.api.security.model.response.AuthenticationResponse;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;

@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationControllerTest extends BaseTestSetup {

    private static final String PASSWORD_RESET_CLAIM = "PASSWORD_RESET_CLAIM";

    private AuthenticationRequest authenticationRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        authenticationRequest = AuthenticationRequest.builder()
                .userName(uuid)
                .email(uuid + "@example.com")
                .password("TestPassword123")
                .role(RoleType.APP_USER)
                .build();
        loginRequest = LoginRequest.builder()
                .userName(uuid)
                .password("TestPassword123")
                .build();
    }

    @Test
    void testSignup_Success() throws Exception {
        authenticationRequest.setUserName("");
        String responseJson = mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authenticationRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        AuthenticationResponse created = objectMapper.readValue(responseJson, AuthenticationResponse.class);
        assertEquals(created.getIsActive(), authenticationRequest.getIsActive());
        assertEquals(created.getUserName(), authenticationRequest.getUserName());
    }

    @Test
    void testSignup_Exception() throws Exception {
        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authenticationRequest)))
                .andExpect(status().isConflict());

        mockMvc.perform(post("/api/v1/auth/signup").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void testGetByUserName_Success() throws Exception {
        mockMvc.perform(get("/api/v1/auth/find/{userName}", uuid).header("Authorization", authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value(uuid));
    }

    @Test
    void testGetByUserName_NotFound() throws Exception {
        mockMvc.perform(get("/api/v1/auth/find/{userName}", "nonexistentUser").header("Authorization", authToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateAuthenticationDetails_Success() throws Exception {
        AuthenticationRequest updatedRequest = AuthenticationRequest.builder()
                .userName(uuid)
                .password("newPassword123")
                .isActive(true)
                .build();

        mockMvc.perform(put("/api/v1/auth/update/{userName}", uuid)
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value(uuid))
                .andExpect(jsonPath("$.isActive").value(true));
    }

    @Test
    void testUpdateAuthenticationDetails_Exception() throws Exception {
        AuthenticationRequest updatedRequest = AuthenticationRequest.builder()
                .userName("nonexistentUser")
                .password("irrelevant")
                .isActive(false)
                .build();

        mockMvc.perform(put("/api/v1/auth/update/{userName}", "nonexistentUser")
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testLogin_Success() throws Exception {
        var loginResponse = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk());
        loginResponse.andExpect(jsonPath("$.token").exists());
    }

    @Test
    void testLogin_Exception() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                AuthenticationRequest.builder().build())))
                .andExpect(status().isNotFound());

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(AuthenticationRequest.builder()
                                .userName(uuid)
                                .password("wrongpass")
                                .build())))
                .andExpect(status().isForbidden());
    }

    @Test
    void testResetPassword_Success() throws Exception {
        mockMvc.perform(post("/api/v1/auth/reset-password/{userName}", loginRequest.getUserName())
                        .header("Authorization", authToken))
                .andExpect(status().isAccepted());
    }

    @Test
    void testResetPassword_Exception() throws Exception {
        mockMvc.perform(post(
                                "/api/v1/auth/reset-password/{userName}",
                                UUID.randomUUID().toString())
                        .header("Authorization", authToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void testChangePassword_Success() throws Exception {
        var passwordResetToken = "Bearer "
                + JwtTokenHelper.generateResetPasswordToken(
                        uuid,
                        Map.of(
                                "passwordReset",
                                PASSWORD_RESET_CLAIM,
                                "role",
                                RoleType.PASSWORD_RESET_CLAIM.getValue()));
        ResetPasswordRequest resetPasswordRequest = ResetPasswordRequest.builder()
                .userName(uuid)
                .password("NewPassword123!")
                .confirmPassword("NewPassword123!")
                .build();

        mockMvc.perform(put("/api/v1/auth/password", uuid)
                        .header("Authorization", passwordResetToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resetPasswordRequest)))
                .andExpect(status().isAccepted());
    }

    @Test
    void testChangePassword_Exception() throws Exception {
        var passwordResetToken = "Bearer "
                + JwtTokenHelper.generateResetPasswordToken(
                        uuid,
                        Map.of(
                                "passwordReset",
                                PASSWORD_RESET_CLAIM,
                                "role",
                                RoleType.PASSWORD_RESET_CLAIM.getValue()));
        ResetPasswordRequest resetPasswordRequest = ResetPasswordRequest.builder()
                .userName(uuid)
                .password("NewPassword123!")
                .confirmPassword("NewPassword123")
                .build();

        mockMvc.perform(put("/api/v1/auth/password", uuid)
                        .header("Authorization", passwordResetToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resetPasswordRequest)))
                .andExpect(status().isBadRequest());

        mockMvc.perform(delete("/api/v1/auth/deactivate/{userName}", authenticationRequest.getUserName())
                        .header("Authorization", authToken))
                .andExpect(status().isAccepted());
        ResetPasswordRequest resetPasswordRequestInactive = ResetPasswordRequest.builder()
                .userName(uuid)
                .password("NewPassword123!")
                .confirmPassword("NewPassword123!")
                .build();
        mockMvc.perform(put("/api/v1/auth/password", uuid)
                        .header("Authorization", passwordResetToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resetPasswordRequestInactive)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testDeactivateUser_Success() throws Exception {
        mockMvc.perform(delete("/api/v1/auth/deactivate/{userName}", authenticationRequest.getUserName())
                        .header("Authorization", authToken))
                .andExpect(status().isAccepted());
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testDeactivateUser_Exception() throws Exception {
        mockMvc.perform(delete("/api/v1/auth/deactivate/{userName}", "userName").header("Authorization", authToken))
                .andExpect(status().isNotFound());
    }
}
