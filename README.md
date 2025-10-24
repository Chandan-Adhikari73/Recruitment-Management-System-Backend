# Recruitment Management System - Backend

Java 17, Spring Boot, Maven, MySQL.

## Quick setup

1. Install Java 17 and Maven.
2. Create MySQL database:
   ```sql
   CREATE DATABASE recruitdb;
   ```
3. Update `src/main/resources/application.properties` with your MySQL username/password.
4. Build and run:
   ```bash
   mvn clean package
   mvn spring-boot:run
   ```
5. APIs:
   - POST /signup
   - POST /login
   - POST /uploadResume (multipart form file, Applicant only)
   - POST /admin/job (Admin only)
   - GET /admin/job/{job_id}
   - GET /admin/applicants
   - GET /admin/applicant/{applicant_id}
   - GET /jobs
   - GET /jobs/apply?job_id={job_id}

## Notes
- Replace `apilayer.api.key` in `application.properties` with a valid key to call the real resume parser API.
- This project saves uploaded files to the `uploads/` folder relative to the run directory.
# Recruitment-Management-System-Backend
