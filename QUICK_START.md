# Quick Start Guide - Spring Microservices Order System

## 📋 Resumen del Proyecto

Este proyecto implementa un sistema de microservicios con Spring Boot y arquitectura hexagonal completa.

### ✅ Componentes Completados

1. **Infraestructura**
   - ✅ Service Discovery (Eureka Server) - Puerto 8761
   - ✅ Config Server - Puerto 8888
   - ✅ API Gateway con Circuit Breaker - Puerto 8080
   - ✅ Common Module (excepciones y DTOs compartidos)

2. **User Service** (COMPLETO)
   - ✅ Arquitectura hexagonal completa
   - ✅ Domain: User, Email (Value Object), Address (Value Object)
   - ✅ Application: UserService, puertos de entrada/salida
   - ✅ Infrastructure: REST Controller, JPA Repository, RabbitMQ Publisher
   - ✅ Puerto 8081

### 🔨 Próximos Pasos

Para completar el proyecto, necesitas crear:

1. **Product Service** (Puerto 8082)
   - Seguir el mismo patrón que User Service
   - Entidades: Product, Price (Value Object), Category
   - Funcionalidades: CRUD de productos, gestión de inventario

2. **Order Service** (Puerto 8083)
   - Seguir el mismo patrón que User Service
   - Entidades: Order, OrderItem, OrderStatus
   - Funcionalidades: Crear pedidos, consultar pedidos, actualizar estado

## 🚀 Cómo Ejecutar

### Opción 1: Desarrollo Local (Sin Docker)

```powershell
# 1. Iniciar Oracle DB y RabbitMQ con Docker
cd c:\Users\Jorney\Desktop\My MVP for incomes\spring-microservices-order-system
docker-compose up -d oracle-db rabbitmq

# 2. Compilar todo el proyecto
mvn clean install

# 3. Iniciar servicios en orden (abrir terminales separadas)

# Terminal 1 - Service Discovery
cd service-discovery
mvn spring-boot:run

# Terminal 2 - Config Server (esperar que Eureka esté listo)
cd config-server
mvn spring-boot:run

# Terminal 3 - User Service (esperar que Config Server esté listo)
cd user-service
mvn spring-boot:run

# Terminal 4 - API Gateway
cd api-gateway
mvn spring-boot:run
```

### Opción 2: Todo con Docker

```powershell
# Compilar todos los servicios
mvn clean package -DskipTests

# Iniciar todo con Docker Compose
docker-compose up -d
```

## 🔗 URLs Importantes

- **Eureka Dashboard**: http://localhost:8761
- **API Gateway**: http://localhost:8080
- **RabbitMQ Management**: http://localhost:15672 (admin/admin123)
- **User Service Swagger**: http://localhost:8080/api/users/swagger-ui.html
- **User Service Direct**: http://localhost:8081/swagger-ui.html

## 📝 Ejemplos de API

### Crear Usuario

```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Juan",
    "lastName": "Pérez",
    "email": "juan.perez@example.com",
    "phone": "+52-555-1234",
    "street": "Av. Reforma 123",
    "city": "Ciudad de México",
    "state": "CDMX",
    "zipCode": "06600",
    "country": "México"
  }'
```

### Obtener Todos los Usuarios

```bash
curl http://localhost:8080/api/users
```

### Obtener Usuario por ID

```bash
curl http://localhost:8080/api/users/{uuid}
```

### Obtener Usuario por Email

```bash
curl http://localhost:8080/api/users/email/juan.perez@example.com
```

## 🏗️ Arquitectura Hexagonal

Cada microservicio sigue esta estructura:

```
service/
├── domain/                    # Capa de Dominio (Lógica de Negocio)
│   ├── model/                # Entidades y Value Objects
│   ├── service/              # Servicios de Dominio
│   └── event/                # Eventos de Dominio
├── application/              # Capa de Aplicación (Casos de Uso)
│   ├── port/
│   │   ├── in/              # Puertos de Entrada (Interfaces)
│   │   └── out/             # Puertos de Salida (Interfaces)
│   └── service/             # Implementación de Casos de Uso
└── infrastructure/          # Capa de Infraestructura (Adaptadores)
    ├── adapter/
    │   ├── in/
    │   │   └── rest/        # Adaptador REST (Controllers)
    │   └── out/
    │       ├── persistence/ # Adaptador de Persistencia (JPA)
    │       └── messaging/   # Adaptador de Mensajería (RabbitMQ)
    ├── config/              # Configuración de Spring
    └── exception/           # Manejo de Excepciones
```

## 🎯 Ventajas de esta Arquitectura

1. **Separación de Responsabilidades**: Cada capa tiene una responsabilidad clara
2. **Independencia de Frameworks**: El dominio no depende de Spring, JPA, etc.
3. **Testeable**: Fácil crear tests unitarios del dominio
4. **Mantenible**: Cambios en infraestructura no afectan el dominio
5. **Escalable**: Fácil agregar nuevos adaptadores (GraphQL, gRPC, etc.)

## 🔧 Troubleshooting

### Oracle DB no inicia
```powershell
docker-compose logs oracle-db
docker-compose restart oracle-db
```

### Puerto ocupado
```powershell
# Ver qué proceso usa el puerto
netstat -ano | findstr :8080

# Cambiar puerto en application.yml o detener el proceso
```

### Servicios no se registran en Eureka
- Verificar que Eureka esté corriendo: http://localhost:8761
- Revisar logs del servicio
- Verificar configuración de `eureka.client.serviceUrl.defaultZone`

## 📚 Recursos Adicionales

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Cloud Documentation](https://spring.io/projects/spring-cloud)
- [Hexagonal Architecture](https://alistair.cockburn.us/hexagonal-architecture/)
- [Domain-Driven Design](https://martinfowler.com/bliki/DomainDrivenDesign.html)

## 🎓 Conceptos Clave

### Arquitectura Hexagonal (Puertos y Adaptadores)

- **Puertos de Entrada**: Interfaces que definen cómo el mundo exterior usa nuestra aplicación
- **Puertos de Salida**: Interfaces que definen cómo nuestra aplicación usa servicios externos
- **Adaptadores de Entrada**: Implementaciones que conectan el mundo exterior (REST, GraphQL, etc.)
- **Adaptadores de Salida**: Implementaciones que conectan a servicios externos (DB, APIs, etc.)

### Value Objects

Objetos inmutables que representan conceptos del dominio:
- `Email`: Valida formato de email
- `Address`: Representa una dirección completa
- `Price`: Representa un precio con validación

### Domain Events

Eventos que representan algo que sucedió en el dominio:
- `UserCreatedEvent`: Se publica cuando se crea un usuario
- Permite comunicación asíncrona entre microservicios
