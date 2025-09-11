package com.pavel.store.mapper.mapers;

import com.pavel.store.dto.request.UserRegistrationDto;
import com.pavel.store.dto.request.UserUpdateDto;
import com.pavel.store.dto.response.UserResponseDto;
import com.pavel.store.entity.User;

public interface UserMapper {

    UserResponseDto toDto(User user);
    User toEntity(UserRegistrationDto registrationDto);
    void updateEntity(UserUpdateDto updateDto, User user);

}
