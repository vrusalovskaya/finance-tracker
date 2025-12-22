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
    void register_success_returns_jwt() throws Exception {
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
    void register_duplicate_email_returns_400() throws Exception {
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
    void register_invalid_email_returns_400() throws Exception {
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
    void register_short_password_returns_400() throws Exception {
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
    void register_missing_fields_returns_400() throws Exception {
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
    void login_success_returns_jwt() throws Exception {
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
    void login_wrong_password_returns_401() throws Exception {
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
    void login_non_existing_user_returns_401() throws Exception {
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
    void login_invalid_request_body_returns_400() throws Exception {
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

