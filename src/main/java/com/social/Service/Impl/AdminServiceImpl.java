package com.social.Service.Impl;

import com.social.DTO.AdminMessageDTO;
import com.social.Model.AdminAction;
import com.social.Model.User;
import com.social.Repository.AdminActionRepository;
import com.social.Repository.UserRepository;
import com.social.Service.AdminService;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final AdminActionRepository actionRepository;

    private User getAdminFromContext() {
        Authentication auth =
            SecurityContextHolder.getContext().getAuthentication();

        if (auth == null) {
            throw new RuntimeException("Unauthenticated");
        }

        User admin = userRepository.findByEmail(auth.getName())
            .orElseThrow(() -> new RuntimeException("Admin not found"));

        if (!"ADMIN".equals(admin.getRole().toString())) {
            throw new RuntimeException("Admin only");
        }

        return admin;
    }

    @Override
    public void disableUser(User target, String reason) {

        User admin = getAdminFromContext();

        target.setEnabled(false);
        userRepository.save(target);

        AdminAction action = new AdminAction();
        action.setAdmin(admin);
        action.setTargetUser(target);
        action.setAction("DISABLE");
        action.setReason(reason);
        action.setCreatedAt(LocalDateTime.now());

        actionRepository.save(action);
    }

    @Override
    public void warnUser(User target, String reason) {

        User admin = getAdminFromContext();

        AdminAction action = new AdminAction();
        action.setAdmin(admin);
        action.setTargetUser(target);
        action.setAction("WARN");
        action.setReason(reason);
        action.setCreatedAt(LocalDateTime.now());

        actionRepository.save(action);
    }
    
    @Override
    public List<AdminMessageDTO> getMyAdminMessages(User user) {

        return actionRepository
            .findByTargetUserAndActionIn(
                user, List.of("WARN", "DISABLE")
            )
            .stream()
            .map(a -> {
                AdminMessageDTO dto = new AdminMessageDTO();
                dto.setAction(a.getAction());
                dto.setReason(a.getReason());
                dto.setCreatedAt(a.getCreatedAt());
                return dto;
            })
            .toList();
    }

}
