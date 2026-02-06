package com.ordersystem.user.application.port.out;

import com.ordersystem.user.domain.event.UserCreatedEvent;

/**
 * Output Port - Event Publisher
 */
public interface EventPublisher {
    void publishUserCreatedEvent(UserCreatedEvent event);
}
