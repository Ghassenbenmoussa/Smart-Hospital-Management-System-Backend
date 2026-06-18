package com.hospitalmanagement.mapper;

import com.hospitalmanagement.dto.response.UserResponse;
import com.hospitalmanagement.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.InjectionStrategy;

import java.util.stream.Collectors;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface UserMapper {

    @Mapping(target = "roles", expression = "java(mapRoles(user))")
    UserResponse toResponse(User user);

    default java.util.Set<String> mapRoles(User user) {
        if (user.getRoles() == null) return java.util.Collections.emptySet();
        return user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet());
    }
}
