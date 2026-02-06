package com.ordersystem.user.application.port.in;

import com.ordersystem.user.domain.model.User;

import java.util.UUID;

/**
 * Input Port - Create User Use Case
 */
public interface CreateUserUseCase {
    User createUser(CreateUserCommand command);
}
