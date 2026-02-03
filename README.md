🚀 SaaS Platform – JWT Authentication Backend (Spring Boot)

A secure backend system built with **Spring Boot, JWT, MySQL and Spring Security** that supports:

* User registration
* Login & JWT token generation
* Secure API access using JWT
* Role-based ready architecture

This project follows **real-world SaaS backend design** used by companies.

---

🛠 Tech Stack

| Layer       | Technology            |
| ----------- | --------------------- |
| Language    | Java 17               |
| Framework   | Spring Boot           |
| Security    | Spring Security + JWT |
| Database    | MySQL                 |
| ORM         | JPA / Hibernate       |
| API Testing | Postman               |
| Build Tool  | Maven                 |

 📂 Project Structure

```
com.rupam.saas
│
├── controller
│   ├── AuthController.java
│   ├── UserController.java
│   └── TestController.java
│
├── dto
│   ├── LoginRequest.java
│   └── RegisterRequest.java
│
├── entity
│   ├── User.java
│   ├── Role.java
│   └── Company.java
│
├── repository
│   ├── UserRepository.java
│   └── RoleRepository.java
│
├── security
│   ├── JwtUtil.java
│   ├── JwtFilter.java
│   └── SecurityConfig.java
│
└── SaasPlatformApplication.java
```

🔐 Authentication Flow

```
Register → Login → JWT Token → Secure API
```

Step-by-step

1. User registers with email & password
2. User logs in
3. Server returns JWT token
4. Client sends token in Authorization header
5. JWT filter validates token
6. Secure endpoints are accessible



🔗 API Endpoints

🧾 Register

```
POST /register
```

Body

```json
{
  "email": "admin@test.com",
  "password": "123456"
}
```

---

🔑 Login

```
POST /login
```

**Body**

```json
{
  "email": "admin@test.com",
  "password": "123456"
}
```

Response**

```text
eyJhbGciOiJIUzI1NiJ9...
```

(This is your JWT token)

🔒 Secure API

```
GET /secure
```

Headers

```
Authorization: Bearer <JWT_TOKEN>
```

Response**

```
Welcome secure user
```

 🛡 How Security Works
 Passwords are stored **encrypted**
 JWT contains the user identity
 Every secure request passes through `JwtFilter`
 If token is invalid → **403 Forbidden**
 If token is valid → request is allowed

This is how production SaaS platforms protect APIs.

🗄 Database

Users are stored in **MySQL**.

Example:

```sql
SELECT * FROM user;
```

Passwords are NOT stored in plain text.

🚀 How to Run

1. Create MySQL database

```
saas_db
```

2. Update `application.properties`

```
spring.datasource.url=jdbc:mysql://localhost:3306/saas_db
spring.datasource.username=root
spring.datasource.password=your_password
```

3. Run project

```
mvn spring-boot:run
```

4. Test using Postman


Author
Rupam Sarangi
Java Backend & SaaS Developer
