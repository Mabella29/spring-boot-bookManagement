package com.book_management.book.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.*;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users", schema = "book_management")
public class UserDto {

    @Id
    @JsonProperty("userId")
    @Column("UserId")
    private UUID userId;

    @JsonProperty("userName")
    @Column("UserName")
    private String userName;

    @JsonProperty("email")
    @Column("Email")
    private String email;

    @JsonProperty("userPassword")
    @Column("UserPassword")
    private String userPassword;

    @JsonProperty("roleUuid")
    @Column("RoleUuid")
    private UUID roleUuid;

    @JsonProperty("roleName")
    @Column("RoleName")
    private String roleName;

    @JsonProperty("active")
    @Column("Active")
    private boolean active;

    @JsonProperty("createdAt")
    @Column("CreatedAt")
    private Instant createdAt;

    @JsonProperty("updatedAt")
    @Column("UpdatedAt")
    private Instant updatedAt;
}