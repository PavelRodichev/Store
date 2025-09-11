package com.pavel.store.service;


import com.pavel.store.dto.request.UserRegistrationDto;
import com.pavel.store.dto.response.UserResponseDto;
import com.pavel.store.entity.Role;
import com.pavel.store.entity.User;
import com.pavel.store.mapper.implMapper.UserMapperImpl;
import com.pavel.store.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        return userRepository.findById(id).map(userMapper::toDto).
                orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    @Transactional(readOnly = true)
    public List<UserResponseDto> getUserByUsername(String username) {
        List<User> users = userRepository.findAllByUsername(username);
        return users.stream().map(userMapper::toDto).toList();
    }

    @Transactional
    public UserRegistrationDto createUser(UserRegistrationDto userRegistrationDto) {
        User userSave = userMapper.toEntity(userRegistrationDto);
        userSave.setRole(Role.USER);
        userRepository.save(userSave);
        return userRegistrationDto;
    }

    @Transactional
    public void deleteUserById(Long id) {
        var user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not fount"));
        userRepository.delete(user);
    }

}
