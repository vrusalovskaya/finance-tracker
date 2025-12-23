package com.example.finance_tracker.controllers;

import com.example.finance_tracker.BaseE2ETest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerTest extends BaseE2ETest {

    @Test
    void register_whenValidRequest_thenReturnsJwt() throws Exception {
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {
                                      "userName": "john",
                                      "email": "john@test.com",
                                      "password": "password123"
                                    }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.token", not("")));
    }

    @Test
    void register_whenEmailAlreadyExists_thenReturns400() throws Exception {
        registerAndGetToken("dup@test.com");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {
                                      "userName": "john",
                                      "email": "dup@test.com",
                                      "password": "password123"
                                    }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_whenEmailIsInvalid_thenReturns400() throws Exception {
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {
                                      "userName": "john",
                                      "email": "not-an-email",
                                      "password": "password123"
                                    }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_whenPasswordIsTooShort_thenReturns400() throws Exception {
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {
                                      "userName": "john",
                                      "email": "short@test.com",
                                      "password": "123"
                                    }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_whenRequiredFieldsMissing_thenReturns400() throws Exception {
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {
                                      "email": "missing@test.com"
                                    }
                                """))
                .andExpect(status().isBadRequest());
    }


    @Test
    void login_whenValidCredentials_thenReturnsJwt() throws Exception {
        registerAndGetToken("login@test.com");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {
                                      "email": "login@test.com",
                                      "password": "password123"
                                    }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void login_whenPasswordIsWrong_thenReturns401() throws Exception {
        registerAndGetToken("wrongpass@test.com");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {
                                      "email": "wrongpass@test.com",
                                      "password": "wrong-password"
                                    }
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_whenUserDoesNotExist_thenReturns401() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {
                                      "email": "noone@test.com",
                                      "password": "password123"
                                    }
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_whenRequestBodyIsInvalid_thenReturns400() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {
                                      "email": ""
                                    }
                                """))
                .andExpect(status().isBadRequest());
    }
}

