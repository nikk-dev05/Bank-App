# Secure Bank Management System

A full-stack **Banking Web Application** built with **Java**, **Spring Boot**, and **JWT-based authentication**. It allows users to register, log in, view their account details, deposit/withdraw funds, and track transaction history securely.

---

## Features

- User Registration and Login (JWT-secured)  
- Role-based Access Control (User / Admin)  
- Account Dashboard with Balance Info  
- Deposit & Withdraw Functionality  
- Transaction History per Account  
- REST APIs secured using Spring Security  
- CORS handled for frontend integration  
- Clean and responsive frontend using HTML, CSS, JavaScript  

---

## Tech Stack

**Backend:**  
- Java 17  
- Spring Boot  
- Spring Security  
- JWT Authentication  
- MySQL Database  
- Maven  

**Frontend:**  
- HTML, CSS, JavaScript  
- Runs on port 3000 and communicates with backend (port 8081)

**Tools:**  
- Git, GitHub, Postman, STS, VS Code

---

## Project Structure
📁 bank-management-app/
├── src/
│ ├── controller/
│ ├── service/
│ ├── repository/
│ ├── model/
│ └── config/
├── resources/
│ ├── application.properties
├── pom.xml


---

## Authentication Flow

- JWT is generated on login and sent in the Authorization header  
- Spring Security validates and authorizes each request  
- Roles like `ROLE_USER` and `ROLE_ADMIN` are enforced using `@PreAuthorize`

---

## API Endpoints (Sample)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST   | `/auth/register` | Register a new user |
| POST   | `/auth/login` | Login and receive JWT |
| GET    | `/bank` | Get account details (auth required) |
| POST   | `/bank/deposit` | Deposit amount |
| POST   | `/bank/withdraw` | Withdraw amount |
| GET    | `/bank/{id}/transactions` | View transaction history |

---

## How to Run

1. Clone the repository  
   `git clone https://github.com/nikk-dev05/your-repo-name.git`

2. Open in Spring Tool Suite or VS Code with Java Extensions

3. Create MySQL database and update `application.properties`

4. Run the project  
   `mvn spring-boot:run` or click "Run" in STS

5. Run frontend separately if using HTML/JS

---

## Contact

**Nikhil Tiwari**  
📧 nikhil98627@gmail.com  
🔗 [LinkedIn](https://www.linkedin.com/in/nikhil-tiwari-9806222b2)  
🔗 [GitHub](https://github.com/nikk-dev05)

---

##  If you like this project, give it a star!_

