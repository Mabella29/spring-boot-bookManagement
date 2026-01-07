package com.book_management.book.domain.dto;


import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoginResponse {
    private String token;
    private String userName;
    private String role;
}
