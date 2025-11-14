package com.pavel.store.service;

import com.pavel.store.dto.request.UserRegistrationDto;
import com.pavel.store.dto.request.UserUpdateDto;
import com.pavel.store.dto.response.UserResponseDto;
import com.pavel.store.entity.Role;
import com.pavel.store.entity.User;
import com.pavel.store.handler.exeption.EntityAlreadyExistsException;
import com.pavel.store.handler.exeption.EntityNotFoundException;
import com.pavel.store.mapper.implMapper.UserMapperImpl;
import com.pavel.store.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapperImpl userMapper;
    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private UserService userService;

    private static final Long TEST_ID = 1L;
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_PASSWORD = "password123";
    private static final String TEST_USERNAME = "JohnDoe";
    private static final String TEST_FIRSTNAME = "John";
    private static final String TEST_LASTNAME = "Doe";


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
            return new UserResponseDto(user.getId(), user.getUsername(), user.getEmail(), user.getFirstName(), user.getLastName(), Role.USER, LocalDateTime.now(), null);
        });

        Page<UserResponseDto> result = userService.getAllUsers(pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(20);


        verify(userRepository).findAll(pageable);
        verify(userMapper, times(2)).toDto(any(User.class));


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

    @Test
    void ifInMethodFindUserByIdRepositoryReturnEmptyOptionalThanException() {
        when(userRepository.findById(TEST_ID)).thenReturn(Optional.empty());
        var result = assertThrows(EntityNotFoundException.class, () -> userService.findUserById(TEST_ID));
        assertThat(result.getMessage()).isEqualTo("Entity User with id 1 not found");
    }

    @Test
    void findUserByIdShouldBeReturnUser() {
        when(userRepository.findById(TEST_ID)).thenReturn(Optional.of(createValidUser()));

        var result = userService.findUserById(TEST_ID);

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo(createValidUser().getUsername());
        assertThat(result.getEmail()).isEqualTo(createValidUser().getEmail());
        assertThat(result.getId()).isEqualTo(createValidUser().getId());
        assertThat(result.getFirstName()).isEqualTo(createValidUser().getFirstName());
        assertThat(result.getLastName()).isEqualTo(createValidUser().getLastName());
    }

    @Test
    void getUserByUsernameShouldReturnUserResponseDto() {
        User testUser = createValidUser();
        UserResponseDto expected = createUserResponseDto();
        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(testUser));
        when(userMapper.toDto(testUser)).thenReturn(expected);

        var result = userService.getUserByUsername(TEST_USERNAME);
        assertThat(result).isNotNull().isEqualTo(expected);
        assertThat(result.getUsername()).isEqualTo(testUser.getUsername());

    }


    @Test
    void createUserShouldBeReturnUserResponseDto() {
        UserRegistrationDto userRegistrationDto = UserRegistrationDto.builder()
                .username("JohnDoe")
                .lastName("Doe")
                .firstName("John")
                .email("test@example.com")
                .role("USER")
                .build();
        User userSave = createValidUser();
        UserResponseDto expectedResponse = createUserResponseDto();

        when(userRepository.existsByEmail(userRegistrationDto.getEmail())).thenReturn(false);
        when(userRepository.existsUserByUsername(userRegistrationDto.getUsername())).thenReturn(false);
        when(userMapper.toEntity(userRegistrationDto)).thenReturn(userSave);
        when(userMapper.toDto(userSave)).thenReturn(expectedResponse);
        when(userRepository.saveAndFlush(userSave)).thenReturn(userSave);
        var result = userService.createUser(userRegistrationDto);

        assertThat(result).isNotNull().isEqualTo(expectedResponse);


        verify(userRepository).existsByEmail(userRegistrationDto.getEmail());
        verify(userRepository).existsUserByUsername(userRegistrationDto.getUsername());
        verify(userMapper).toEntity(userRegistrationDto);
        verify(userRepository).saveAndFlush(userSave);
        verify(userMapper).toDto(userSave);
        verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test
    void createUserShouldThrowExceptionWhenEmailExists() {
        UserRegistrationDto userRegistrationDto = UserRegistrationDto.builder()
                .username("JohnDoe")
                .lastName("Doe")
                .firstName("John")
                .email("test@example.com")
                .build();

        when(userRepository.existsByEmail(userRegistrationDto.getEmail())).thenReturn(true);

        var result = assertThrows(EntityAlreadyExistsException.class, () -> userService.createUser(userRegistrationDto));

        assertThat(result.getMessage())
                .isEqualTo(String.format("Entity %s with field %s and value %s already Exist",
                        "User", "email", userRegistrationDto.getEmail()));

    }

    @Test
    void createUserShouldThrowExceptionWhenUsernameExists() {
        UserRegistrationDto userRegistrationDto = UserRegistrationDto.builder()
                .username("JohnDoe")
                .lastName("Doe")
                .firstName("John")
                .email("test@example.com")
                .build();

        when(userRepository.existsUserByUsername(userRegistrationDto.getUsername())).thenReturn(true);


        var result = assertThrows(EntityAlreadyExistsException.class, () -> userService.createUser(userRegistrationDto));

        assertThat(result.getMessage())
                .isEqualTo(String.format("Entity %s with field %s and value %s already Exist",
                        "User", "username", userRegistrationDto.getUsername()));

    }

    @Test
    void deleteUserById_WhenUserExists_ShouldDeleteUser() {

        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true);


        userService.deleteUserById(userId);


        verify(userRepository).existsById(userId);
        verify(userRepository).deleteById(userId);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void updateUserShouldReturnUserResponseDto() {
        Long userId = 1L;
        UserUpdateDto updateDto = UserUpdateDto.builder()
                .lastName("Test")
                .firstName("Test")
                .email("test@mail.com")
                .build();
        User existingUser = createValidUser();
        UserResponseDto expectedResponse = createUserResponseDto();

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        doNothing().when(userMapper).updateEntity(updateDto, existingUser);
        when(userMapper.toDto(any(User.class))).thenReturn(expectedResponse);

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);



        UserResponseDto result = userService.updateUser(updateDto, userId);




        verify(userMapper).updateEntity(eq(updateDto), userArgumentCaptor.capture());
        User capturedUser = userArgumentCaptor.getValue();

        assertThat(capturedUser).isNotNull().isSameAs(existingUser);
        assertThat(result).isEqualTo(expectedResponse);

        verify(userRepository).findById(userId);
        verify(userMapper).updateEntity(updateDto, existingUser);
        verify(userMapper).toDto(existingUser);
        verifyNoMoreInteractions(userRepository, userMapper);


    }

    private static UserResponseDto createUserResponseDto() {
        return UserResponseDto.builder().id(1L)
                .username("JohnDoe")
                .lastName("Doe")
                .firstName("John")
                .email("test@example.com")
                .role(Role.USER)
                .build();

    }

    private static User createValidUser() {
        return User.builder().id(1L)
                .username("JohnDoe")
                .lastName("Doe")
                .firstName("John")
                .password("password123")
                .email("test@example.com")
                .role(Role.USER).build();
    }
}
