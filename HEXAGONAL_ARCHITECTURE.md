# Arquitectura Hexagonal - Guía Completa

## 🎯 ¿Qué es la Arquitectura Hexagonal?

La arquitectura hexagonal (también conocida como "Puertos y Adaptadores") fue propuesta por Alistair Cockburn. Su objetivo principal es **aislar la lógica de negocio de las dependencias externas**.

### Principios Fundamentales

1. **El dominio es el centro**: La lógica de negocio no depende de frameworks
2. **Inversión de dependencias**: Las capas externas dependen del dominio, no al revés
3. **Puertos definen contratos**: Interfaces que el dominio expone o necesita
4. **Adaptadores implementan detalles**: Tecnologías específicas (REST, JPA, RabbitMQ)

## 📐 Estructura de Capas

```
┌─────────────────────────────────────────────────────────┐
│                    INFRASTRUCTURE                        │
│  ┌──────────────────────────────────────────────────┐  │
│  │              APPLICATION LAYER                    │  │
│  │  ┌────────────────────────────────────────────┐  │  │
│  │  │          DOMAIN LAYER                      │  │  │
│  │  │  ┌──────────────────────────────────────┐  │  │  │
│  │  │  │  Entities, Value Objects, Events    │  │  │  │
│  │  │  │  Domain Services                     │  │  │  │
│  │  │  └──────────────────────────────────────┘  │  │  │
│  │  │  Use Cases, Ports (Interfaces)            │  │  │
│  │  └────────────────────────────────────────────┘  │  │
│  │  Adapters (REST, JPA, RabbitMQ, etc.)           │  │
│  └──────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────┘
```

### 1. Domain Layer (Núcleo)

**Responsabilidad**: Contiene la lógica de negocio pura

**Componentes**:
- **Entities**: Objetos con identidad (User, Product, Order)
- **Value Objects**: Objetos inmutables sin identidad (Email, Price, Address)
- **Domain Services**: Lógica que no pertenece a una entidad específica
- **Domain Events**: Eventos que ocurren en el dominio

**Reglas**:
- ❌ NO debe depender de Spring, JPA, o cualquier framework
- ❌ NO debe tener anotaciones de infraestructura
- ✅ Debe ser fácil de testear sin dependencias externas
- ✅ Debe contener toda la lógica de validación de negocio

**Ejemplo**:
```java
// ✅ CORRECTO - Dominio puro
public class User {
    private UUID id;
    private String firstName;
    private String lastName;
    private Email email;  // Value Object
    private UserStatus status;
    
    public void activate() {
        if (this.status == UserStatus.SUSPENDED) {
            throw new BusinessException("Cannot activate suspended user");
        }
        this.status = UserStatus.ACTIVE;
    }
}

// ❌ INCORRECTO - Dominio con dependencias de infraestructura
@Entity  // ❌ Anotación de JPA
@Table(name = "users")  // ❌ Anotación de JPA
public class User {
    @Id  // ❌ Anotación de JPA
    private UUID id;
    // ...
}
```

### 2. Application Layer (Casos de Uso)

**Responsabilidad**: Orquesta la lógica de negocio

**Componentes**:
- **Use Cases**: Implementan los casos de uso del sistema
- **Input Ports**: Interfaces que definen qué puede hacer la aplicación
- **Output Ports**: Interfaces que definen qué necesita la aplicación

**Reglas**:
- ✅ Puede usar anotaciones de Spring (@Service, @Transactional)
- ✅ Depende solo del dominio
- ✅ Define interfaces (puertos) para dependencias externas
- ❌ NO debe conocer detalles de REST, JPA, etc.

**Ejemplo**:
```java
// Input Port (Interface)
public interface CreateUserUseCase {
    User createUser(CreateUserCommand command);
}

// Output Port (Interface)
public interface UserRepository {
    User save(User user);
    Optional<User> findById(UUID id);
}

// Use Case Implementation
@Service
public class UserService implements CreateUserUseCase {
    private final UserRepository userRepository;  // Puerto de salida
    private final EventPublisher eventPublisher;  // Puerto de salida
    
    @Transactional
    public User createUser(CreateUserCommand command) {
        // Orquesta la lógica usando el dominio
        User user = User.builder()
            .email(new Email(command.getEmail()))
            .build();
            
        User savedUser = userRepository.save(user);
        eventPublisher.publishUserCreatedEvent(...);
        return savedUser;
    }
}
```

### 3. Infrastructure Layer (Adaptadores)

**Responsabilidad**: Implementa los detalles técnicos

**Componentes**:
- **Input Adapters**: REST Controllers, GraphQL Resolvers, Message Listeners
- **Output Adapters**: JPA Repositories, HTTP Clients, Message Publishers
- **Configuration**: Beans de Spring, configuración de frameworks

**Reglas**:
- ✅ Implementa los puertos definidos en la capa de aplicación
- ✅ Puede usar cualquier framework o librería
- ✅ Convierte entre DTOs y entidades de dominio
- ❌ NO debe contener lógica de negocio

**Ejemplo**:
```java
// Input Adapter - REST Controller
@RestController
@RequestMapping("/users")
public class UserController {
    private final CreateUserUseCase createUserUseCase;  // Puerto de entrada
    
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody UserRequest request) {
        CreateUserCommand command = mapper.toCommand(request);
        User user = createUserUseCase.createUser(command);
        return ResponseEntity.ok(mapper.toResponse(user));
    }
}

// Output Adapter - JPA Repository
@Component
public class UserRepositoryAdapter implements UserRepository {
    private final UserJpaRepository jpaRepository;
    private final UserPersistenceMapper mapper;
    
    @Override
    public User save(User user) {
        UserJpaEntity entity = mapper.toEntity(user);
        UserJpaEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }
}
```

