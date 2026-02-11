package com.social.DTO;

import com.social.Model.Enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class UserResponseDTO {

    private Long id;
    private String email;
    private String name;
    private String profileImageUrl;
    private Role role;
    private boolean online;
    private LocalDateTime lastSeen;
    private LocalDateTime createdAt;
}
