package com.book_management.book.domain.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoginRequest {
    @NotBlank
    @Email(message = "Email must be a valid email address")
    private String email;

    @NotBlank
    @Size(min = 8, message = "password should have a min of 8")
    private String userPassword;
}
