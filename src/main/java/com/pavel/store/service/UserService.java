package com.pavel.store.service;


import com.pavel.store.controller.handler.exeption.EntityNotFoundException;
import com.pavel.store.dto.request.UserRegistrationDto;
import com.pavel.store.dto.request.UserUpdateDto;
import com.pavel.store.dto.response.UserResponseDto;
import com.pavel.store.entity.Role;
import com.pavel.store.entity.User;
import com.pavel.store.mapper.implMapper.UserMapperImpl;
import com.pavel.store.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final UserMapperImpl userMapper;

    @Transactional(readOnly = true)
    public Page<UserResponseDto> getAllUsers(Pageable pageable) {
        Page<User> userPage = userRepository.findAll(pageable);
        return userPage.map(userMapper::toDto);
    }

    @Transactional(readOnly = true)
    public UserResponseDto getUserById(Long id) {
        Optional<User> user = userRepository.findById(id);
        return user.map(userMapper::toDto).
                orElseThrow(() -> new EntityNotFoundException("User", id));
    }

    @Transactional(readOnly = true)
    public UserResponseDto getUserByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);

        return user.map(userMapper::toDto).orElseThrow(() -> new EntityNotFoundException(username));
    }

    @Transactional
    public UserRegistrationDto createUser(UserRegistrationDto userRegistrationDto) {
        User userSave = userMapper.toEntity(userRegistrationDto);
        userSave.setRole(Role.USER);
        userRepository.saveAndFlush(userSave);
        return userRegistrationDto;
    }

    @Transactional
    public void deleteUserById(Long id) {
        var user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User", id));
        userRepository.delete(user);
    }

    @Transactional
    public void updateUser(UserUpdateDto updateDto, Long id) {
        var user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User", id));
        userMapper.updateEntity(updateDto, user);
    }
}
