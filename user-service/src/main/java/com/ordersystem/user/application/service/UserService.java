package com.ordersystem.user.application.service;

import com.ordersystem.common.exception.BusinessException;
import com.ordersystem.user.application.port.in.CreateUserCommand;
import com.ordersystem.user.application.port.in.CreateUserUseCase;
import com.ordersystem.user.application.port.in.GetUserUseCase;
import com.ordersystem.user.application.port.out.EventPublisher;
import com.ordersystem.user.application.port.out.UserRepository;
import com.ordersystem.user.domain.event.UserCreatedEvent;
import com.ordersystem.user.domain.model.Address;
import com.ordersystem.user.domain.model.Email;
import com.ordersystem.user.domain.model.User;
import com.ordersystem.user.domain.model.UserStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * User Service - Application Layer
 * Implementa los casos de uso de usuario
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements CreateUserUseCase, GetUserUseCase {

    private final UserRepository userRepository;
    private final EventPublisher eventPublisher;

    @Override
    @Transactional
    public User createUser(CreateUserCommand command) {
        log.info("Creating user with email: {}", command.getEmail());

        // Validar que el email no exista
        if (userRepository.existsByEmail(command.getEmail())) {
            throw new BusinessException("USER_ALREADY_EXISTS", 
                "User with email " + command.getEmail() + " already exists");
        }

        // Crear el usuario
        User user = User.builder()
                .id(UUID.randomUUID())
                .firstName(command.getFirstName())
                .lastName(command.getLastName())
                .email(new Email(command.getEmail()))
                .phone(command.getPhone())
                .address(Address.builder()
                        .street(command.getStreet())
                        .city(command.getCity())
                        .state(command.getState())
                        .zipCode(command.getZipCode())
                        .country(command.getCountry())
                        .build())
                .status(UserStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Guardar el usuario
        User savedUser = userRepository.save(user);

        // Publicar evento de dominio
        UserCreatedEvent event = UserCreatedEvent.builder()
                .userId(savedUser.getId())
                .email(savedUser.getEmail().getValue())
                .fullName(savedUser.getFullName())
                .occurredAt(LocalDateTime.now())
                .build();
        
        eventPublisher.publishUserCreatedEvent(event);

        log.info("User created successfully with ID: {}", savedUser.getId());
        return savedUser;
    }

    @Override
    public Optional<User> getUserById(UUID id) {
        log.info("Fetching user by ID: {}", id);
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        log.info("Fetching user by email: {}", email);
        return userRepository.findByEmail(email);
    }

    @Override
    public List<User> getAllUsers() {
        log.info("Fetching all users");
        return userRepository.findAll();
    }
}
