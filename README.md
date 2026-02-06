# Spring Microservices Order System

Sistema de gestión de pedidos construido con Spring Boot, microservicios y arquitectura hexagonal.

## 🏗️ Arquitectura

Este proyecto implementa una arquitectura de microservicios con los siguientes componentes:

- **Service Discovery (Eureka)**: Registro y descubrimiento de servicios
- **API Gateway**: Punto de entrada único para todos los servicios
- **Config Server**: Configuración centralizada
- **User Service**: Gestión de usuarios
- **Product Service**: Gestión de productos
- **Order Service**: Gestión de pedidos

### Arquitectura Hexagonal

Cada microservicio sigue el patrón de arquitectura hexagonal (puertos y adaptadores):

```
├── domain/              # Lógica de negocio pura
│   ├── model/          # Entidades y Value Objects
│   ├── service/        # Servicios de dominio
│   └── event/          # Eventos de dominio
├── application/        # Casos de uso
│   ├── port/
│   │   ├── in/        # Puertos de entrada (interfaces)
│   │   └── out/       # Puertos de salida (interfaces)
│   └── service/       # Implementación de casos de uso
└── infrastructure/     # Adaptadores
    ├── adapter/
    │   ├── in/        # Adaptadores de entrada (REST, etc.)
    │   └── out/       # Adaptadores de salida (DB, messaging)
    └── config/        # Configuración de Spring
```

## 🛠️ Tecnologías

- **Java 17**
- **Spring Boot 3.2.2**
- **Spring Cloud 2023.0.0**
- **Oracle Database 21c**
- **RabbitMQ 3.12**
- **Maven**
- **Docker & Docker Compose**

## 📋 Requisitos Previos

- Java 17 o superior
- Maven 3.8+
- Docker y Docker Compose
- Git

## 🚀 Inicio Rápido

### 1. Clonar el repositorio

```bash
git clone <repository-url>
cd spring-microservices-order-system
```

### 2. Compilar el proyecto

```bash
mvn clean install
```

### 3. Iniciar infraestructura con Docker

```bash
docker-compose up -d oracle-db rabbitmq
```

Espera unos minutos para que Oracle DB esté completamente inicializado.

### 4. Iniciar servicios

**Opción A: Con Docker Compose (Recomendado)**
```bash
docker-compose up -d
```

**Opción B: Manualmente**
```bash
# Service Discovery
cd service-discovery
mvn spring-boot:run

# Config Server (en otra terminal)
cd config-server
mvn spring-boot:run

# User Service (en otra terminal)
cd user-service
mvn spring-boot:run

# Product Service (en otra terminal)
cd product-service
mvn spring-boot:run

# Order Service (en otra terminal)
cd order-service
mvn spring-boot:run

# API Gateway (en otra terminal)
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

Una vez iniciados los servicios, accede a la documentación Swagger:

- **User Service API**: http://localhost:8080/user-service/swagger-ui.html
- **Product Service API**: http://localhost:8080/product-service/swagger-ui.html
- **Order Service API**: http://localhost:8080/order-service/swagger-ui.html

## 🧪 Testing

### Ejecutar tests unitarios
```bash
mvn clean test
```

### Ejecutar tests de integración
```bash
mvn clean verify
```

### Cobertura de código
```bash
mvn clean test jacoco:report
```

Los reportes se generan en `target/site/jacoco/index.html` de cada módulo.

## 📦 Estructura del Proyecto

```
spring-microservices-order-system/
├── api-gateway/              # Spring Cloud Gateway
├── service-discovery/        # Eureka Server
├── config-server/           # Spring Cloud Config
├── user-service/            # Microservicio de usuarios
├── product-service/         # Microservicio de productos
├── order-service/           # Microservicio de pedidos
├── common/                  # Librerías compartidas
├── docker-compose.yml       # Configuración Docker
├── pom.xml                  # Parent POM
└── README.md
```

## 🔧 Configuración

### Base de Datos Oracle

Credenciales por defecto:
- **Host**: localhost:1521
- **SID**: XEPDB1
- **Usuario**: system
- **Password**: Oracle123

### RabbitMQ

Credenciales por defecto:
- **Host**: localhost:5672
- **Usuario**: admin
- **Password**: admin123

### Control de Versiones

El proyecto incluye archivos `.gitignore` configurados en:
- **Raíz del proyecto**: Configuración global para todo el monorepo
- **Cada microservicio**: Configuración específica para cada módulo

Los archivos `.gitignore` excluyen:
- Archivos de compilación de Maven (`target/`, `*.class`)
- Archivos de configuración de IDEs (`.idea/`, `*.iml`, `.vscode/`)
- Logs y archivos temporales (`*.log`, `*.tmp`, `*.bak`)
- Archivos de configuración local (`application-local.yml`)
- Archivos del sistema operativo (`.DS_Store`, `Thumbs.db`)

## 🐛 Troubleshooting

### Oracle DB no inicia
```bash
docker-compose logs oracle-db
docker-compose restart oracle-db
```

### Puerto ya en uso
```bash
# Ver procesos usando el puerto
netstat -ano | findstr :8080

# Cambiar el puerto en application.yml o detener el proceso
```

### Servicios no se registran en Eureka
- Verificar que Eureka esté corriendo en http://localhost:8761
- Revisar logs del servicio
- Verificar configuración de `eureka.client.serviceUrl.defaultZone`

## 📝 Licencia

Este proyecto está bajo la licencia MIT.

## 👥 Contribuir

Las contribuciones son bienvenidas. Por favor:

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## 📧 Contacto

Para preguntas o sugerencias, por favor abre un issue en el repositorio.
