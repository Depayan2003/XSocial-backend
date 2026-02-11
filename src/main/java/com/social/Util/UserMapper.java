package com.social.Util;

import com.social.DTO.UserResponseDTO;
import com.social.Model.User;

public class UserMapper {

    public static UserResponseDTO toDto(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getProfileImageUrl(),
                user.getRole(),
                user.isOnline(),
                user.getLastSeen(),
                user.getCreatedAt()
        );
    }
}
