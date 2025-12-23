package com.example.finance_tracker.controllers;

import com.example.finance_tracker.BaseE2ETest;
import com.example.finance_tracker.common.Type;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TransactionControllerTest extends BaseE2ETest {

    @Test
    void accessTransactions_whenJwtIsMissing_thenReturns401() throws Exception {
        mockMvc.perform(get("/api/v1/transactions"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createTransaction_whenValidRequest_thenReturns201() throws Exception {
        String token = registerAndGetToken("tx-create@test.com");

        Long categoryId = createCategory(token, "Food", Type.EXPENSE);

        mockMvc.perform(post("/api/v1/transactions")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "type": "EXPENSE",
                              "amount": 50.25,
                              "date": "%s",
                              "description": "Groceries",
                              "categoryId": %d
                            }
                        """.formatted(LocalDate.now(), categoryId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.amount").value(50.25))
                .andExpect(jsonPath("$.type").value("EXPENSE"))
                .andExpect(jsonPath("$.categoryId").value(categoryId));
    }

    @Test
    void createTransaction_whenAmountIsNegative_thenReturns400() throws Exception {
        String token = registerAndGetToken("tx-neg@test.com");
        Long categoryId = createCategory(token, "Food", Type.EXPENSE);

        mockMvc.perform(post("/api/v1/transactions")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "type": "EXPENSE",
                              "amount": -10,
                              "date": "%s",
                              "categoryId": %d
                            }
                        """.formatted(LocalDate.now(), categoryId)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createTransaction_whenDateIsInFuture_thenReturns400() throws Exception {
        String token = registerAndGetToken("tx-future@test.com");
        Long categoryId = createCategory(token, "Food", Type.EXPENSE);

        mockMvc.perform(post("/api/v1/transactions")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "type": "EXPENSE",
                              "amount": 10,
                              "date": "%s",
                              "categoryId": %d
                            }
                        """.formatted(LocalDate.now().plusDays(1), categoryId)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getTransactions_whenMultipleExist_thenReturnsAllUserTransactions() throws Exception {
        String token = registerAndGetToken("tx-all@test.com");

        Long category = createCategory(token, "Food", Type.EXPENSE);

        createTransaction(token, category, 10, Type.EXPENSE);
        createTransaction(token, category, 20, Type.EXPENSE);

        mockMvc.perform(get("/api/v1/transactions")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void getTransactionsByType_whenMixedTypesExist_thenReturnsOnlyRequestedType() throws Exception {
        String token = registerAndGetToken("tx-type-full@test.com");

        Long expenseCategory = createCategory(token, "Food", Type.EXPENSE);
        Long incomeCategory = createCategory(token, "Salary", Type.INCOME);

        createTransaction(token, expenseCategory, 20, Type.EXPENSE);
        createTransaction(token, incomeCategory, 100, Type.INCOME);

        mockMvc.perform(get("/api/v1/transactions/type/EXPENSE")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].type").value("EXPENSE"))
                .andExpect(jsonPath("$[0].categoryId").value(expenseCategory));
    }

    @Test
    void getTransactionsByCategory_whenMultipleCategoriesExist_thenReturnsOnlyMatchingCategory() throws Exception {
        String token = registerAndGetToken("tx-cat-full@test.com");

        Long foodCategory = createCategory(token, "Food", Type.EXPENSE);
        Long transportCategory = createCategory(token, "Transport", Type.EXPENSE);

        createTransaction(token, foodCategory, 30, Type.EXPENSE);
        createTransaction(token, transportCategory, 15, Type.EXPENSE);

        mockMvc.perform(get("/api/v1/transactions/category/{id}", foodCategory)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].categoryId").value(foodCategory));
    }

    @Test
    void getTransactionsByCategory_whenCategoryBelongsToAnotherUser_thenReturnsEmptyList() throws Exception {
        String ownerToken = registerAndGetToken("tx-owner2@test.com");
        String attackerToken = registerAndGetToken("tx-attacker2@test.com");

        Long ownerCategory = createCategory(ownerToken, "Private", Type.EXPENSE);
        createTransaction(ownerToken, ownerCategory, 50, Type.EXPENSE);

        mockMvc.perform(get("/api/v1/transactions/category/{id}", ownerCategory)
                        .header("Authorization", "Bearer " + attackerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }


    @Test
    void getTransactionsByDateRange_whenRangeIsValid_thenReturnsList() throws Exception {
        String token = registerAndGetToken("tx-date@test.com");
        Long categoryId = createCategory(token, "Food", Type.EXPENSE);

        createTransaction(token, categoryId, 40, Type.EXPENSE);

        mockMvc.perform(get("/api/v1/transactions/dates")
                        .header("Authorization", "Bearer " + token)
                        .param("startDate", LocalDate.now().minusDays(1).toString())
                        .param("endDate", LocalDate.now().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void getTransactionsByDateRange_whenNoTransactionsInRange_thenReturnsEmptyList() throws Exception {
        String token = registerAndGetToken("tx-date-empty@test.com");

        Long category = createCategory(token, "Food", Type.EXPENSE);
        createTransaction(token, category, 10, Type.EXPENSE);

        mockMvc.perform(get("/api/v1/transactions/dates")
                        .header("Authorization", "Bearer " + token)
                        .param("startDate", LocalDate.now().minusDays(10).toString())
                        .param("endDate", LocalDate.now().minusDays(5).toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void updateTransaction_whenTransactionBelongsToUser_thenReturns200() throws Exception {
        String token = registerAndGetToken("tx-update@test.com");
        Long categoryId = createCategory(token, "Food", Type.EXPENSE);

        Long txId = createTransaction(token, categoryId, 15, Type.EXPENSE);

        mockMvc.perform(put("/api/v1/transactions/{id}", txId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "type": "EXPENSE",
                              "amount": 25,
                              "date": "%s",
                              "categoryId": %d
                            }
                        """.formatted(LocalDate.now(), categoryId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(25));
    }

    @Test
    void updateTransaction_whenTransactionBelongsToAnotherUser_thenReturns404() throws Exception {
        String ownerToken = registerAndGetToken("tx-owner@test.com");
        String attackerToken = registerAndGetToken("tx-attacker@test.com");

        Long categoryId = createCategory(ownerToken, "Private", Type.EXPENSE);
        Long txId = createTransaction(ownerToken, categoryId, 99, Type.EXPENSE);

        mockMvc.perform(put("/api/v1/transactions/{id}", txId)
                        .header("Authorization", "Bearer " + attackerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "type": "EXPENSE",
                              "amount": 1,
                              "date": "%s"
                            }
                        """.formatted(LocalDate.now())))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteTransaction_whenTransactionBelongsToUser_thenReturns204() throws Exception {
        String token = registerAndGetToken("tx-delete@test.com");
        Long categoryId = createCategory(token, "Food", Type.EXPENSE);

        Long txId = createTransaction(token, categoryId, 10, Type.EXPENSE);

        mockMvc.perform(delete("/api/v1/transactions/{id}", txId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteTransaction_whenTransactionDoesNotExist_thenReturns404() throws Exception {
        String token = registerAndGetToken("tx-delete404@test.com");

        mockMvc.perform(delete("/api/v1/transactions/{id}", 9999)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }
}

