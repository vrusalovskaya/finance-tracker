# 💰 Finance Tracker API

A backend REST API for personal finance tracking built with **Java 21** and **Spring Boot**.  
The application allows users to manage income and expense categories, record transactions, generate financial reports, and **export data in PDF and CSV formats**.

---

## 🚀 Features

- User registration and authentication using JWT
- Secure access to user-specific financial data
- CRUD operations for income and expense categories
- Income and expense transactions
- Financial reports:
  - Period summary (income, expense, balance)
- Data export in multiple formats
  - PDF export
  - CSV export
- Global exception handling
- End-to-End testing with MockMvc

---

## 🛠 Tech Stack

- **Java 21**
- **Spring Boot 4**
- **Spring Security**
- **Spring Data JPA**
- **JWT (JSON Web Tokens)**
- **Hibernate**
- **PostgreSQL**
- **Maven**
- **JUnit 5**
- **MockMvc**

---

## 📐 Architecture

The project follows a layered architecture with clear separation of concerns:

`controller → service → repository → database`

Core principles:
- Controllers are thin and contain no business logic
- Services encapsulate business rules
- Repositories handle data access
- DTOs are used for API communication
- Export logic is isolated from business logic

---

## 🔐 Security

- Stateless authentication using JWT
- Token is passed via `Authorization: Bearer <token>` header
- User identity is extracted from `SecurityContext`
- User ID is never passed from the client
- Users cannot access or export other users’ data
- Passwords are stored in encoded form

---

## 📦 Main Modules

- **Auth**
  - User registration
  - Login and JWT token generation
- **Category**
  - Income and expense categories
  - User-scoped access
- **Transaction**
  - Financial transactions
  - Balance calculation
- **Reports**
  - Aggregated financial data
- **Export**
  - PDF and CSV export functionality

---

## 📤 Export Feature

The application supports **exporting financial reports in PDF and CSV formats**.

### Purpose
- Allow users to download and store their financial data
- Separate data representation from business logic
- Demonstrate extensible backend design

### Supported Formats
- **PDF** – human-readable report format
- **CSV** – machine-readable format for spreadsheets and further analysis
  
---

## 🧪 Testing

The project includes **End-to-End** tests using `MockMvc`:
- Real HTTP requests
- JWT authentication in tests
- Database interaction verification
- Positive and negative scenarios

Tests ensure:
- Security restrictions are enforced
- Users cannot access or export other users' data
- Report and export calculations are correct

---

## ⚙️ Configuration

### Application Properties

The project uses **`application.properties`** for configuration.

- `application.properties` – default configuration (committed to GitHub)
- `application-test.properties` – test environment (**not pushed to GitHub**)

Example configuration (`application.properties`):

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/finance_tracker
spring.datasource.username=postgres
spring.datasource.password=postgres

spring.jpa.hibernate.ddl-auto=validate
```

---
## ▶️ Running the Application

### 1. Clone the repository
```bash
git clone https://github.com/your-username/finance-tracker.git
cd finance-tracker
```

### 2. Start MySQL

Ensure MySQL is running and the database exists.

### 3. Run the application
```bash
mvn spring-boot:run
```
The API will be available at:
```bash
http://localhost:8080
```

---

## 📌 API Endpoints

### 🔐 Authentication
- `POST /api/v1/auth/register` — Register a new user
- `POST /api/v1/auth/login` — Authenticate user and receive JWT token

---

### 📂 Categories
- `POST /api/v1/categories` — Create a new category
- `PUT /api/v1/categories/{id}` — Update an existing category
- `DELETE /api/v1/categories/{id}` — Delete a category
- `GET /api/v1/categories` — Get all categories of the authenticated user

---

### 💸 Transactions
- `POST /api/v1/transactions` — Create a new transaction
- `PUT /api/v1/transactions/{id}` — Update an existing transaction
- `DELETE /api/v1/transactions/{id}` — Delete a transaction
- `GET /api/v1/transactions` — Get all transactions of the authenticated user
- `GET /api/v1/transactions/type/{type}` — Get transactions by type (`INCOME` / `EXPENSE`)
- `GET /api/v1/transactions/category/{categoryId}` — Get transactions by category
- `GET /api/v1/transactions/dates` — Get transactions within a date range  
  - Query params: `startDate`, `endDate` (ISO format)

---

### 📊 Reports
- `GET /api/v1/reports/monthly-summary` — Get monthly income, expense, and balance  
  - Query param: `month` (`yyyy-MM`)
- `GET /api/v1/reports/monthly-category-summary` — Get monthly summary grouped by category  
  - Query params: `month` (`yyyy-MM`), `type`
- `GET /api/v1/reports/period-summary` — Get summary for a custom period  
  - Query params: `startDate`, `endDate` (ISO format)
- `GET /api/v1/reports/monthly-trend` — Get monthly trend data  
  - Query params: `from` (`yyyy-MM`), `to` (`yyyy-MM`), `type`

---

### 📤 Export
- `GET /api/v1/export` — Export financial data  
  - Query params:
    - `format` (`PDF`, `CSV`)
    - `startDate` (optional, ISO date)
    - `endDate` (optional, ISO date)
   
