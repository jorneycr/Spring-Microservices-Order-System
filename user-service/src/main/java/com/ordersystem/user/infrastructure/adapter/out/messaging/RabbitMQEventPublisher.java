package com.ordersystem.user.infrastructure.adapter.out.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ordersystem.user.application.port.out.EventPublisher;
import com.ordersystem.user.domain.event.UserCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * RabbitMQ Event Publisher - Output Adapter
 * Implementa el puerto de salida EventPublisher
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitMQEventPublisher implements EventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    @Value("${rabbitmq.exchange.user}")
    private String userExchange;

    @Value("${rabbitmq.routing-key.user-created}")
    private String userCreatedRoutingKey;

    @Override
    public void publishUserCreatedEvent(UserCreatedEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            rabbitTemplate.convertAndSend(userExchange, userCreatedRoutingKey, message);
            log.info("Published UserCreatedEvent for user ID: {}", event.getUserId());
        } catch (JsonProcessingException e) {
            log.error("Error publishing UserCreatedEvent", e);
            throw new RuntimeException("Failed to publish event", e);
        }
    }
}
