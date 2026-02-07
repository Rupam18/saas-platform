# 🚀 SaaS Platform – Multi-Tenant Task Management System

A full-stack SaaS platform built with **Spring Boot**, **Spring Security**, **JWT**, and **PostgreSQL** that supports secure authentication, company-based multi-tenancy, and team task management with an interactive dashboard.

This project demonstrates a production-style backend architecture with role-based access control, REST APIs, Docker deployment, and a modern frontend.

---

# 📌 Features

## 🔐 Authentication & Security

* User registration & login using JWT
* Secure API access with Spring Security
* Password encryption
* Role-based access structure (Admin/User)

## 🏢 Multi-Tenant Architecture

* Users belong to companies/organizations
* Data structured for SaaS-style scalability
* Company-level user grouping

## 📋 Task Management

* Create and manage tasks
* Track task status:

  * Completed
  * In Progress
  * Pending
* Dashboard visualization for task distribution

## 📊 Dashboard

* Activity overview (last 7 days)
* Task status chart
* Search & filter tasks

## ☁️ Deployment Ready

* Dockerized backend
* Deployable on Render
* Frontend hosted on Vercel

---

# 🧱 Tech Stack

## Backend

* Java 17
* Spring Boot
* Spring Security
* JWT Authentication
* Spring Data JPA
* Hibernate
* Maven

## Frontend

* Vite
* JavaScript
* HTML5
* CSS3
* Fetch API

## Database

* PostgreSQL

## DevOps

* Docker
* Docker Compose
* Render (Backend Hosting)
* Vercel (Frontend Hosting)

---

# 🏗️ Project Architecture

```
Controller → Service → Repository → Database
            ↓
         Security (JWT)
```

### Layers

* **Controller** – Handles API requests
* **Service** – Business logic
* **Repository** – Database interaction
* **Security** – JWT validation & access control

---

# 📂 Folder Structure

```
saas-platform/
│
├── backend/
│   ├── controller/
│   ├── service/
│   ├── entity/
│   ├── repository/
│   ├── security/
│   └── dto/
│
├── frontend/
│   ├── src/
│   └── public/
│
├── Dockerfile
├── docker-compose.yml
└── pom.xml
```

---

# 🔐 Authentication Flow

### Register

```
POST /api/auth/register
```

* Creates company
* Creates user
* Encrypts password
* Assigns role

### Login

```
POST /api/auth/login
```

* Validates credentials
* Generates JWT token
* Returns token

### Access Protected APIs

```
Authorization: Bearer <token>
```

---

# 🗄️ Database Design

### Tables

* users
* roles
* companies
* tasks

### Relationships

* User → belongs to → Company
* User → has → Role
* Company → has → Multiple Users

---

# ⚙️ Local Setup Instructions

## 1️⃣ Clone the Repository

```bash
git clone https://github.com/Rupam18/saas-platform.git
cd saas-platform
```

## 2️⃣ Backend Setup

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

## 3️⃣ Database Setup

Update `application.properties`:

```
spring.datasource.url=jdbc:postgresql://localhost:5432/saas_db
spring.datasource.username=your_username
spring.datasource.password=your_password
```

## 4️⃣ Frontend Setup

```bash
cd frontend
npm install
npm run dev
```

---

# 🐳 Run Using Docker

```bash
docker-compose up --build
```

---

# 🌍 Deployment

* **Backend:** Render
* **Frontend:** Vercel
* **Database:** PostgreSQL (Cloud/Local)

---

# 📈 Future Improvements

* Refresh token support
* Email verification
* Password reset
* Role-based API restrictions
* API documentation (Swagger)
* Notifications system

---

# 👨‍💻 Author

**Rupam Sarangi**

* Built as a full-stack SaaS architecture learning project
* Focused on security, scalability, and clean backend design

---

# ⭐ Project Highlights

* Production-style Spring Boot architecture
* JWT-based authentication & authorization
* Multi-tenant SaaS-ready data model
* Dockerized deployment
* Interactive dashboard UI

---

# 📜 License

This project is open-source and available under the MIT License.
