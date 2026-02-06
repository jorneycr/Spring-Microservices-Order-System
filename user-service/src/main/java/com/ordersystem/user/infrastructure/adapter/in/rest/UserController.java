package com.ordersystem.user.infrastructure.adapter.in.rest;

import com.ordersystem.common.dto.ApiResponse;
import com.ordersystem.common.exception.ResourceNotFoundException;
import com.ordersystem.user.application.port.in.CreateUserCommand;
import com.ordersystem.user.application.port.in.CreateUserUseCase;
import com.ordersystem.user.application.port.in.GetUserUseCase;
import com.ordersystem.user.domain.model.User;
import com.ordersystem.user.infrastructure.adapter.in.rest.dto.UserRequest;
import com.ordersystem.user.infrastructure.adapter.in.rest.dto.UserResponse;
import com.ordersystem.user.infrastructure.adapter.in.rest.mapper.UserRestMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * REST Controller - Input Adapter
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "APIs for managing users")
public class UserController {

    private final CreateUserUseCase createUserUseCase;
    private final GetUserUseCase getUserUseCase;
    private final UserRestMapper mapper;

    @PostMapping
    @Operation(summary = "Create a new user")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@Valid @RequestBody UserRequest request) {
        CreateUserCommand command = mapper.toCommand(request);
        User user = createUserUseCase.createUser(command);
        UserResponse response = mapper.toResponse(user);
        
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("User created successfully", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable UUID id) {
        User user = getUserUseCase.getUserById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id.toString()));
        
        UserResponse response = mapper.toResponse(user);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    @Operation(summary = "Get all users")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        List<UserResponse> users = getUserUseCase.getAllUsers()
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Get user by email")
    public ResponseEntity<ApiResponse<UserResponse>> getUserByEmail(@PathVariable String email) {
        User user = getUserUseCase.getUserByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", email));
        
        UserResponse response = mapper.toResponse(user);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
