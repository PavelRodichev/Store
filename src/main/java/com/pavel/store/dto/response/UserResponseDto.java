package com.pavel.store.dto.response;

import com.pavel.store.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponseDto {

    private Long id;

    private String username;

    private String email;

    private String firstName;

    private String lastName;

    private Role role;

    private LocalDateTime createdAt;

}
