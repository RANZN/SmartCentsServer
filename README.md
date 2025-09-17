SmartCents Ktor Server API
This repository contains the backend server for the SmartCents service, built with Ktor. It provides a RESTful API for user authentication, including user registration and login.

🚀 Deployment
The service is continuously deployed and hosted on Render.

Production Base URL: https://ranjan-smartcents.onrender.com

📌 API Endpoints
All endpoints are relative to the base URL.

Authentication
🔹 Sign Up a New User
Creates a new user account in the database.

Endpoint: POST /api/auth/signup

Request Body (application/json):

{
  "name": "John Doe",
  "email": "john.doe@example.com",
  "password": "a-strong-password-123"
}

Success Response (201 Created):

{
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "user": {
        "id": "c7a8b2f1-c3e4-4a56-b789-01d2e3f4a5b6",
        "name": "John Doe",
        "email": "john.doe@example.com"
    }
}

Curl Example:

curl -X POST [https://ranjan-smartcents.onrender.com/api/auth/signup](https://ranjan-smartcents.onrender.com/api/auth/signup) \
-H "Content-Type: application/json" \
-d '{
  "name": "John Doe",
  "email": "john.doe@example.com",
  "password": "a-strong-password-123"
}'

🔹 Log In an Existing User
Authenticates a user and returns a JWT for accessing protected routes.

Endpoint: POST /api/auth/login

Request Body (application/json):

{
  "email": "john.doe@example.com",
  "password": "a-strong-password-123"
}

Success Response (200 OK):

{
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "user": {
        "id": "c7a8b2f1-c3e4-4a56-b789-01d2e3f4a5b6",
        "name": "John Doe",
        "email": "john.doe@example.com"
    }
}

Curl Example:

curl -X POST [https://ranjan-smartcents.onrender.com/api/auth/login](https://ranjan-smartcents.onrender.com/api/auth/login) \
-H "Content-Type: application/json" \
-d '{
  "email": "john.doe@example.com",
  "password": "a-strong-password-123"
}'

🛠️ Local Development
To run the server on your local machine for development and testing, follow these steps.

Clone the repository:

git clone [https://github.com/your-username/smartcents-server.git](https://github.com/your-username/smartcents-server.git)
cd smartcents-server

Build and Run the Server:
The project is configured to use a local H2 file-based database by default when no environment variables are set.

./gradlew run

Access the API Locally:
The server will be available at http://localhost:8080.

💻 Technology Stack
Framework: Ktor

Language: Kotlin

Database: PostgreSQL (Production), H2 (Local)

Database Library: Exposed

Authentication: JWT

Dependency Injection: Koin

Build Tool: Gradle

📄 License
This project is licensed under the MIT License.
