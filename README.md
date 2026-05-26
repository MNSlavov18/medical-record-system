# Medical Record System

Medical Record System is a Spring Boot web application for managing patients, doctors, examinations, diagnoses, treatments, sick leaves, and health insurance statuses.

The system supports role-based access for administrators, doctors, and patients. It includes a Thymeleaf web interface, REST API endpoints, Swagger documentation, validation, exception handling, Spring Security, and automated tests.

---

## Technologies Used

- Java 21
- Spring Boot
- Spring Data JPA
- Spring Security
- Thymeleaf
- MySQL
- Hibernate
- Gradle
- Swagger / OpenAPI
- JUnit 5
- Mockito
- MockMvc

---

## User Roles

### Admin

Admins have full access to the system.

They can manage:

- Patients
- Doctors
- Specialties
- Diagnoses
- Examinations
- Treatments
- Sick leaves
- Health insurance statuses
- Statistics

### Doctor

Doctors can:

- View patients
- Create examinations
- Add diagnoses
- Add treatments
- Add sick leaves
- View patient medical history
- Check health insurance information

Doctors cannot freely edit examinations created by other doctors.

### Patient

Patients can:

- Log into the system
- View their own medical information
- View their own examinations
- View their own treatments
- View their own sick leaves
- See whether they are insured for the last six months

Patients cannot access other patients’ medical data.

---

## Main Business Logic

### Health Insurance

The system stores health insurance status by patient, year, and month.

Example:

```json
{
  "patientId": 1,
  "year": 2026,
  "month": 5,
  "insured": true
}
```

A patient is considered insured only if all last six months are marked as insured.

If one month is missing or marked as not insured, the patient is treated as not insured.

### Examination Payment

When an examination is created, the system automatically decides the payment source:

```text
Insured for last 6 months -> NHIF
Not insured              -> PATIENT
```

---

## Security

The project uses Spring Security.

Implemented security features:

- Login form
- Role-based access control
- Protected Thymeleaf pages
- Protected REST API endpoints
- Custom access denied page
- JSON response for unauthorized API requests

Example unauthorized API response:

```json
{
  "error": "Unauthorized"
}
```

---

## Validation and Error Handling

The application uses DTO validation.

Examples of validation rules:

- Required fields cannot be empty
- Examination date cannot be in the future
- Month must be valid
- Price must be valid
- Related entity IDs must be provided

Invalid requests return structured error responses.

Example:

```json
{
  "status": 400,
  "error": "Validation failed",
  "messages": [
    "examinationDate: Examination date cannot be in the future"
  ]
}
```

The project also includes custom pages for:

- Access denied
- 403 Forbidden
- 404 Not Found
- 500 Internal Server Error

---

## REST API

Swagger UI is available at:

```text
http://localhost:8080/swagger-ui/index.html
```

Example endpoints:

```text
GET    /api/examinations
GET    /api/examinations/{id}
POST   /api/examinations
PUT    /api/examinations/{id}
DELETE /api/examinations/{id}
```

```text
GET  /api/health-insurance-statuses
POST /api/health-insurance-statuses
```

More endpoints are available in Swagger.

---

## Web Pages

Main Thymeleaf pages:

```text
/login
/home
/patients
/doctors
/examinations
/treatments
/sick-leaves
/health-insurance-statuses
/statistics
/access-denied
```

Access depends on the logged-in user role.

---

## Database

The project uses MySQL.

Example `application.properties` configuration:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/medical_record_system
spring.datasource.username=root
spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

Create the database if needed:

```sql
CREATE DATABASE medical_record_system;
```

---

## How to Run

### 1. Start MySQL

Make sure MySQL is running locally.

### 2. Configure Database

Open:

```text
src/main/resources/application.properties
```

Set your local MySQL username and password.

### 3. Run the Application

From IntelliJ, run:

```text
MedicalRecordSystemApplication
```

Or from terminal:

```powershell
.\gradlew.bat bootRun
```

The application starts at:

```text
http://localhost:8080
```

---

## Tests

The project includes tests for multiple layers.

### Service Tests

Current service tests:

```text
HealthInsuranceStatusServiceImplTest
ExaminationServiceImplTest
```

They test:

- Health insurance last-six-months logic
- Creating and updating health insurance statuses
- Examination payment source logic
- Patient access restrictions
- Doctor edit restrictions

### Controller Tests

Current controller tests:

```text
ExaminationApiControllerTest
HealthInsuranceStatusApiControllerTest
```

They test:

- HTTP status codes
- JSON responses
- DTO validation
- REST API request handling

### Integration Tests

Current integration tests:

```text
ExaminationApiControllerIntegrationTest
HealthInsuranceStatusApiControllerIntegrationTest
SecurityIntegrationTest
```

They test:

- Authenticated API access
- Unauthorized API access
- Validation errors
- Security restrictions
- Access denied behavior

Run all tests:

```powershell
.\gradlew.bat clean test
```

Run a specific test:

```powershell
.\gradlew.bat test --tests "com.inf.medical_record_system.service.impl.ExaminationServiceImplTest"
```

---

## Suggested Demo Flow

1. Log in as admin
2. Show patients and doctors
3. Show health insurance statuses
4. Create or view an examination
5. Explain automatic payment source logic
6. Log in as doctor
7. Show patient history
8. Log in as patient
9. Show that the patient only sees their own data
10. Try to open a restricted page
11. Show the access denied page
12. Open Swagger
13. Run or show the tests

---

## Project Status

Implemented:

- Entities
- Repositories
- DTOs
- Services
- REST controllers
- Thymeleaf pages
- Spring Security
- Role-based access
- Custom error pages
- Validation
- Exception handling
- Swagger documentation
- Service tests
- Controller tests
- Integration tests
- Security tests

---

## Author

Mario Slavov  
New Bulgarian University  
Informatics