package com.pavel.store.controller.rest;

import com.pavel.store.dto.request.UserLoginDto;
import com.pavel.store.dto.request.UserRegistrationDto;
import com.pavel.store.dto.response.JwtAuthenticationResponse;
import com.pavel.store.entity.Role;
import com.pavel.store.entity.User;
import com.pavel.store.events.UserRegisteredEvent;
import com.pavel.store.repository.UserRepository;
import com.pavel.store.security.jwt.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
@Slf4j
@Tag(name = "Аутентификация", description = "API для регистрации и входа")
public class AuthController {

    private AuthenticationManager authenticationManager;
    private UserRepository userRepository;
    private JwtTokenProvider jwtTokenProvider;
    private PasswordEncoder passwordEncoder;
    private KafkaTemplate<String, Object> kafkaTemplate;

    @PostMapping("/login")
    @Operation(summary = "Вход в систему", description = "Аутентификация пользователя и получение JWT токена")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Успешный вход", content = @Content(schema = @Schema(implementation = JwtAuthenticationResponse.class))),
            @ApiResponse(responseCode = "401", description = "Неверные учетные данные")
    })
    public ResponseEntity<?> authenticateUser(@RequestBody UserLoginDto userLoginDto) {
        // инициирует проверку пользователя
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userLoginDto.getEmail(), userLoginDto.getPassword()));
        // кладем authentication в контекст секьюрити
        SecurityContextHolder.getContext().setAuthentication(authentication);

        //генерируем токен
        String jwt = jwtTokenProvider.generateToken(authentication);

        // отправляем токен пользователю
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));

    }

    @Operation(summary = "Регистрация пользователя", description = "Создание нового аккаунта")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Пользователь успешно зарегистрирован"),
            @ApiResponse(responseCode = "400", description = "Пользователь с таким email уже существует")
    })
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRegistrationDto userRegistrationDto) {
        if (userRepository.existsByEmail(userRegistrationDto.getEmail())) {
            return ResponseEntity.badRequest().body("User with that email already exist");
        }

        Role userRole;
        try {
            userRole = (userRegistrationDto.getRole() != null && !userRegistrationDto.getRole().isEmpty())
                    ? Role.valueOf(userRegistrationDto.getRole().toUpperCase())
                    : Role.USER; // значение по умолчанию
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid role: " + userRegistrationDto.getRole());
        }

        User user = User.builder()
                .email(userRegistrationDto.getEmail())
                .username(userRegistrationDto.getUsername())
                .lastName(userRegistrationDto.getLastName())
                .firstName(userRegistrationDto.getFirstName())
                .password(passwordEncoder.encode(userRegistrationDto.getPassword()))
                .role(userRole)
                .build();

        User result = userRepository.save(user);


        UserRegisteredEvent userRegisteredEvent = new UserRegisteredEvent();
        userRegisteredEvent.setEmail(user.getEmail());
        userRegisteredEvent.setUsername(user.getUsername());
        userRegisteredEvent.setFirstName(user.getFirstName());
        userRegisteredEvent.setLastName(user.getLastName());
        userRegisteredEvent.setEvent("USER_REGISTERED");

        kafkaTemplate.send("events", userRegisteredEvent);

        log.info("The user's registration information was sent to the appropriate handler");

        //Создаёт базовый URI, используя текущий контекстный путь приложения и добавляет к базовому URI путь /api/users/{id}
        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/users/{id}")
                .buildAndExpand(result.getId()).toUri();
// возвращаем 201 статус и ссылку на юзера
        return ResponseEntity.created(location).body("User registered successfully");
    }


}

