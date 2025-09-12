package com.pavel.store.controller;


import com.pavel.store.dto.request.UserRegistrationDto;
import com.pavel.store.dto.response.UserResponseDto;
import com.pavel.store.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(path = "api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    @GetMapping()
    public ResponseEntity<?> getUsers(
            @RequestParam(required = false) String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "username") String sort
    ) {
        if (username != null) {
            // Вернуть одного пользователя
            UserResponseDto user = userService.getUserByUsername(username);
            return ResponseEntity.ok(user);
        } else {
            var sortBy = Sort.by(sort);
            Pageable pageable = PageRequest.of(page, size, sortBy);
            return ResponseEntity.ok(userService.getAllUsers(pageable));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {

        return ResponseEntity.ok(userService.getUserById(id));
    }


    @PostMapping
    public ResponseEntity<UserRegistrationDto> createUser(@Valid @RequestBody UserRegistrationDto user) {

        return ResponseEntity.ok(userService.createUser(user));
    }


}
