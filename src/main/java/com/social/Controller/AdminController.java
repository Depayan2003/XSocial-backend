package com.social.Controller;

import java.util.Map;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.social.Model.User;
import com.social.Repository.UserRepository;
import com.social.Service.AdminService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final UserRepository userRepository;

    @PostMapping("/users/{id}/warn")
    public void warnUser(
            @PathVariable Long id,
            @RequestBody Map<String, String> body
    ) {
        User target = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));

        adminService.warnUser(target, body.get("reason"));
    }

    @DeleteMapping("/users/{id}")
    public void disableUser(
            @PathVariable Long id,
            @RequestBody Map<String, String> body
    ) {
        User target = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));

        adminService.disableUser(target, body.get("reason"));
    }
}
