# Quick Start Guide - Testing JWT Authentication

## Prerequisites
- MySQL running on localhost:3306
- Database: `taskmanager` (will be created automatically)
- Java 17+
- Maven (or use included wrapper)

## Step 1: Start the Application

```bash
.\mvnw.cmd spring-boot:run
```

The application will start on `http://localhost:9090`

## Step 2: Create First Admin User

Since all endpoints are protected, you need to create an admin user directly in the database:

```sql
-- Connect to MySQL
mysql -u root -p

-- Use the database
USE taskmanager;

-- Insert admin user (password: admin123)
INSERT INTO users (user_id, username, email, password, role) 
VALUES (
    UUID(), 
    'Admin User', 
    'admin@example.com', 
    '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGbVESjrCtKltAvV4o2VqBYjvKkXzi', 
    'ADMIN'
);

-- Insert regular user (password: user123)
INSERT INTO users (user_id, username, email, password, role) 
VALUES (
    UUID(), 
    'Regular User', 
    'user@example.com', 
    '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 
    'USER'
);
```

## Step 3: Login and Get JWT Token

### Using cURL:
```bash
curl -X POST http://localhost:9090/api/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"email\":\"admin@example.com\",\"password\":\"admin123\"}"
```

### Using Postman:
1. Method: POST
2. URL: `http://localhost:9090/api/auth/login`
3. Headers: `Content-Type: application/json`
4. Body (raw JSON):
```json
{
  "email": "admin@example.com",
  "password": "admin123"
}
```

### Expected Response:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiQURNSU4iLCJzdWIiOiJhZG1pbkBleGFtcGxlLmNvbSIsImlhdCI6MTY5ODk5OTk5OSwiZXhwIjoxNjk5MDg2Mzk5fQ.xyz...",
  "username": "Admin User",
  "email": "admin@example.com",
  "role": "ADMIN"
}
```

**Copy the token value!**

## Step 4: Test Protected Endpoints

### Create a Task (ADMIN only)

```bash
curl -X POST http://localhost:9090/api/tasks ^
  -H "Content-Type: application/json" ^
  -H "Authorization: Bearer YOUR_TOKEN_HERE" ^
  -d "{\"title\":\"Complete Project\",\"description\":\"Finish the task manager\",\"priority\":\"HIGH\",\"status\":\"PENDING\",\"dueDate\":\"2025-12-31\"}"
```

### Get All Tasks with Pagination

```bash
curl -X GET "http://localhost:9090/api/tasks?page=0&size=10&sortBy=dueDate" ^
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

### Create a New User (ADMIN only)

```bash
curl -X POST http://localhost:9090/api/users ^
  -H "Content-Type: application/json" ^
  -H "Authorization: Bearer YOUR_TOKEN_HERE" ^
  -d "{\"username\":\"John Doe\",\"email\":\"john@example.com\",\"password\":\"john123\",\"role\":\"USER\"}"
```

### Assign Task to User (ADMIN only)

```bash
curl -X POST http://localhost:9090/api/tasks/{taskId}/assign/{userId} ^
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

### Get All Users (ADMIN only)

```bash
curl -X GET http://localhost:9090/api/users ^
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

## Step 5: Test Regular User Access

Login as regular user:
```bash
curl -X POST http://localhost:9090/api/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"email\":\"user@example.com\",\"password\":\"user123\"}"
```

Try to access tasks (should work):
```bash
curl -X GET http://localhost:9090/api/tasks ^
  -H "Authorization: Bearer USER_TOKEN_HERE"
```

Try to create a task (should fail - ADMIN only):
```bash
curl -X POST http://localhost:9090/api/tasks ^
  -H "Content-Type: application/json" ^
  -H "Authorization: Bearer USER_TOKEN_HERE" ^
  -d "{\"title\":\"Test\",\"description\":\"Test\",\"priority\":\"LOW\",\"status\":\"PENDING\",\"dueDate\":\"2025-12-31\"}"
```

## Common Test Scenarios

### 1. Test Without Token (Should Fail - 401 Unauthorized)
```bash
curl -X GET http://localhost:9090/api/tasks
```

### 2. Test With Invalid Token (Should Fail - 403 Forbidden)
```bash
curl -X GET http://localhost:9090/api/tasks ^
  -H "Authorization: Bearer invalid_token_here"
```

### 3. Test Token Expiration
- Wait 24 hours (or change `jwt.expiration` to a smaller value for testing)
- Try to use the old token
- Should receive 403 Forbidden

### 4. Test Pagination
```bash
# First page with 5 items
curl -X GET "http://localhost:9090/api/tasks?page=0&size=5" ^
  -H "Authorization: Bearer YOUR_TOKEN_HERE"

# Second page with 5 items
curl -X GET "http://localhost:9090/api/tasks?page=1&size=5" ^
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

### 5. Test Filtering and Sorting
```bash
# Filter by status and sort by due date
curl -X GET "http://localhost:9090/api/tasks?status=PENDING&sortBy=dueDate" ^
  -H "Authorization: Bearer YOUR_TOKEN_HERE"

# Filter by priority
curl -X GET "http://localhost:9090/api/tasks?priority=HIGH" ^
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

## Postman Collection Setup

1. Create a new environment with variable:
   - Key: `jwt_token`
   - Value: (will be set automatically)

2. In Login request, add to Tests tab:
```javascript
var jsonData = pm.response.json();
pm.environment.set("jwt_token", jsonData.token);
```

3. In other requests, use Authorization:
   - Type: Bearer Token
   - Token: `{{jwt_token}}`

## Troubleshooting

### Issue: "Could not authenticate"
- Check email and password are correct
- Ensure user exists in database
- Verify password is BCrypt encoded

### Issue: "403 Forbidden"
- Check token is valid and not expired
- Ensure Bearer prefix is included: `Bearer <token>`
- Verify user has required role for the endpoint

### Issue: "No beans of 'AuthenticationManager' type found"
- This is an IDE warning only
- The bean is created at runtime in SecurityConfig
- Application will work correctly

### Issue: Database connection error
- Ensure MySQL is running
- Check credentials in application.properties
- Verify database exists or `createDatabaseIfNotExist=true` is set

## Endpoints Summary

| Endpoint | Method | Role | Description |
|----------|--------|------|-------------|
| /api/auth/login | POST | Public | Login and get JWT token |
| /api/users | POST | ADMIN | Create user |
| /api/users | GET | ADMIN | Get all users |
| /api/users/{id} | GET | ADMIN | Get user by ID |
| /api/users/{id} | PUT | ADMIN | Update user |
| /api/users/{id} | DELETE | ADMIN | Delete user |
| /api/tasks | POST | ADMIN | Create task |
| /api/tasks | GET | ADMIN/USER | Get all tasks (paginated) |
| /api/tasks/{id} | GET | ADMIN/USER | Get task by ID |
| /api/tasks/{id} | PUT | ADMIN/USER | Update task |
| /api/tasks/{id} | DELETE | ADMIN | Delete task |
| /api/tasks/{taskId}/assign/{userId} | POST | ADMIN | Assign task to user |

## Next Steps

1. Test all CRUD operations with both ADMIN and USER roles
2. Verify pagination and sorting work correctly
3. Test JWT token expiration handling
4. Add custom exception handlers for better error messages
5. Consider adding refresh token functionality
6. Add API documentation with Swagger/OpenAPI

