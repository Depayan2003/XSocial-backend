package com.social.Service.Impl;

import com.social.DTO.UserResponseDTO;
import com.social.Model.User;
import com.social.Repository.UserRepository;
import com.social.Service.UserService;
import com.social.Util.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    
    @Override
    public User getCurrentUserEntity() {

        Authentication auth =
            SecurityContextHolder.getContext().getAuthentication();

        String email = auth.getName();

        return userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // 🔐 Get logged-in user
    @Override
    public UserResponseDTO getCurrentUser() {

        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();

        String email = auth.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return UserMapper.toDto(user);
    }

    // 🔐 Get user by ID
    @Override
    public UserResponseDTO getUserById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return UserMapper.toDto(user);
    }

    // 🔐 Search users
    @Override
    public List<UserResponseDTO> searchUsers(String query) {

        return userRepository
                .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                        query, query
                )
                .stream()
                .map(UserMapper::toDto)
                .toList();
    }

    // 🔐 Update profile (NO bio)
    @Override
    public UserResponseDTO updateProfile(
            String name,
            String profileImageUrl
    ) {
        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();

        String email = auth.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // ✅ update name only if provided
        if (name != null && !name.trim().isEmpty()) {
            user.setName(name.trim());
        }

        // ✅ update image only if provided
        if (profileImageUrl != null && !profileImageUrl.trim().isEmpty()) {
            user.setProfileImageUrl(profileImageUrl.trim());
        }

        userRepository.save(user);

        return UserMapper.toDto(user);
    }

}
