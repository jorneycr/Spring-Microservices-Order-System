# Quick Start Guide - Spring Microservices Order System

## 📋 Project Overview

This project implements a microservices system with Spring Boot and complete hexagonal architecture.

### ✅ Completed Components

1. **Infrastructure**
   - ✅ Service Discovery (Eureka Server) - Port 8761
   - ✅ Config Server - Port 8888
   - ✅ API Gateway with Circuit Breaker - Port 8080
   - ✅ Common Module (shared exceptions and DTOs)

2. **User Service** (COMPLETE)
   - ✅ Complete hexagonal architecture
   - ✅ Domain: User, Email (Value Object), Address (Value Object)
   - ✅ Application: UserService, input/output ports
   - ✅ Infrastructure: REST Controller, JPA Repository, RabbitMQ Publisher
   - ✅ Port 8081

### 🔨 Next Steps

To complete the project, you need to create:

1. **Product Service** (Port 8082)
   - Follow the same pattern as User Service
   - Entities: Product, Price (Value Object), Category
   - Features: Product CRUD, inventory management

2. **Order Service** (Port 8083)
   - Follow the same pattern as User Service
   - Entities: Order, OrderItem, OrderStatus
   - Features: Create orders, query orders, update status

## 🚀 How to Run

### Option 1: Local Development (Without Docker)

```powershell
# 1. Start Oracle DB and RabbitMQ with Docker
cd c:\Users\Jorney\Desktop\My MVP for incomes\spring-microservices-order-system
docker-compose up -d oracle-db rabbitmq

# 2. Build the entire project
mvn clean install

# 3. Start services in order (open separate terminals)

# Terminal 1 - Service Discovery
cd service-discovery
mvn spring-boot:run

# Terminal 2 - Config Server (wait for Eureka to be ready)
cd config-server
mvn spring-boot:run

# Terminal 3 - User Service (wait for Config Server to be ready)
cd user-service
mvn spring-boot:run

# Terminal 4 - API Gateway
cd api-gateway
mvn spring-boot:run
```

### Option 2: Everything with Docker

```powershell
# Build all services
mvn clean package -DskipTests

# Start everything with Docker Compose
docker-compose up -d
```

## 🔗 Important URLs

- **Eureka Dashboard**: http://localhost:8761
- **API Gateway**: http://localhost:8080
- **RabbitMQ Management**: http://localhost:15672 (admin/admin123)
- **User Service Swagger**: http://localhost:8080/api/users/swagger-ui.html
- **User Service Direct**: http://localhost:8081/swagger-ui.html

## 📝 API Examples

### Create User

```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "phone": "+1-555-1234",
    "street": "123 Main St",
    "city": "New York",
    "state": "NY",
    "zipCode": "10001",
    "country": "USA"
  }'
```

### Get All Users

```bash
curl http://localhost:8080/api/users
```

### Get User by ID

```bash
curl http://localhost:8080/api/users/{uuid}
```

### Get User by Email

```bash
curl http://localhost:8080/api/users/email/john.doe@example.com
```

## 🏗️ Hexagonal Architecture

Each microservice follows this structure:

```
service/
├── domain/                    # Domain Layer (Business Logic)
│   ├── model/                # Entities and Value Objects
│   ├── service/              # Domain Services
│   └── event/                # Domain Events
├── application/              # Application Layer (Use Cases)
│   ├── port/
│   │   ├── in/              # Input Ports (Interfaces)
│   │   └── out/             # Output Ports (Interfaces)
│   └── service/             # Use Case Implementation
└── infrastructure/          # Infrastructure Layer (Adapters)
    ├── adapter/
    │   ├── in/
    │   │   └── rest/        # REST Adapter (Controllers)
    │   └── out/
    │       ├── persistence/ # Persistence Adapter (JPA)
    │       └── messaging/   # Messaging Adapter (RabbitMQ)
    ├── config/              # Spring Configuration
    └── exception/           # Exception Handling
```

## 🎯 Architecture Advantages

1. **Separation of Concerns**: Each layer has a clear responsibility
2. **Framework Independence**: Domain doesn't depend on Spring, JPA, etc.
3. **Testable**: Easy to create unit tests for the domain
4. **Maintainable**: Infrastructure changes don't affect the domain
5. **Scalable**: Easy to add new adapters (GraphQL, gRPC, etc.)

## 🔧 Troubleshooting

### Oracle DB won't start
```powershell
docker-compose logs oracle-db
docker-compose restart oracle-db
```

### Port already in use
```powershell
# Check which process is using the port
netstat -ano | findstr :8080

# Change port in application.yml or stop the process
```

### Services don't register in Eureka
- Verify Eureka is running: http://localhost:8761
- Check service logs
- Verify `eureka.client.serviceUrl.defaultZone` configuration

## 📚 Additional Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Cloud Documentation](https://spring.io/projects/spring-cloud)
- [Hexagonal Architecture](https://alistair.cockburn.us/hexagonal-architecture/)
- [Domain-Driven Design](https://martinfowler.com/bliki/DomainDrivenDesign.html)

## 🎓 Key Concepts

### Hexagonal Architecture (Ports and Adapters)

- **Input Ports**: Interfaces that define how the outside world uses our application
- **Output Ports**: Interfaces that define how our application uses external services
- **Input Adapters**: Implementations that connect the outside world (REST, GraphQL, etc.)
- **Output Adapters**: Implementations that connect to external services (DB, APIs, etc.)

### Value Objects

Immutable objects that represent domain concepts:
- `Email`: Validates email format
- `Address`: Represents a complete address
- `Price`: Represents a price with validation

### Domain Events

Events that represent something that happened in the domain:
- `UserCreatedEvent`: Published when a user is created
- Enables asynchronous communication between microservices
