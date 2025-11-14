Digital Banking System – Spring Boot (JWT + PostgreSQL)

A complete Digital Banking System built with Spring Boot, featuring secure authentication using JWT, real-time transaction management, and a single-table user-account architecture.
This project demonstrates clean architecture, security best practices, and production-ready backend banking logic.

Features
  🔐 Authentication Module
          User Registration (Username, Email, Password, Full Name, Phone, DOB, Address)
          Role selection: CUSTOMER, MANAGER, ADMIN
          Login with JWT authentication
          Token-based access for all transactions
          Secure password hashing (BCrypt)

          
  🧾 User & Account Management (Single Table)
          Every new registration automatically stores:
          Account Number (auto-generated)
          Account Type: SAVINGS | CURRENT | FIXED_DEPOSIT
          Account Balance (starts from ₹0)
          Account Status
          Role
          Personal details (email, phone, DOB, address)

          
  💸 Transaction Module
      Supports the following operations:
          Deposit
          Withdraw
          Transfer to another account
          Fixed Deposit creation
          Transaction history (saved to transactions table)
      Each transaction stores:
          Transaction ID (UUID)
          Account Number
          Transaction Type (DEPOSIT / WITHDRAWAL / TRANSFER / FIXED_DEPOSIT
          Status (COMPLETED / FAILED)
          Currency (INR)
          Description
          Created timestamp

🛠 Tech Stack
      Layer	      :  Technology
      Backend	    :  Spring Boot 3, Spring Web
      Security    :  Spring Security + JWT
      Database	  :  PostgreSQL
      ORM	        :  Spring Data JPA / Hibernate
      Logging     :  SLF4J + Spring Boot Logging
      Build Tool  :  Maven
      API Testing :  Postman
      Language	  :  Java 17+
    
📁 Project Structure
digital-banking-system/
│
├── src/main/java/com/bank/digital_banking/
│   ├── config/
│   │   ├── SecurityConfig.java
│   │   ├── JwtAuthenticationFilter.java
│   │   ├── CustomUserDetailsService.java
│   │   └── CorsConfig.java
│   │
│   ├── controller/
│   │   ├── AuthController.java
│   │   └── TransactionController.java
│   │
│   ├── dto/  (Request/Response Models)
│   ├── entity/ (User, Transaction)
│   ├── repository/ (JPA Repositories)
│   ├── service/ (UserService, TransactionService)
│   ├── util/ (JwtUtil)
│   └── DigitalBankingApplication.java
│
└── src/main/resources/
    ├── application.properties
    

