# 🏦 Bank Management REST API

This is a secure **Bank Management System** built using **Spring Boot**, with user authentication, password encryption, and a clean RESTful API structure.

---

## 🚀 Features

- ✅ User Registration (`/user/signup`)
- ✅ Secure Login with Password Encryption (`/user/login`)
- ✅ Passwords encrypted using **BCrypt**
- ✅ Input validation using `jakarta.validation`
- ✅ Exception handling with clean HTTP responses

---

## 🛠️ Tech Stack

| Layer        | Technology            |
|--------------|------------------------|
| Language     | Java                   |
| Backend      | Spring Boot            |
| Security     | BCryptPasswordEncoder  |
| Validation   | Jakarta Bean Validation (`@Valid`) |
| Database     | H2 / MySQL (Your Choice) |
| Build Tool   | Maven                  |
| HTTP Client  | Postman / curl         |

---

## 🔐 API Endpoints

### 📥 Register (Sign Up)
```
POST /user/signup
Content-Type: application/json

{
  "name": "nikhil",
  "password": "your_password"
}
```

### 🔓 Login
```
POST /user/login
Content-Type: application/json

{
  "name": "nikhil",
  "password": "your_password"
}
```

---

## 📂 Project Structure

```
src
├── controller        # REST Controllers (Authcontroller)
├── entity            # JPA Entity (User)
├── repository        # Spring Data Repositories
├── services          # Business Logic Layer
└── main              # Application Entry Point
```

---

## 🧪 How to Run

```bash
# 1. Clone the repo
git clone https://github.com/nikk-dev05/Bank-Management-REST-API.git

# 2. Open in IDE (IntelliJ, VS Code, etc.)

# 3. Build and run
mvn clean install
mvn spring-boot:run
```

---

## 📌 To Do (Future Enhancements)

- 🔐 Add JWT Token Authentication
- 👥 Add Role-based Access (admin/user)
- 💾 Connect to MySQL for production
- 🧪 Add Swagger API documentation

---

## 👨‍💻 Author

**Nikhil Tiwari**  
Aspiring Backend Developer | Java + Spring Boot Enthusiast  
[GitHub Profile](https://github.com/nikk-dev05)

---

## 📃 License

This project is licensed under the MIT License.
