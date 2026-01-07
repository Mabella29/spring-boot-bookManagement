package com.book_management.book.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "Roles", schema = "book_management")
public class RoleDto {

    @Id
    @JsonProperty("roleUuid")
    @Column("RoleUuid")
    private UUID roleUuid;

    @JsonProperty("roleName")
    @Column("RoleName")
    private String roleName;

    @JsonProperty("isActive")
    @Column("IsActive")
    private boolean isActive;
}