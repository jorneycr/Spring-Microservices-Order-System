package com.ordersystem.user.infrastructure.adapter.in.rest.mapper;

import com.ordersystem.user.application.port.in.CreateUserCommand;
import com.ordersystem.user.domain.model.User;
import com.ordersystem.user.infrastructure.adapter.in.rest.dto.AddressDto;
import com.ordersystem.user.infrastructure.adapter.in.rest.dto.UserRequest;
import com.ordersystem.user.infrastructure.adapter.in.rest.dto.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct Mapper for REST DTOs
 */
@Mapper(componentModel = "spring")
public interface UserRestMapper {

    CreateUserCommand toCommand(UserRequest request);

    @Mapping(target = "email", expression = "java(user.getEmail().getValue())")
    @Mapping(target = "status", expression = "java(user.getStatus().name())")
    UserResponse toResponse(User user);

    AddressDto toAddressDto(com.ordersystem.user.domain.model.Address address);
}
