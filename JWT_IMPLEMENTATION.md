# Task Manager 03 - JWT Authentication Implementation

## Completed Features ✅

### 8. Pagination and Sorting ✅
- Implemented in `TaskController.getTasks()` endpoint
- Supports pagination with configurable page size and page number
- Supports sorting by any field (default: "id")
- Query parameters:
  - `page` (default: 0)
  - `size` (default: 5)
  - `sortBy` (default: "id")
  - `status`, `priority`, `dueDate` for filtering

**Example Request:**
```
GET /api/tasks?page=0&size=10&sortBy=dueDate&status=PENDING
```

### 9. MapStruct DTO Mapping ✅
- MapStruct dependencies added to `pom.xml`
- MapStruct annotation processor configured in Maven compiler plugin
- `TaskMapper` interface created for Task entity-DTO mapping
- `UserMapper` interface created for User entity-DTO mapping
- Automatic mapping between entities and DTOs at compile time

**Files:**
- `TaskMapper.java` - Maps Task entity ↔ TaskDTO
- `UserMapper.java` - Maps User entity ↔ UserDTO

### 10. Spring Security with BCrypt Password Encoding ✅
- `BCryptPasswordEncoder` bean configured in `SecurityConfig`
- Passwords are automatically encoded when creating/updating users
- Used in `UserServiceImpl` for secure password storage

### 11. JWT-Based Authentication ✅

#### Components Implemented:

##### A. JWT Utility Class (`JwtUtil.java`)
- Token generation with username and role
- Token validation
- Claims extraction (username, expiration, etc.)
- Configurable secret key and expiration time
- Uses JJWT library (version 0.12.3)

##### B. JWT Authentication Filter (`JwtAuthenticationFilter.java`)
- Extends `OncePerRequestFilter`
- Intercepts all requests to validate JWT tokens
- Extracts JWT from `Authorization` header (Bearer token)
- Sets authentication in SecurityContext if token is valid
- Integrated into Spring Security filter chain

##### C. Custom UserDetailsService (`CustomUserDetailsService.java`)
- Loads user details from database by email
- Converts User entity to Spring Security UserDetails
- Used by AuthenticationManager for authentication

##### D. Login Endpoint (`AuthController.java`)
- `POST /api/auth/login` - Public endpoint for authentication
- Accepts email and password
- Returns JWT token and user information
- Handles authentication errors gracefully

**Request Body:**
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "John Doe",
  "email": "user@example.com",
  "role": "ADMIN"
}
```

##### E. Security Configuration (`SecurityConfig.java`)
- JWT filter added before UsernamePasswordAuthenticationFilter
- CSRF disabled (stateless JWT authentication)
- Session management set to STATELESS
- `/api/auth/**` endpoints are public (permitAll)
- All other endpoints require authentication
- Method-level security enabled with `@EnableMethodSecurity`
- AuthenticationManager bean configured

##### F. Application Properties
- `jwt.secret` - Secret key for JWT signing
- `jwt.expiration` - Token expiration time (default: 24 hours)

## Dependencies Added

```xml
<!-- JWT Dependencies -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.3</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.3</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.3</version>
    <scope>runtime</scope>
</dependency>

<!-- MapStruct -->
<dependency>
    <groupId>org.mapstruct</groupId>
    <artifactId>mapstruct</artifactId>
    <version>1.5.5.Final</version>
</dependency>
```

## API Endpoints

### Public Endpoints
- `POST /api/auth/login` - Authenticate and receive JWT token

### Protected Endpoints (Require JWT Token)

#### User Management (ADMIN only)
- `POST /api/users` - Create user
- `GET /api/users` - Get all users
- `GET /api/users/{uuid}` - Get user by ID
- `PUT /api/users/{uuid}` - Update user
- `DELETE /api/users/{uuid}` - Delete user

#### Task Management
- `POST /api/tasks` - Create task (ADMIN only)
- `GET /api/tasks` - Get all tasks with pagination (ADMIN or USER)
- `GET /api/tasks/{uuid}` - Get task by ID (ADMIN or USER)
- `PUT /api/tasks/{uuid}` - Update task (ADMIN or USER)
- `DELETE /api/tasks/{uuid}` - Delete task (ADMIN only)
- `POST /api/tasks/{taskId}/assign/{userId}` - Assign task to user (ADMIN only)

## How to Use JWT Authentication

### 1. Login to Get Token
```bash
curl -X POST http://localhost:9090/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@example.com",
    "password": "admin123"
  }'
```

### 2. Use Token in Subsequent Requests
```bash
curl -X GET http://localhost:9090/api/tasks \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE"
```

## Security Flow

1. **User Login:**
   - User sends credentials to `/api/auth/login`
   - AuthenticationManager validates credentials
   - If valid, JWT token is generated and returned

2. **Accessing Protected Resources:**
   - Client includes JWT in Authorization header: `Bearer <token>`
   - JwtAuthenticationFilter intercepts the request
   - Token is validated and user details are extracted
   - Spring Security context is populated with user authentication
   - Request proceeds to controller with authenticated user

3. **Authorization:**
   - Controller methods use `@PreAuthorize` annotations
   - Spring Security checks if authenticated user has required role
   - Access granted or denied based on role

## Configuration

### application.properties
```properties
# JWT Configuration
jwt.secret=2D4A614E645267556B58703273357638792F423F4428472B4B6250655368566DF423F4428472B4B6250655368566D
jwt.expiration=86400000

# Server Port
server.port=9090

# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/taskmanager?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=harshima@147
spring.jpa.hibernate.ddl-auto=update
```

## Testing the Implementation

1. **Start the Application:**
   ```bash
   .\mvnw.cmd spring-boot:run
   ```

2. **Create a User (if not exists):**
   First, you need to temporarily allow public access to create the first admin user, or manually insert into database.

3. **Login:**
   ```bash
   curl -X POST http://localhost:9090/api/auth/login \
     -H "Content-Type: application/json" \
     -d '{"email":"admin@example.com","password":"admin123"}'
   ```

4. **Copy the token from response**

5. **Access Protected Endpoint:**
   ```bash
   curl -X GET http://localhost:9090/api/tasks?page=0&size=10 \
     -H "Authorization: Bearer YOUR_TOKEN_HERE"
   ```

## Repository Method Updates

Fixed repository methods to use correct field names:
- `TaskRepository.findByTaskId()` (was findByUuid)
- `UserRepository.findByUserId()` (was findByUuid)
- `TaskRepository.findByAssignedUserUserId()` (was findByAssignedUserUuid)

## Build Status

✅ Maven build successful
✅ All dependencies resolved
✅ MapStruct processors configured
✅ Lombok and MapStruct working together
✅ No compilation errors

## Notes

- JWT tokens expire after 24 hours (configurable via `jwt.expiration`)
- The secret key should be changed in production and stored securely
- Password encoding uses BCrypt with default strength
- Session management is stateless (no server-side sessions)
- All exceptions are handled gracefully with appropriate HTTP status codes

