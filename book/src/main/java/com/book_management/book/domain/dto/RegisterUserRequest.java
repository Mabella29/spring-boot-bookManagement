package com.book_management.book.domain.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterUserRequest {
    @NotBlank(message = "user name is required")
    private String userName;

    @NotBlank(message = "email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String userPassword;
}
