# Salon Queue

A simple appointment and virtual queue system for a salon. Customers can book a time slot or join a live queue and see their position and estimated wait time. Staff manage the queue from a dashboard.

## Tech stack
- Java 21, Spring Boot 4.1.2 (Maven)
- Spring Web, Spring Data JPA, Validation, Lombok, DevTools
- MySQL 8.0
- Frontend: plain HTML, CSS and JavaScript (served by Spring Boot)

## Features
- CRUD for salons, services and staff
- Appointment booking with available slots (within salon hours, no past times, no overlaps)
- Virtual queue with live position and wait time (refreshes every 5 seconds)
- Staff dashboard to Start, finish (Done) or remove (Leave) queue entries
- JSON error responses: 400 invalid request, 404 not found, 409 conflict

## Setup

### 1. Create the database
```sql
CREATE DATABASE salon_db;
```

### 2. Configure `src/main/resources/application.properties`
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/salon_db
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD
spring.jpa.hibernate.ddl-auto=update
```

### 3. Run
Run `SalonQueueApplication` from IntelliJ, or use:
```
mvnw spring-boot:run
```
On first start, `DataSeeder` creates one salon (Glamour Studio, 09:00 to 20:00), 3 services and 2 staff members.

Open http://localhost:8080

## Pages
| Page | Purpose |
|------|---------|
| index.html | Links to everything |
| salons.html | Manage salons |
| services.html | Manage services |
| staff.html | Manage staff |
| book.html | Book an appointment |
| appointments.html | View, cancel or delete appointments |
| queue-join.html | Join the queue |
| queue-status.html | Live queue position (refreshes every 5s) |
| dashboard.html | Staff queue view (refreshes every 5s) |

## API
See [POSTMAN.md](POSTMAN.md) for all endpoints and sample bodies. Base URL: `http://localhost:8080/api`

## Queue rules
- A new entry starts as WAITING.
- Valid changes: WAITING to IN_SERVICE, WAITING to LEFT, IN_SERVICE to DONE.
- Starting a customer (IN_SERVICE) needs a `staffId`.

## Project structure
```
com.salon.salon_queue
  controller   REST controllers (/api)
  service      business logic and entity-to-DTO conversion
  repository   Spring Data JPA repositories
  entity       Salon, ServiceItem, Staff, Appointment, QueueEntry
  dto          request and response records
  exception    custom exceptions and GlobalExceptionHandler
src/main/resources/static   HTML, CSS and JS pages
```