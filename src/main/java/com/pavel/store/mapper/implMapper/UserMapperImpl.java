package com.pavel.store.mapper.implMapper;

import com.pavel.store.dto.request.UserRegistrationDto;
import com.pavel.store.dto.request.UserUpdateDto;
import com.pavel.store.dto.response.UserResponseDto;
import com.pavel.store.entity.User;
import com.pavel.store.mapper.mapers.UserMapper;
import org.springframework.stereotype.Component;

@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserResponseDto toDto(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User entity cannot be null");
        }

        return UserResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build();
    }

    @Override
    public User toEntity(UserRegistrationDto registrationDto) {
        if (registrationDto == null) {
            throw new IllegalArgumentException("UserCreateDto  cannot be null");
        }
        return User.builder()
                .username(registrationDto.getUsername())
                .email(registrationDto.getEmail())
                .password(registrationDto.getPassword())
                .firstName(registrationDto.getFirstName())
                .lastName(registrationDto.getLastName())
                .build();
    }


    @Override
    public void updateEntity(UserUpdateDto updateDto, User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        user.setEmail(updateDto.getEmail());
        user.setFirstName(updateDto.getFirstName());
        user.setLastName(user.getLastName());

    }
}
