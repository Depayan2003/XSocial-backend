package com.social.Controller;

import com.social.DTO.ConversationEvent;
import com.social.DTO.CreateGroupDTO;
import com.social.Model.Conversation;
import com.social.Model.User;
import com.social.Repository.UserRepository;
import com.social.Service.ConversationService;
import lombok.RequiredArgsConstructor;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/conversations")
@RequiredArgsConstructor
public class ConversationController {

    private final ConversationService conversationService;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;
    
    private User getCurrentUser() {

        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();

        String email = auth.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
    
    @PostMapping("/group")
    public Conversation createGroup(@RequestBody CreateGroupDTO dto) {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User creator = userRepository.findByEmail(email).orElseThrow();

        Set<User> participants = userRepository
                .findAllById(dto.getParticipantIds())
                .stream()
                .collect(Collectors.toSet());

        participants.add(creator);

        Conversation group = conversationService.createGroup(
                dto.getName(),
                participants,
                dto.getGroupImageUrl()
        );

        // 🔥 REAL-TIME NOTIFY PARTICIPANTS
        for (User u : participants) {
            messagingTemplate.convertAndSendToUser(
                    u.getEmail(),
                    "/queue/conversations",
                    new ConversationEvent(group.getId())
            );
        }

        return group;
    }

    
    /**
     * 🔐 Remove a participant from a group (admin only)
     */
    @DeleteMapping("/groups/{groupId}/participants/{userId}")
    public void removeParticipant(
            @PathVariable Long groupId,
            @PathVariable Long userId
    ) {
        conversationService.removeParticipant(
                groupId,
                userId,
                getCurrentUser()
        );
    }

    /**
     * 🔐 Leave group
     */
    @PostMapping("/groups/{groupId}/leave")
    public void leaveGroup(@PathVariable Long groupId) {
        conversationService.leaveGroup(
                groupId,
                getCurrentUser()
        );
    }
    
    /**
     * 🔐 Delete group (admin only)
     */
    @DeleteMapping("/groups/{groupId}")
    public void deleteGroup(@PathVariable Long groupId) {
        conversationService.deleteGroup(
                groupId,
                getCurrentUser()
        );
    }


}
