package com.pavel.store.service;

import com.pavel.store.dto.response.UserResponseDto;
import com.pavel.store.entity.Role;
import com.pavel.store.entity.User;
import com.pavel.store.mapper.implMapper.UserMapperImpl;
import com.pavel.store.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapperImpl userMapper;

    @InjectMocks
    private UserService userService;

    private final Long TEST_ID = 1L;
    private final String TEST_EMAIL = "test@example.com";
    private final String TEST_PASSWORD = "password123";
    private final String TEST_USERNAME = "JohnDoe";
    private final String TEST_FIRSTNAME = "John";
    private final String TEST_LASTNAME = "Doe";


    @Test
    void getAllUsersShouldReturnPageUserResponseDto() {
        Pageable pageable = PageRequest.of(0, 5, Sort.by("username"));

        List<User> mockUsers = Arrays.asList(
                createMockUser(1L, TEST_EMAIL, TEST_USERNAME, TEST_FIRSTNAME, TEST_LASTNAME, TEST_PASSWORD),
                createMockUser(2L, "test2@example.com", "Test", "FirstTESt", "lastTEst", "passTest")
        );

        Page<User> mockPage = new PageImpl<>(mockUsers, pageable, 20);

        when(userRepository.findAll(pageable)).thenReturn(mockPage);

        when(userMapper.toDto(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0); // Получаем реальный User
            return new UserResponseDto(user.getId(), user.getUsername(), user.getEmail(), user.getFirstName(), user.getLastName(), Role.USER, LocalDateTime.now());
        });

        Page<UserResponseDto> result = userService.getAllUsers(pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(20);

        // Проверяем вызовы
        verify(userRepository).findAll(pageable);
        verify(userMapper, times(2)).toDto(any(User.class));

        // Проверяем маппинг
        UserResponseDto firstUser = result.getContent().get(0);
        assertThat(firstUser.getEmail()).isEqualTo(TEST_EMAIL);
    }

    private User createMockUser(Long id, String email, String username, String firstName, String lastname, String password) {
        return User.builder()
                .id(id)
                .email(email)
                .password(password)
                .firstName(firstName)
                .lastName(lastname)
                .username(username)
                .build();
    }

}
