package com.example.finance_tracker.controllers;

import com.example.finance_tracker.BaseE2ETest;
import com.example.finance_tracker.common.Type;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class CategoryControllerTest extends BaseE2ETest {

    @Test
    void accessCategories_whenJwtIsMissing_thenReturns401() throws Exception {
        mockMvc.perform(get("/api/v1/categories"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createCategory_whenValidRequest_thenReturns201() throws Exception {
        String token = registerAndGetToken("createcat@test.com");

        mockMvc.perform(post("/api/v1/categories")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {
                                      "name": "Food",
                                      "type": "EXPENSE",
                                      "description": "Groceries"
                                    }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Food"))
                .andExpect(jsonPath("$.type").value("EXPENSE"))
                .andExpect(jsonPath("$.description").value("Groceries"))
                .andExpect(jsonPath("$.userId").exists());
    }

    @Test
    void createCategory_whenNameIsMissing_thenReturns400() throws Exception {
        String token = registerAndGetToken("badcat@test.com");

        mockMvc.perform(post("/api/v1/categories")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {
                                      "type": "EXPENSE"
                                    }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createCategory_whenTypeIsInvalid_thenReturns400() throws Exception {
        String token = registerAndGetToken("badtype@test.com");

        mockMvc.perform(post("/api/v1/categories")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {
                                      "name": "Food",
                                      "type": "INVALID"
                                    }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getCategories_whenUserHasNoCategories_thenReturnsEmptyList() throws Exception {
        String token = registerAndGetToken("empty@test.com");

        mockMvc.perform(get("/api/v1/categories")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getCategories_whenUserHasCategories_thenReturnsOnlyOwnCategories() throws Exception {
        String token = registerAndGetToken("getcats@test.com");

        createCategory(token, "Salary", Type.INCOME);
        createCategory(token, "Food", Type.EXPENSE);

        mockMvc.perform(get("/api/v1/categories")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].name",
                        containsInAnyOrder("Salary", "Food")));
    }

    @Test
    void getCategories_whenAnotherUserHasCategories_thenTheyAreNotReturned() throws Exception {
        String userToken = registerAndGetToken("user@test.com");
        String otherToken = registerAndGetToken("other@test.com");

        createCategory(otherToken, "Private", Type.EXPENSE);

        mockMvc.perform(get("/api/v1/categories")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void updateCategory_whenCategoryBelongsToUser_thenReturns200() throws Exception {
        String token = registerAndGetToken("update@test.com");

        Long categoryId = createCategory(token, "Food", Type.EXPENSE);

        mockMvc.perform(put("/api/v1/categories/{id}", categoryId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {
                                      "name": "Restaurants",
                                      "type": "EXPENSE"
                                    }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Restaurants"));
    }

    @Test
    void updateCategory_whenRequestBodyIsInvalid_thenReturns400() throws Exception {
        String token = registerAndGetToken("updatebad@test.com");

        Long categoryId = createCategory(token, "Food", Type.EXPENSE);

        mockMvc.perform(put("/api/v1/categories/{id}", categoryId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {
                                      "type": "EXPENSE"
                                    }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateCategory_whenCategoryBelongsToAnotherUser_thenReturns404() throws Exception {
        String ownerToken = registerAndGetToken("owner@test.com");
        String attackerToken = registerAndGetToken("attacker@test.com");

        Long categoryId = createCategory(ownerToken, "Private", Type.EXPENSE);

        mockMvc.perform(put("/api/v1/categories/{id}", categoryId)
                        .header("Authorization", "Bearer " + attackerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {
                                      "name": "Hack",
                                      "type": "EXPENSE"
                                    }
                                """))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteCategory_whenCategoryBelongsToUser_thenReturns204() throws Exception {
        String token = registerAndGetToken("delete@test.com");

        Long categoryId = createCategory(token, "Temp", Type.EXPENSE);

        mockMvc.perform(delete("/api/v1/categories/{id}", categoryId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteCategory_whenCategoryBelongsToAnotherUser_thenReturns404() throws Exception {
        String ownerToken = registerAndGetToken("owner2@test.com");
        String attackerToken = registerAndGetToken("attacker2@test.com");

        Long categoryId = createCategory(ownerToken, "Private", Type.EXPENSE);

        mockMvc.perform(delete("/api/v1/categories/{id}", categoryId)
                        .header("Authorization", "Bearer " + attackerToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteCategory_whenCategoryDoesNotExist_thenReturns404() throws Exception {
        String token = registerAndGetToken("delete404@test.com");

        mockMvc.perform(delete("/api/v1/categories/{id}", 9999)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }
}

