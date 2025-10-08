package com.pavel.store.controller.rest;


import com.pavel.store.dto.request.UserRegistrationDto;
import com.pavel.store.dto.request.UserUpdateDto;
import com.pavel.store.dto.response.UserResponseDto;
import com.pavel.store.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(path = "api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User API", description = "Операции с пользователями")
public class UserController {

    private final UserService userService;


    @Operation(summary = "Get all users with pagination")
    @GetMapping
    public ResponseEntity<Page<UserResponseDto>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "username") String sort
    ) {
        Sort sortBy = Sort.by(sort);
        Pageable pageable = PageRequest.of(page, size, sortBy);
        Page<UserResponseDto> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(users);
    }


    @Operation(summary = "Get user by username")
    @GetMapping("/username")
    public ResponseEntity<UserResponseDto> getUserByUsername(
            @RequestParam String username
    ) {
        UserResponseDto user = userService.getUserByUsername(username);
        return ResponseEntity.ok(user);
    }


    @Operation(summary = "get User by id")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {

        return ResponseEntity.ok(userService.getUserById(id));
    }


    @Operation(summary = "create user")
    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody UserRegistrationDto user) {

        return ResponseEntity.ok(userService.createUser(user));
    }

    @Transactional
    @Operation(summary = "delete user")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
    }

    @Transactional
    @PutMapping("/{id}")
    @Operation(summary = "delete user")
    public ResponseEntity<UserResponseDto> updateUser(@RequestBody @Valid UserUpdateDto updateDto, @PathVariable Long id) {

        return ResponseEntity.ok(userService.updateUser(updateDto, id));
    }

}
