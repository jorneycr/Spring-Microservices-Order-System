package com.ordersystem.user.application.port.out;

import com.ordersystem.user.domain.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Output Port - User Repository
 */
public interface UserRepository {
    User save(User user);
    Optional<User> findById(UUID id);
    Optional<User> findByEmail(String email);
    List<User> findAll();
    void deleteById(UUID id);
    boolean existsByEmail(String email);
}
