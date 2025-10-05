# Help Desk System

A comprehensive customer service ticket management system built with Spring Boot 3.2.x, PostgreSQL, and Redis.

## Features

- **Auto-Assignment**: Workload-based, round-robin, and department-based assignment algorithms
- **SLA Management**: Automatic SLA calculation and monitoring with escalation
- **Real-time Monitoring**: 5-minute interval SLA tracking with warning/critical thresholds
- **Concurrent Request Handling**: Pessimistic locking for race condition prevention
- **Caching**: Redis-based cache-aside strategy
- **Async Notifications**: RabbitMQ with dead letter queue support

## Architecture

### Core Components

- **Presentation**: REST API Controllers (Spring MVC)
- **Application**: Business Services with Transaction Management
- **Domain**: Entities and Business Logic
- **Infrastructure**: PostgreSQL, Redis, RabbitMQ

### Database Schema

- `tickets` - Main ticket tracking
- `agents` - Agent management with workload tracking
- `users` - User authentication and roles
- `departments` - Hierarchical department structure
- `ticket_comments` - Internal and external comments

## Setup

1. **Prerequisites**
   - Java 17
   - PostgreSQL 15
   - Redis 7.x (optional)
   - RabbitMQ 3.12 (optional)

2. **Configuration**
   - Update `application.yml` with your database credentials
   - Configure Redis and RabbitMQ connections if using

3. **Run Migrations**
   ```bash
   mvn flyway:migrate
   ```

4. **Start Application**
   ```bash
   mvn spring-boot:run
   ```

## API Endpoints

### Authentication
- `POST /auth/register` - User registration
- `POST /auth/login` - JWT authentication

### Tickets
- `POST /tickets` - Create ticket with auto-assignment
- `GET /tickets/{id}` - Get ticket details
- `GET /tickets` - List with filtering and pagination
- `POST /tickets/{id}/assign` - Manual assignment
- `POST /tickets/{id}/close` - Close ticket

### Agents & Departments
- `GET /agents` - List agents
- `GET /agents/{id}/tickets` - Agent's tickets
- `GET /departments` - Department tree structure

## Assignment Strategies

1. **WORKLOAD_BASED** (default) - Assign to agent with lowest workload percentage
2. **ROUND_ROBIN** - Rotate through available agents
3. **DEPARTMENT_BASED** - Skill-based assignment within department

## SLA Configuration

- URGENT: 4 hours
- HIGH: 8 hours
- MEDIUM: 24 hours
- LOW: 48 hours
- LOWEST: 72 hours

## Monitoring

- Warning threshold: 75% of SLA time
- Critical threshold: 90% of SLA time
- Overdue: >100% of SLA time

## Testing

```bash
mvn test
```

Includes comprehensive unit and integration tests with TestContainers.
