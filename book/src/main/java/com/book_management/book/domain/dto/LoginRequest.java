package com.book_management.book.domain.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoginRequest {
    private String email;
    private String userPassword;
}
