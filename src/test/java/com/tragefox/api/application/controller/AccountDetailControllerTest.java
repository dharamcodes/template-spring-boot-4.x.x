package com.tragefox.api.application.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.tragefox.api.common.BaseTestSetup;
import com.tragefox.api.security.model.request.AccountDetailRequest;
import com.tragefox.api.security.model.response.AccountDetailResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;

@SpringBootTest
@AutoConfigureMockMvc
class AccountDetailControllerTest extends BaseTestSetup {

    private AccountDetailRequest accountDetailRequest;

    @BeforeEach
    void setUp() {
        accountDetailRequest = AccountDetailRequest.builder()
                .name("Test Account")
                .email("testacc@example.com")
                .identifier("testAccount")
                .phoneNumber(1234567890L)
                .isActive(true)
                .build();
    }

    @Test
    void testCreateAccount_Success() throws Exception {
        String responseJson = mockMvc.perform(post("/api/v1/account")
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountDetailRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        AccountDetailResponse created = objectMapper.readValue(responseJson, AccountDetailResponse.class);
        assertEquals(created.getEmail(), accountDetailRequest.getEmail());
        assertEquals(created.getName(), accountDetailRequest.getName());
    }

    @Test
    void testCreateAccount_Exception() throws Exception {
        mockMvc.perform(post("/api/v1/account")).andExpect(status().is4xxClientError());

        mockMvc.perform(post("/api/v1/account")
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountDetailRequest)))
                .andExpect(status().isConflict());

        mockMvc.perform(post("/api/v1/account")
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());

        mockMvc.perform(post("/api/v1/account")
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                AccountDetailRequest.builder().build())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetAccount_Success() throws Exception {

        mockMvc.perform(get("/api/v1/account/{identifier}", accountDetailRequest.getIdentifier())
                        .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Account"))
                .andExpect(jsonPath("$.identifier").value("testAccount"))
                .andExpect(jsonPath("$.phoneNumber").value(1234567890L));
    }

    @Test
    void testGetAccount_Exception() throws Exception {
        mockMvc.perform(get("/api/v1/account/{identifier}", " ").header("Authorization", authToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid parameter."))
                .andExpect(jsonPath("$.errorCode").value("E0001"));

        mockMvc.perform(get("/api/v1/account/{identifier}", "test").header("Authorization", authToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Data not found."))
                .andExpect(jsonPath("$.errorCode").value("E0004"));
    }

    @Test
    void testUpdateAccount_Success() throws Exception {
        accountDetailRequest.setEmail("updateduser@example.com");
        mockMvc.perform(put("/api/v1/account/{identifier}", "testAccount")
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountDetailRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Account"))
                .andExpect(jsonPath("$.identifier").value("testAccount"))
                .andExpect(jsonPath("$.phoneNumber").value(1234567890L));
    }

    @Test
    void testUpdateAccount_Exception() throws Exception {
        mockMvc.perform(put("/api/v1/account/{identifier}", " ")
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountDetailRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Data not found."))
                .andExpect(jsonPath("$.errorCode").value("E0004"));
    }

    @Test
    void testDeactivateAccount_Success() throws Exception {
        mockMvc.perform(delete("/api/v1/account/{identifier}", "testAccount").header("Authorization", authToken))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/account/{identifier}", "testAccount").header("Authorization", authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Account"))
                .andExpect(jsonPath("$.identifier").value("testAccount"))
                .andExpect(jsonPath("$.phoneNumber").value(1234567890L))
                .andExpect(jsonPath("$.isActive").value(false));
    }

    @Test
    void testDeactivateAccount_Exception() throws Exception {
        mockMvc.perform(delete("/api/v1/account/{identifier}", " ").header("Authorization", authToken))
                .andExpect(status().isNotFound());
    }
}
