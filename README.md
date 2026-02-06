# Spring Microservices Order System

Order management system built with Spring Boot, microservices, and hexagonal architecture.

## 🏗️ Architecture

This project implements a microservices architecture with the following components:

- **Service Discovery (Eureka)**: Service registration and discovery
- **API Gateway**: Single entry point for all services
- **Config Server**: Centralized configuration
- **User Service**: User management
- **Product Service**: Product management
- **Order Service**: Order management

### Hexagonal Architecture

Each microservice follows the hexagonal architecture pattern (ports and adapters):

```
├── domain/              # Pure business logic
│   ├── model/          # Entities and Value Objects
│   ├── service/        # Domain services
│   └── event/          # Domain events
├── application/        # Use cases
│   ├── port/
│   │   ├── in/        # Input ports (interfaces)
│   │   └── out/       # Output ports (interfaces)
│   └── service/       # Use case implementation
└── infrastructure/     # Adapters
    ├── adapter/
    │   ├── in/        # Input adapters (REST, etc.)
    │   └── out/       # Output adapters (DB, messaging)
    └── config/        # Spring configuration
```

## 🛠️ Technologies

- **Java 17**
- **Spring Boot 3.2.2**
- **Spring Cloud 2023.0.0**
- **Oracle Database 21c**
- **RabbitMQ 3.12**
- **Maven**
- **Docker & Docker Compose**

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.8+
- Docker and Docker Compose
- Git

## 🚀 Quick Start

### 1. Clone the repository

```bash
git clone <repository-url>
cd spring-microservices-order-system
```

### 2. Build the project

```bash
mvn clean install
```

### 3. Start infrastructure with Docker

```bash
docker-compose up -d oracle-db rabbitmq
```

Wait a few minutes for Oracle DB to be fully initialized.

### 4. Start services

**Option A: With Docker Compose (Recommended)**
```bash
docker-compose up -d
```

**Option B: Manually**
```bash
# Service Discovery
cd service-discovery
mvn spring-boot:run

# Config Server (in another terminal)
cd config-server
mvn spring-boot:run

# User Service (in another terminal)
cd user-service
mvn spring-boot:run

# Product Service (in another terminal)
cd product-service
mvn spring-boot:run

# Order Service (in another terminal)
cd order-service
mvn spring-boot:run

# API Gateway (in another terminal)
cd api-gateway
mvn spring-boot:run
```

## 🔗 Endpoints

- **Eureka Dashboard**: http://localhost:8761
- **API Gateway**: http://localhost:8080
- **RabbitMQ Management**: http://localhost:15672 (admin/admin123)
- **User Service**: http://localhost:8081
- **Product Service**: http://localhost:8082
- **Order Service**: http://localhost:8083

## 📚 API Documentation

Once the services are started, access the Swagger documentation:

- **User Service API**: http://localhost:8080/user-service/swagger-ui.html
- **Product Service API**: http://localhost:8080/product-service/swagger-ui.html
- **Order Service API**: http://localhost:8080/order-service/swagger-ui.html

## 🧪 Testing

### Run unit tests
```bash
mvn clean test
```

### Run integration tests
```bash
mvn clean verify
```

### Code coverage
```bash
mvn clean test jacoco:report
```

Reports are generated in `target/site/jacoco/index.html` for each module.

## 📦 Project Structure

```
spring-microservices-order-system/
├── api-gateway/              # Spring Cloud Gateway
├── service-discovery/        # Eureka Server
├── config-server/           # Spring Cloud Config
├── user-service/            # User microservice
├── product-service/         # Product microservice
├── order-service/           # Order microservice
├── common/                  # Shared libraries
├── docker-compose.yml       # Docker configuration
├── pom.xml                  # Parent POM
└── README.md
```

## 🔧 Configuration

### Oracle Database

Default credentials:
- **Host**: localhost:1521
- **SID**: XEPDB1
- **User**: system
- **Password**: Oracle123

### RabbitMQ

Default credentials:
- **Host**: localhost:5672
- **User**: admin
- **Password**: admin123

### Version Control

The project includes `.gitignore` files configured in:
- **Project root**: Global configuration for the entire monorepo
- **Each microservice**: Specific configuration for each module

The `.gitignore` files exclude:
- Maven build files (`target/`, `*.class`)
- IDE configuration files (`.idea/`, `*.iml`, `.vscode/`)
- Logs and temporary files (`*.log`, `*.tmp`, `*.bak`)
- Local configuration files (`application-local.yml`)
- Operating system files (`.DS_Store`, `Thumbs.db`)

## 🐛 Troubleshooting

### Oracle DB won't start
```bash
docker-compose logs oracle-db
docker-compose restart oracle-db
```

### Port already in use
```bash
# View processes using the port
netstat -ano | findstr :8080

# Change the port in application.yml or stop the process
```

### Services don't register in Eureka
- Verify that Eureka is running at http://localhost:8761
- Check service logs
- Verify `eureka.client.serviceUrl.defaultZone` configuration

## 📝 License

This project is licensed under the MIT License.

## 👥 Contributing

Contributions are welcome. Please:

1. Fork the project
2. Create a branch for your feature (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📧 Contact

For questions or suggestions, please open an issue in the repository.
