This project is a Spring Boot REST API application that handles user authentication, password recovery, and movie management with secure admin operations.

Main Features:
*User Authentication
    -User registration with secure password storage
    -Login with JWT-based authentication
    -Token refresh mechanism for seamless user sessions
    
*Forgot Password Workflow
    -Sends OTP (one-time password) to user email for verification
    -Validates OTP to authorize password reset
    -Allows users to change their password securely
*Movie Management (Admin only) -Add new movies with poster image upload -View movie details by ID or get all movies -Update movie details and optionally update poster image -Delete movies

#Technologies Used:

   Spring Boot (Java)
   JWT for authentication
   Spring Security
   Email service for OTP handling
   Swagger/OpenAPI for API documentation
   Multipart file handling for image uploads
