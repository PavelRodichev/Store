package com.pavel.store.service;


import com.pavel.store.events.UserRegisteredEvent;
import com.pavel.store.handler.exeption.EntityAlreadyExistsException;
import com.pavel.store.handler.exeption.EntityNotFoundException;
import com.pavel.store.dto.request.UserRegistrationDto;
import com.pavel.store.dto.request.UserUpdateDto;
import com.pavel.store.dto.response.UserResponseDto;
import com.pavel.store.entity.Role;
import com.pavel.store.entity.User;
import com.pavel.store.mapper.implMapper.UserMapperImpl;
import com.pavel.store.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
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

    @Transactional
    public User findUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User", id));
    }

    @Transactional(readOnly = true)
    public UserResponseDto getUserByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);

        return user.map(userMapper::toDto).orElseThrow(() -> new EntityNotFoundException(username));
    }


    @Transactional
    public UserResponseDto createUser(UserRegistrationDto userRegistrationDto) {
        if (userRepository.existsByEmail(userRegistrationDto.getEmail())) {
            throw new EntityAlreadyExistsException("User", "email", userRegistrationDto.getEmail());
        }
        if (userRepository.existsUserByUsername(userRegistrationDto.getUsername())) {
            throw new EntityAlreadyExistsException("User", "username", userRegistrationDto.getUsername());
        }

        User userSave = userMapper.toEntity(userRegistrationDto);
        userSave.setRole(Role.valueOf(userRegistrationDto.getRole()));

        log.info("User created");


        return userMapper.toDto(userRepository.saveAndFlush(userSave));
    }

    @Transactional
    public void deleteUserById(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException("User", id);
        }
    }

    @Transactional
    public UserResponseDto updateUser(UserUpdateDto updateDto, Long id) {
        var user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User", id));
        userMapper.updateEntity(updateDto, user);
        return userMapper.toDto(user);

    }

}
