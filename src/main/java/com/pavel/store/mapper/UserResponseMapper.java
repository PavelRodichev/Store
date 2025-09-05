package com.pavel.store.mapper;

import com.pavel.store.dto.UserCreateDto;
import com.pavel.store.dto.UserResponseDto;
import com.pavel.store.dto.UserUpdateDto;
import com.pavel.store.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserResponseMapper implements Mapper<User, UserResponseDto> {


    @Override
    public UserResponseDto mapTo(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User entity cannot be null");
        }

        return UserResponseDto.builder() // ← Убрал new UserResponseDto()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build();

    }


}
