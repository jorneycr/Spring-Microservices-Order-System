package com.ordersystem.user.application.port.in;

import com.ordersystem.user.domain.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Input Port - Get User Use Case
 */
public interface GetUserUseCase {
    Optional<User> getUserById(UUID id);
    Optional<User> getUserByEmail(String email);
    List<User> getAllUsers();
}
