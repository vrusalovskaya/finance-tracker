package com.example.finance_tracker.services;

import com.example.finance_tracker.common.Type;
import com.example.finance_tracker.entities.CategoryEntity;
import com.example.finance_tracker.entities.TransactionEntity;
import com.example.finance_tracker.entities.UserEntity;
import com.example.finance_tracker.repositories.CategoryRepository;
import com.example.finance_tracker.repositories.TransactionRepository;
import com.example.finance_tracker.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeedService {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;
    private final PasswordEncoder passwordEncoder;

    public void seed() {
        if (userRepository.count() > 0) {
            return;
        }

        UserEntity user = createUser();
        List<CategoryEntity> categories = createCategories(user);
        createTransactions(user, categories);
    }

    // ---------------- USERS ----------------

    private UserEntity createUser() {
        UserEntity user = new UserEntity();
        user.setUserName("john_doe");
        user.setEmail("john@example.com");
        user.setPassword(passwordEncoder.encode("password123"));

        return userRepository.save(user);
    }

    // ---------------- CATEGORIES ----------------

    private List<CategoryEntity> createCategories(UserEntity user) {
        List<CategoryEntity> categories = List.of(
                category("Salary", Type.INCOME, "Monthly salary", user),
                category("Freelance", Type.INCOME, "Side projects", user),

                category("Groceries", Type.EXPENSE, "Food & supermarkets", user),
                category("Rent", Type.EXPENSE, "Monthly rent", user),
                category("Transport", Type.EXPENSE, "Public transport & fuel", user),
                category("Entertainment", Type.EXPENSE, "Movies, games, fun", user)
        );

        return categoryRepository.saveAll(categories);
    }

    private CategoryEntity category(
            String name,
            Type type,
            String description,
            UserEntity user
    ) {
        CategoryEntity c = new CategoryEntity();
        c.setName(name);
        c.setType(type);
        c.setDescription(description);
        c.setUserEntity(user);
        return c;
    }

    // ---------------- TRANSACTIONS ----------------

    private void createTransactions(UserEntity user, List<CategoryEntity> categories) {

        Map<String, CategoryEntity> map = categories.stream()
                .collect(Collectors.toMap(CategoryEntity::getName, c -> c));

        CategoryEntity salary = map.get("Salary");
        CategoryEntity freelance = map.get("Freelance");
        CategoryEntity groceries = map.get("Groceries");
        CategoryEntity rent = map.get("Rent");
        CategoryEntity transport = map.get("Transport");
        CategoryEntity entertainment = map.get("Entertainment");

        for (int i = 1; i < 5; i++) {
            YearMonth month = YearMonth.now().minusMonths(i);

            saveTransaction(
                    Type.INCOME,
                    new BigDecimal("3000"),
                    month.atDay(1),
                    "Salary for " + month,
                    salary,
                    user
            );

            saveTransaction(
                    Type.INCOME,
                    new BigDecimal("800"),
                    month.atDay(15),
                    "Freelance work",
                    freelance,
                    user
            );

            saveTransaction(
                    Type.EXPENSE,
                    new BigDecimal("1200"),
                    month.atDay(5),
                    "Apartment rent",
                    rent,
                    user
            );

            saveTransaction(
                    Type.EXPENSE,
                    new BigDecimal("350"),
                    month.atDay(10),
                    "Groceries",
                    groceries,
                    user
            );

            saveTransaction(
                    Type.EXPENSE,
                    new BigDecimal("120"),
                    month.atDay(18),
                    "Transport",
                    transport,
                    user
            );

            saveTransaction(
                    Type.EXPENSE,
                    new BigDecimal("200"),
                    month.atDay(22),
                    "Entertainment",
                    entertainment,
                    user
            );
        }
    }

    private void saveTransaction(
            Type type,
            BigDecimal amount,
            LocalDate date,
            String description,
            CategoryEntity category,
            UserEntity user
    ) {
        TransactionEntity t = new TransactionEntity();
        t.setType(type);
        t.setAmount(amount);
        t.setDate(date);
        t.setDescription(description);
        t.setCategoryEntity(category);
        t.setUserEntity(user);

        transactionRepository.save(t);
    }
}
