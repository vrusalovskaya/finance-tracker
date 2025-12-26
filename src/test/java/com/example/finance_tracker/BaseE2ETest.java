package com.example.finance_tracker;

import com.example.finance_tracker.common.Type;
import com.example.finance_tracker.dtos.TokenResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@ActiveProfiles("test")
@Import(JacksonTestConfig.class)
public abstract class BaseE2ETest {

    @Autowired
    protected WebApplicationContext webApplicationContext;

    @Autowired
    protected ObjectMapper objectMapper;

    protected MockMvc mockMvc;

    @BeforeEach
    void setupMockMvc() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    protected String registerAndGetToken(String email) throws Exception {

        MvcResult result = mockMvc.perform(
                        post("/api/v1/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                            {
                                              "userName": "test",
                                              "email": "%s",
                                              "password": "password123"
                                            }
                                        """.formatted(email))
                )
                .andExpect(status().isOk())
                .andReturn();

        TokenResponse tokenResponse = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                TokenResponse.class
        );

        return tokenResponse.token();
    }

    protected Long createCategory(String token, String name, Type type) throws Exception {
        String response = mockMvc.perform(post("/api/v1/categories")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {
                                      "name": "%s",
                                      "type": "%s"
                                    }
                                """.formatted(name, type)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        return extractId(response);
    }

    protected Long createTransaction(
            String token,
            Long categoryId,
            int amount,
            Type type
    ) throws Exception {

        String response = mockMvc.perform(post("/api/v1/transactions")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {
                                      "type": "%s",
                                      "amount": %d,
                                      "date": "%s",
                                      "categoryId": %d
                                    }
                                """.formatted(type, amount, LocalDate.now(), categoryId)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        return extractId(response);
    }

    protected void createTransaction(
            String token,
            Long categoryId,
            int amount,
            Type type,
            LocalDate date
    ) throws Exception {

        String response = mockMvc.perform(post("/api/v1/transactions")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {
                                      "type": "%s",
                                      "amount": %d,
                                      "date": "%s",
                                      "categoryId": %d
                                    }
                                """.formatted(type, amount, date, categoryId)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        extractId(response);
    }


    protected Long extractId(String json) throws Exception {
        return objectMapper.readTree(json).get("id").asLong();
    }
}
