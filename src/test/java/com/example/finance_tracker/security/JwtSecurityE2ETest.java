package com.example.finance_tracker.security;

import com.example.finance_tracker.BaseE2ETest;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class JwtSecurityE2ETest extends BaseE2ETest {

    @Test
    void accessProtectedEndpoint_whenJwtIsMissing_thenReturns401() throws Exception {
        mockMvc.perform(get("/api/v1/categories"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void accessProtectedEndpoint_whenAuthorizationHeaderHasNoBearerPrefix_thenReturns401() throws Exception {
        String token = registerAndGetToken("nobearer@test.com");

        mockMvc.perform(get("/api/v1/categories")
                        .header("Authorization", token))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void accessProtectedEndpoint_whenJwtIsMalformed_thenReturns401() throws Exception {
        mockMvc.perform(get("/api/v1/categories")
                        .header("Authorization", "Bearer not-a-jwt"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void accessProtectedEndpoint_whenJwtSignatureIsInvalid_thenReturns401() throws Exception {

        SecretKey wrongKey = Keys.hmacShaKeyFor(
                "this-is-a-wrong-secret-key-which-is-long-enough"
                        .getBytes(StandardCharsets.UTF_8)
        );

        String invalidToken = Jwts.builder()
                .subject("user@test.com")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 60000))
                .signWith(wrongKey)
                .compact();

        mockMvc.perform(get("/api/v1/categories")
                        .header("Authorization", "Bearer " + invalidToken))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void accessProtectedEndpoint_whenJwtIsExpired_thenReturns401() throws Exception {

        SecretKey key = Keys.hmacShaKeyFor(
                "very-strong-secret-key-should-be-at-least-32-bytes"
                        .getBytes(StandardCharsets.UTF_8)
        );

        String expiredToken = Jwts.builder()
                .subject("expired@test.com")
                .issuedAt(new Date(System.currentTimeMillis() - 120000))
                .expiration(new Date(System.currentTimeMillis() - 60000))
                .signWith(key)
                .compact();

        mockMvc.perform(get("/api/v1/categories")
                        .header("Authorization", "Bearer " + expiredToken))
                .andExpect(status().isUnauthorized());
    }
}
