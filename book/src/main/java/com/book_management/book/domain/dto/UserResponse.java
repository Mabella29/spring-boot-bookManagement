package com.book_management.book.domain.dto;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
    private UUID userId;
    private String userName;
    private String email;
    private  String roleName;
    private Instant createdAt;

    public static UserResponse from (UserDto user){
        return UserResponse.builder()
                .userId(user.getUserId())
                .userName(user.getUserName())
                .email(user.getEmail())
                .roleName(user.getRoleName())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
