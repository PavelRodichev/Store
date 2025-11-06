package com.pavel.store.controller.rest;


import com.pavel.store.dto.request.UserRegistrationDto;
import com.pavel.store.dto.request.UserUpdateDto;
import com.pavel.store.dto.response.PageResponse;
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
import org.springframework.data.web.PageableDefault;
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


    @Transactional
    @PostMapping()
    public ResponseEntity<UserResponseDto> registration(@RequestBody @Valid UserRegistrationDto userRegistrationDto) {
        var userResponse = userService.createUser(userRegistrationDto);
        return ResponseEntity.ok(userResponse);

    }

    @Operation(summary = "Get all users with pagination")
    @GetMapping
    public ResponseEntity<PageResponse<UserResponseDto>> getAllUsers(
            @PageableDefault(size = 20) Pageable pageable
    ) {
        Page<UserResponseDto> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(PageResponse.of(users));
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
