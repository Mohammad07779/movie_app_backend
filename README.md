# 🎬 Movie Management REST API

This project is a Spring Boot REST API application that handles user authentication, password recovery, and secure movie management with admin-only operations.

---

## 🚀 Main Features

### 🔐 User Authentication
- User registration with secure password hashing
- Login using JWT-based authentication
- Token refresh mechanism for seamless sessions

### ✉️ Forgot Password Workflow
- Sends OTP (One-Time Password) to user email
- Validates OTP before allowing password reset
- Securely updates user password

### 🎞️ Movie Management (Admin Only)
- Add new movies with poster image upload
- View movie details by ID or get all movies
- Update movie details and optionally update the poster
- Delete movies securely

---

## 🛠️ Technologies Used

- **Spring Boot (Java)**
- **Spring Security**
- **JWT (JSON Web Token)** – for authentication
- **JavaMailSender** – for sending OTP emails
- **Multipart File Handling** – for image uploads
- **Swagger / OpenAPI** – for interactive API documentation
- **MySQL** – as the relational database
