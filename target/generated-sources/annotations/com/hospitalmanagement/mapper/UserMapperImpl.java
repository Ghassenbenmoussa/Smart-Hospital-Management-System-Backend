package com.hospitalmanagement.mapper;

import com.hospitalmanagement.dto.response.UserResponse;
import com.hospitalmanagement.entity.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-18T15:00:32+0100",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.2 (Oracle Corporation)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserResponse toResponse(User user) {
        if ( user == null ) {
            return null;
        }

        UserResponse.UserResponseBuilder userResponse = UserResponse.builder();

        userResponse.id( user.getId() );
        userResponse.username( user.getUsername() );
        userResponse.email( user.getEmail() );
        userResponse.enabled( user.isEnabled() );
        userResponse.createdAt( user.getCreatedAt() );

        userResponse.roles( mapRoles(user) );

        return userResponse.build();
    }
}
