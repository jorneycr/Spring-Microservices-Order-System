# Template for Creating New Microservices

This guide will help you create Product Service and Order Service following the User Service pattern.

## Steps to Create a New Microservice

### 1. Create module structure

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

### 2. Product Service - Suggested Domain Model

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

### 3. Order Service - Suggested Domain Model

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

### 4. Port Configuration

- **Product Service**: 8082
- **Order Service**: 8083

### 5. RabbitMQ Configuration

Each service should have its own exchange and queues:

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

### 6. Update API Gateway

Add routes in `api-gateway/src/main/resources/application.yml`:

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

### 7. Update Parent POM

Add modules in `pom.xml`:

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

### 8. Communication Between Microservices

**Order Service needs to communicate with User Service and Product Service:**

```java
// In Order Service - create a Feign client
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

Add dependency in `order-service/pom.xml`:

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```

And enable Feign in the application:

```java
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class OrderServiceApplication {
    // ...
}
```

## Checklist for New Microservice

- [ ] Create folder structure
- [ ] Configure pom.xml with dependencies
- [ ] Create domain entities
- [ ] Create value objects
- [ ] Define input ports (use cases)
- [ ] Define output ports (repositories, publishers)
- [ ] Implement application services
- [ ] Create REST controller
- [ ] Create request/response DTOs
- [ ] Create mappers (MapStruct)
- [ ] Create JPA entities
- [ ] Create JPA repository
- [ ] Create repository adapter
- [ ] Create event publisher
- [ ] Configure RabbitMQ
- [ ] Configure OpenAPI/Swagger
- [ ] Create global exception handler
- [ ] Configure application.yml
- [ ] Create Dockerfile
- [ ] Update docker-compose.yml
- [ ] Update API Gateway routes
- [ ] Write unit tests
- [ ] Write integration tests

## Important Tips

1. **Always start with the domain**: Define your entities and value objects first
2. **Keep the domain pure**: Don't use Spring/JPA annotations in the domain
3. **Use Value Objects**: For concepts that don't have identity (Email, Price, Address)
4. **Validate in the domain**: Validation logic goes in entities and value objects
5. **Ports before adapters**: Define interfaces before implementing them
6. **One adapter per technology**: Separate REST, JPA, RabbitMQ into different adapters
7. **Mappers for conversion**: Use MapStruct to convert between layers
8. **Domain tests first**: The domain should be easy to test without Spring

## Complete Example: Create Product Service

You can copy the entire structure from `user-service` and replace:
- `user` → `product`
- `User` → `Product`
- `Email` → `Price`
- `Address` → `Category`
- Port `8081` → `8082`

Then adjust the business logic according to product needs.