## 🔌 Puertos y Adaptadores

### Puertos de Entrada (Input Ports)

Definen **cómo el mundo exterior usa nuestra aplicación**

```java
// Puerto de entrada
public interface CreateUserUseCase {
    User createUser(CreateUserCommand command);
}

// Adaptador de entrada (REST)
@RestController
public class UserController {
    private final CreateUserUseCase createUserUseCase;
    // Implementa endpoint HTTP que usa el puerto
}

// Adaptador de entrada (Message Listener)
@Component
public class UserMessageListener {
    private final CreateUserUseCase createUserUseCase;
    // Escucha mensajes de RabbitMQ y usa el puerto
}
```

### Puertos de Salida (Output Ports)

Definen **qué necesita nuestra aplicación del mundo exterior**

```java
// Puerto de salida
public interface UserRepository {
    User save(User user);
    Optional<User> findById(UUID id);
}

// Adaptador de salida (JPA)
@Component
public class UserJpaRepositoryAdapter implements UserRepository {
    // Implementa usando JPA
}

// Adaptador de salida (MongoDB) - Fácil de cambiar!
@Component
public class UserMongoRepositoryAdapter implements UserRepository {
    // Implementa usando MongoDB
}
```

## 💎 Value Objects

Los Value Objects son objetos inmutables que representan conceptos del dominio sin identidad propia.

### Características

1. **Inmutables**: No se pueden modificar después de crearse
2. **Sin identidad**: Se comparan por valor, no por referencia
3. **Auto-validación**: Se validan en el constructor
4. **Encapsulan lógica**: Contienen comportamiento relacionado

### Ejemplos

```java
// Email Value Object
public class Email {
    private final String value;
    
    public Email(String value) {
        if (!isValid(value)) {
            throw new IllegalArgumentException("Invalid email");
        }
        this.value = value.toLowerCase();
    }
    
    private boolean isValid(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Email)) return false;
        Email email = (Email) o;
        return value.equals(email.value);
    }
}

// Price Value Object
public class Price {
    private final BigDecimal amount;
    private final String currency;
    
    public Price(BigDecimal amount, String currency) {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        this.amount = amount;
        this.currency = currency;
    }
    
    public Price add(Price other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("Cannot add different currencies");
        }
        return new Price(this.amount.add(other.amount), this.currency);
    }
}
```

## 🎭 Ventajas de la Arquitectura Hexagonal

### 1. Independencia de Frameworks

```java
// El dominio no sabe nada de Spring, JPA, etc.
public class User {
    // Lógica de negocio pura
}

// Fácil cambiar de JPA a MongoDB
public class UserMongoAdapter implements UserRepository {
    // Nueva implementación sin tocar el dominio
}
```

### 2. Testabilidad

```java
// Test del dominio - Sin Spring, sin BD
@Test
void shouldActivateUser() {
    User user = User.builder()
        .status(UserStatus.INACTIVE)
        .build();
    
    user.activate();
    
    assertEquals(UserStatus.ACTIVE, user.getStatus());
}

// Test del caso de uso - Con mocks
@Test
void shouldCreateUser() {
    UserRepository mockRepo = mock(UserRepository.class);
    UserService service = new UserService(mockRepo, ...);
    
    service.createUser(command);
    
    verify(mockRepo).save(any(User.class));
}
```

### 3. Mantenibilidad

- Cambios en UI no afectan el dominio
- Cambios en BD no afectan el dominio
- Fácil agregar nuevos adaptadores (GraphQL, gRPC)

### 4. Escalabilidad

- Cada adaptador puede escalar independientemente
- Fácil migrar a microservicios
- Fácil agregar nuevas funcionalidades

## 🚀 Flujo de una Petición

```
1. HTTP Request
   ↓
2. UserController (Input Adapter)
   ↓
3. CreateUserCommand (DTO)
   ↓
4. UserService (Use Case)
   ↓
5. User (Domain Entity)
   ↓
6. UserRepository (Output Port)
   ↓
7. UserRepositoryAdapter (Output Adapter)
   ↓
8. UserJpaEntity (JPA Entity)
   ↓
9. Database
```

## 📚 Comparación con Arquitectura en Capas Tradicional

| Aspecto | Capas Tradicional | Hexagonal |
|---------|------------------|-----------|
| Dependencias | Hacia abajo | Hacia el dominio |
| Dominio | Depende de BD | Independiente |
| Testabilidad | Difícil | Fácil |
| Cambio de BD | Complejo | Simple |
| Lógica de negocio | Dispersa | Centralizada |

## 🎓 Mejores Prácticas

1. **Empieza por el dominio**: Define entidades y value objects primero
2. **Mantén el dominio puro**: Sin anotaciones de frameworks
3. **Usa interfaces para puertos**: Define contratos claros
4. **Un adaptador por tecnología**: Separa REST, JPA, RabbitMQ
5. **Mappers entre capas**: Convierte DTOs ↔ Dominio ↔ Entities
6. **Valida en el dominio**: No en controllers o DTOs
7. **Eventos de dominio**: Para comunicación entre bounded contexts
8. **Tests primero del dominio**: Debe ser fácil testear sin Spring

## 🔗 Referencias

- [Hexagonal Architecture - Alistair Cockburn](https://alistair.cockburn.us/hexagonal-architecture/)
- [Clean Architecture - Robert C. Martin](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [Domain-Driven Design - Eric Evans](https://www.domainlanguage.com/ddd/)
