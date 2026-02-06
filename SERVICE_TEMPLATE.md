# Plantilla para Crear Nuevos Microservicios

Esta guía te ayudará a crear Product Service y Order Service siguiendo el patrón de User Service.

## Pasos para Crear un Nuevo Microservicio

### 1. Crear estructura del módulo

```
new-service/
├── pom.xml
├── Dockerfile
└── src/
    └── main/
        ├── java/com/ordersystem/newservice/
        │   ├── domain/
        │   │   ├── model/
        │   │   │   ├── MainEntity.java
        │   │   │   ├── ValueObject.java
        │   │   │   └── EntityStatus.java
        │   │   ├── service/
        │   │   └── event/
        │   │       └── EntityCreatedEvent.java
        │   ├── application/
        │   │   ├── port/
        │   │   │   ├── in/
        │   │   │   │   ├── CreateEntityUseCase.java
        │   │   │   │   ├── CreateEntityCommand.java
        │   │   │   │   └── GetEntityUseCase.java
        │   │   │   └── out/
        │   │   │       ├── EntityRepository.java
        │   │   │       └── EventPublisher.java
        │   │   └── service/
        │   │       └── EntityService.java
        │   ├── infrastructure/
        │   │   ├── adapter/
        │   │   │   ├── in/
        │   │   │   │   └── rest/
        │   │   │   │       ├── EntityController.java
        │   │   │   │       ├── dto/
        │   │   │   │       │   ├── EntityRequest.java
        │   │   │   │       │   └── EntityResponse.java
        │   │   │   │       └── mapper/
        │   │   │   │           └── EntityRestMapper.java
        │   │   │   └── out/
        │   │   │       ├── persistence/
        │   │   │       │   ├── EntityJpaEntity.java
        │   │   │       │   ├── EntityJpaRepository.java
        │   │   │       │   ├── EntityRepositoryAdapter.java
        │   │   │       │   └── mapper/
        │   │   │       │       └── EntityPersistenceMapper.java
        │   │   │       └── messaging/
        │   │   │           └── RabbitMQEventPublisher.java
        │   │   ├── config/
        │   │   │   ├── RabbitMQConfig.java
        │   │   │   └── OpenApiConfig.java
        │   │   └── exception/
        │   │       └── GlobalExceptionHandler.java
        │   └── NewServiceApplication.java
        └── resources/
            └── application.yml
```

### 2. Product Service - Modelo de Dominio Sugerido

```java
// Product.java
public class Product {
    private UUID id;
    private String name;
    private String description;
    private Price price;
    private Integer stock;
    private Category category;
    private ProductStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

// Price.java (Value Object)
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
}

// Category.java
public enum Category {
    ELECTRONICS,
    CLOTHING,
    FOOD,
    BOOKS,
    OTHER
}
```

### 3. Order Service - Modelo de Dominio Sugerido

```java
// Order.java
public class Order {
    private UUID id;
    private UUID userId;
    private List<OrderItem> items;
    private Money totalAmount;
    private OrderStatus status;
    private Address shippingAddress;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public void addItem(OrderItem item) {
        items.add(item);
        recalculateTotal();
    }
    
    public void cancel() {
        if (status == OrderStatus.DELIVERED) {
            throw new BusinessException("Cannot cancel delivered order");
        }
        this.status = OrderStatus.CANCELLED;
    }
}

// OrderItem.java (Value Object)
public class OrderItem {
    private final UUID productId;
    private final String productName;
    private final Integer quantity;
    private final Money unitPrice;
    
    public Money getSubtotal() {
        return unitPrice.multiply(quantity);
    }
}

// OrderStatus.java
public enum OrderStatus {
    PENDING,
    CONFIRMED,
    PROCESSING,
    SHIPPED,
    DELIVERED,
    CANCELLED
}
```

### 4. Configuración de Puertos

- **Product Service**: 8082
- **Order Service**: 8083

### 5. Configuración de RabbitMQ

Cada servicio debe tener su propio exchange y queues:

**Product Service:**
```yaml
rabbitmq:
  exchange:
    product: product.exchange
  queue:
    product-created: product.created.queue
    product-updated: product.updated.queue
  routing-key:
    product-created: product.created
    product-updated: product.updated
```

**Order Service:**
```yaml
rabbitmq:
  exchange:
    order: order.exchange
  queue:
    order-created: order.created.queue
    order-updated: order.updated.queue
  routing-key:
    order-created: order.created
    order-updated: order.updated
```

### 6. Actualizar API Gateway

Agregar rutas en `api-gateway/src/main/resources/application.yml`:

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: product-service
          uri: lb://product-service
          predicates:
            - Path=/api/products/**
          filters:
            - RewritePath=/api/products/(?<segment>.*), /${segment}
            - name: CircuitBreaker
              args:
                name: productServiceCircuitBreaker
                fallbackUri: forward:/fallback/products
```

### 7. Actualizar Parent POM

Agregar módulos en `pom.xml`:

```xml
<modules>
    <module>service-discovery</module>
    <module>api-gateway</module>
    <module>config-server</module>
    <module>user-service</module>
    <module>product-service</module>
    <module>order-service</module>
    <module>common</module>
</modules>
```

### 8. Comunicación entre Microservicios

**Order Service necesita comunicarse con User Service y Product Service:**

```java
// En Order Service - crear un cliente Feign
@FeignClient(name = "user-service")
public interface UserServiceClient {
    @GetMapping("/users/{id}")
    UserDto getUserById(@PathVariable UUID id);
}

@FeignClient(name = "product-service")
public interface ProductServiceClient {
    @GetMapping("/products/{id}")
    ProductDto getProductById(@PathVariable UUID id);
}
```

Agregar dependencia en `order-service/pom.xml`:

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```

Y habilitar Feign en la aplicación:

```java
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class OrderServiceApplication {
    // ...
}
```

## Checklist para Nuevo Microservicio

- [ ] Crear estructura de carpetas
- [ ] Configurar pom.xml con dependencias
- [ ] Crear entidades de dominio
- [ ] Crear value objects
- [ ] Definir puertos de entrada (use cases)
- [ ] Definir puertos de salida (repositories, publishers)
- [ ] Implementar servicios de aplicación
- [ ] Crear REST controller
- [ ] Crear DTOs de request/response
- [ ] Crear mappers (MapStruct)
- [ ] Crear JPA entities
- [ ] Crear JPA repository
- [ ] Crear repository adapter
- [ ] Crear event publisher
- [ ] Configurar RabbitMQ
- [ ] Configurar OpenAPI/Swagger
- [ ] Crear global exception handler
- [ ] Configurar application.yml
- [ ] Crear Dockerfile
- [ ] Actualizar docker-compose.yml
- [ ] Actualizar API Gateway routes
- [ ] Escribir tests unitarios
- [ ] Escribir tests de integración

## Tips Importantes

1. **Siempre empieza por el dominio**: Define primero tus entidades y value objects
2. **Mantén el dominio puro**: No uses anotaciones de Spring/JPA en el dominio
3. **Usa Value Objects**: Para conceptos que no tienen identidad (Email, Price, Address)
4. **Valida en el dominio**: La lógica de validación va en las entidades y value objects
5. **Puertos antes que adaptadores**: Define las interfaces antes de implementarlas
6. **Un adaptador por tecnología**: Separa REST, JPA, RabbitMQ en adaptadores distintos
7. **Mappers para conversión**: Usa MapStruct para convertir entre capas
8. **Tests del dominio primero**: El dominio debe ser fácil de testear sin Spring

## Ejemplo Completo: Crear Product Service

Puedes copiar toda la estructura de `user-service` y reemplazar:
- `user` → `product`
- `User` → `Product`
- `Email` → `Price`
- `Address` → `Category`
- Puerto `8081` → `8082`

Luego ajusta la lógica de negocio según las necesidades de productos.
