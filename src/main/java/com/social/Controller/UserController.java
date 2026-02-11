package com.social.Controller;

import com.social.DTO.AdminMessageDTO;
import com.social.DTO.UserResponseDTO;
import com.social.Service.AdminService;
import com.social.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AdminService adminService;

    /**
     * 🔐 Get logged-in user's profile
     */
    @GetMapping("/me")
    public UserResponseDTO getMyProfile() {
        return userService.getCurrentUser();
    }

    /**
     * 🔐 Get user by ID
     */
    @GetMapping("/{id}")
    public UserResponseDTO getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    /**
     * 🔐 Search users by name or email
     * Example: /users/search?q=depayan
     */
    @GetMapping("/search")
    public List<UserResponseDTO> searchUsers(
            @RequestParam("query") String query
    ) {
        return userService.searchUsers(query);
    }

    /**
     * 🔐 Update logged-in user's profile
     */
    @PutMapping("/me")
    public UserResponseDTO updateProfile(
            @RequestBody Map<String, String> body
    ) {
        String name = body.get("name");
        String profileImageUrl = body.get("profileImageUrl");

        return userService.updateProfile(name, profileImageUrl);
    }
    
    @GetMapping("/me/admin-messages")
    public List<AdminMessageDTO> myAdminMessages() {
        return adminService.getMyAdminMessages(userService.getCurrentUserEntity());
    }

}
