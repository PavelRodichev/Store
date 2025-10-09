package com.pavel.store.service;


import com.pavel.store.aop.MethodTime;
import com.pavel.store.entity.CustomUserDetails;
import com.pavel.store.handler.exeption.EntityAlreadyExistsException;
import com.pavel.store.handler.exeption.EntityNotFoundException;
import com.pavel.store.dto.request.UserRegistrationDto;
import com.pavel.store.dto.request.UserUpdateDto;
import com.pavel.store.dto.response.UserResponseDto;
import com.pavel.store.entity.Role;
import com.pavel.store.entity.User;
import com.pavel.store.mapper.implMapper.UserMapperImpl;
import com.pavel.store.repository.UserRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

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
        userSave.setRole(Role.USER);
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

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        log.info("Loading user: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("User not found: {}", username);
                    return new UsernameNotFoundException("User not found: " + username);
                });

        // Преобразуем роли в authorities
        Collection<GrantedAuthority> authorities = new ArrayList<>();

        // Основная роль
        authorities.add(new SimpleGrantedAuthority(user.getRole().getAuthority()));


        log.info("User loaded: {}, authorities: {}", user.getUsername(), authorities);

        return new CustomUserDetails(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                authorities
        );

    }


}
