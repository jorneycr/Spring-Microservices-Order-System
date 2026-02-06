package com.ordersystem.user.infrastructure.adapter.out.persistence.mapper;

import com.ordersystem.user.domain.model.Address;
import com.ordersystem.user.domain.model.Email;
import com.ordersystem.user.domain.model.User;
import com.ordersystem.user.infrastructure.adapter.out.persistence.UserJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct Mapper for JPA entities
 */
@Mapper(componentModel = "spring")
public interface UserPersistenceMapper {

    @Mapping(target = "email", expression = "java(user.getEmail().getValue())")
    @Mapping(target = "street", expression = "java(user.getAddress() != null ? user.getAddress().getStreet() : null)")
    @Mapping(target = "city", expression = "java(user.getAddress() != null ? user.getAddress().getCity() : null)")
    @Mapping(target = "state", expression = "java(user.getAddress() != null ? user.getAddress().getState() : null)")
    @Mapping(target = "zipCode", expression = "java(user.getAddress() != null ? user.getAddress().getZipCode() : null)")
    @Mapping(target = "country", expression = "java(user.getAddress() != null ? user.getAddress().getCountry() : null)")
    UserJpaEntity toEntity(User user);

    default User toDomain(UserJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        return User.builder()
                .id(entity.getId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .email(new Email(entity.getEmail()))
                .phone(entity.getPhone())
                .address(Address.builder()
                        .street(entity.getStreet())
                        .city(entity.getCity())
                        .state(entity.getState())
                        .zipCode(entity.getZipCode())
                        .country(entity.getCountry())
                        .build())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
