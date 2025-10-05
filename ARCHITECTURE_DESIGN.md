# Sistem Ticketing - Arsitektur Desain

## 1. Gambaran Umum

Sistem manajemen tiket layanan pelanggan dengan fitur assignment otomatis, monitoring SLA, eskalasi, dan notifikasi asinkron.

## 2. Arsitektur Sistem

### 2.1 Pola Arsitektur
- **Layered Architecture (4 lapisan)**
- Komunikasi: Controller → Service → Repository

### 2.2 Lapisan Sistem
1. **Presentation**: REST API Controllers (Spring MVC)
2. **Application**: Business Services (Transaction Management)
3. **Domain**: Domain Entities & Business Logic
4. **Infrastructure**: Repositories, Cache, Message Queue

### 2.3 Komponen Utama
- **Core Domain**: Ticket, Agent, User, Department
- **Support Domain**: TicketComment, TicketAttachment, Notification
- **Services**: TicketService, AssignmentService, EscalationService, NotificationService
- **Infrastructure**: PostgreSQL, Redis, RabbitMQ

## 3. Teknologi

- **Bahasa**: Java 17 LTS
- **Framework**: Spring Boot 3.2.x
- **Database**: PostgreSQL 15
- **Cache**: Redis 7.x (opsional)
- **Message Queue**: RabbitMQ 3.12 (opsional)
- **Security**: Spring Security + JWT
- **Build Tool**: Maven 3.9.x
- **Testing**: JUnit 5 + Mockito

## 4. Algoritma Inti

### 4.1 Ticket Assignment
- **Strategi**: Round-Robin, Workload-Based, Department-Based
- **Default**: WORKLOAD_BASED
- **Kompleksitas**: O(n)

### 4.2 SLA Calculation
- **Mapping**: URGENT(4h), HIGH(8h), MEDIUM(24h), LOW(48h), LOWEST(72h)
- **Kompleksitas**: O(1)

### 4.3 SLA Monitoring & Escalation
- **Threshold**: 75%, 90%, >100%
- **Frekuensi**: Setiap 5 menit
- **Kompleksitas**: O(n)

### 4.4 Search & Filtering
- **Teknologi**: JPA Specification
- **Index**: GIN, composite, created_at
- **Kompleksitas**: O(n) dengan index

### 4.5 Cache Invalidation
- **Strategi**: Cache-aside
- **TTL**: Ticket(10m), Agent(15m), Department(1j)
- **Kompleksitas**: O(1) single key, O(n) pattern

### 4.6 Rate Limiting
- **Algoritma**: Token Bucket
- **Implementasi**: Redis
- **Kompleksitas**: O(1)

### 4.7 Async Notification
- **Queue**: RabbitMQ dengan DLQ
- **Retry**: 3x exponential backoff
- **Kompleksitas**: O(1) publish, O(n) processing

## 5. Struktur Data

### 5.1 Entity Utama

#### Ticket
- **Fields**: id, subject, description, category, priority, status, departmentId, createdBy, assigneeId, slaHours, slaDueAt, escalationLevel, timeResolved, timeClosed
- **Relasi**: Creator, Assignee, Department, Comments, Attachments
- **Index**: status, priority, assignee, sla_due_at, created_at, category

#### Agent
- **Fields**: id, name, email, departmentId, skills, maxConcurrentTickets, activeTicketCount, status
- **Derived**: workloadPercentage, isFull, isAvailable
- **Index**: department, status(AVAILABLE)

#### User
- **Fields**: id, name, email, passwordHash, role, departmentId
- **Security**: BCrypt, JWT
- **Index**: email(UNIQUE)

#### Department
- **Struktur**: Hierarki self-referencing
- **Tree Ops**: getAncestors, getDescendants, getPath
- **Index**: parent_department_id

#### TicketComment
- **Fields**: id, ticketId, authorId, content, isInternal
- **Visibility**: Internal comments
- **Index**: ticket_id, created_at

### 5.2 Database Schema

```sql
-- Tables: tickets, agents, users, departments, ticket_comments
-- Indexes: status, priority, sla_due_at, assignee, created_at, category
-- Constraints: Foreign keys, check constraints
-- Enums: Priority(LOWEST, LOW, MEDIUM, HIGH, URGENT), Status(NEW, ASSIGNED, IN_PROGRESS, ON_HOLD, RESOLVED, CLOSED)
```

## 6. API Endpoints

### 6.1 Auth
- `POST /auth/register` - Registrasi user
- `POST /auth/login` - Login JWT

### 6.2 Ticket
- `POST /tickets` - Create (auto-assignment, SLA)
- `GET /tickets/{id}` - Detail (cache 10m)
- `GET /tickets` - List (filtering, pagination)
- `POST /tickets/{id}/assign` - Manual assignment
- `POST /tickets/{id}/close` - Close ticket

### 6.3 Agent & Department
- `GET /agents` - List agents
- `GET /agents/{id}/tickets` - Agent tickets
- `GET /departments` - Department tree (cache 1j)

## 7. Konfigurasi

### 7.1 Environment
- Database, Redis, RabbitMQ, JWT
- Assignment strategy
- SLA monitoring cron

### 7.2 Dependencies
- Spring Boot 3.2.x modules
- PostgreSQL, Redis, RabbitMQ
- Testing: JUnit, Mockito, TestContainers

## 8. Testing Strategy

### 8.1 Unit Testing
- Coverage: 80%
- Fokus: Business logic, validasi, algoritma

### 8.2 Integration Testing
- TestContainers: PostgreSQL, Redis
- API testing: MockMvc

### 8.3 Skenario
- Auto-assignment
- SLA escalation
- Workload-based assignment
- Rate limiting
- Multi-filter search

## 9. Implementasi Bertahap

### Phase 1 (MVP)
- Core entities
- Basic CRUD APIs
- Assignment algorithm
- SLA calculation
- PostgreSQL

### Phase 2 (Intermediate)
- Authentication & Authorization
- Search & Filtering
- SLA Monitoring
- Escalation logic

### Phase 3 (Advanced)
- Redis caching
- RabbitMQ notifications
- Rate limiting
- Comprehensive testing

## 10. Validasi

- **Kelengkapan**: 100%
- **Implementabilitas**: TINGGI
- **Skalabilitas**: SEDANG (10K+ tiket)
- **Maintainabilitas**: TINGGI
- **Production Readiness**: 80%
