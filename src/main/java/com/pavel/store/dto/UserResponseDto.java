package com.pavel.store.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponseDto {

    private Long id;

    private String email;

    private String firstName;

    private String lastName;


}
