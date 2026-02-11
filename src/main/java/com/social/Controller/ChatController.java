package com.social.Controller;

import com.social.DTO.MediaUploadResponse;
import com.social.DTO.MessageResponseDTO;
import com.social.Model.Conversation;
import com.social.Model.Message;
import com.social.Model.User;
import com.social.Model.Enums.MessageType;
import com.social.Repository.ConversationRepository;
import com.social.Repository.UserRepository;
import com.social.Service.ConversationService;
import com.social.Service.MediaService;
import com.social.Service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ConversationService conversationService;
    private final MessageService messageService;
    private final MediaService mediaService;
    private final UserRepository userRepository;
    private final ConversationRepository conversationRepository;

    /* ===================== 1–1 CONVERSATION ===================== */

    @PostMapping("/conversation/{userId}")
    public Conversation getOrCreateConversation(@PathVariable Long userId) {

        User currentUser = getCurrentUser();

        User otherUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return conversationService.getOrCreateConversation(
                currentUser,
                otherUser
        );
    }

    /* ===================== MY CONVERSATIONS ===================== */

    @GetMapping("/conversations")
    public List<Conversation> getMyConversations() {
        List<Conversation> list =
            conversationService.getUserConversations(getCurrentUser());

        list.forEach(c ->
            System.out.println(
                "SERIALIZE CHECK: conv=" + c.getId() +
                " participants=" + c.getParticipants().size()
            )
        );

        return list;
    }

    /* ===================== GET MESSAGES ===================== */
    
    @GetMapping("/messages/{conversationId}")
    public List<MessageResponseDTO> getConversationMessages(
            @PathVariable Long conversationId
    ) {
        Conversation conversation =
            conversationRepository.findByIdWithParticipants(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));

        User currentUser = getCurrentUser();

        // ✅ THIS IS THE FIX
        boolean isParticipant = conversation.getParticipants().stream()
            .anyMatch(cp ->
                cp.getUser().equals(currentUser) && !cp.isDeleted()
            );

        if (!isParticipant) {
            throw new RuntimeException("Access denied");
        }

        return messageService.getConversationMessages(conversation)
                .stream()
                .map(this::toDto)
                .toList();
    }

    /* ===================== SEND MESSAGE ===================== */

    @PostMapping("/messages/{conversationId}")
    public Message sendMessage(
            @PathVariable Long conversationId,
            @RequestBody Map<String, String> body
    ) {
        Conversation conversation =
            conversationRepository.findByIdWithParticipants(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));

        User sender = getCurrentUser();

        // ✅ SAME FIX HERE
        boolean isParticipant = conversation.getParticipants().stream()
            .anyMatch(cp ->
                cp.getUser().equals(sender) && !cp.isDeleted()
            );

        if (!isParticipant) {
            throw new RuntimeException("Access denied");
        }

        String content = body.get("content");
        String mediaUrl = body.get("mediaUrl");
        MessageType messageType =
            MessageType.valueOf(body.getOrDefault("messageType", "TEXT"));

        return messageService.sendMessage(
            conversation,
            sender,
            content,
            mediaUrl,
            messageType
        );
    }

    /* ===================== MEDIA UPLOAD ===================== */

    @PostMapping("/upload")
    public MediaUploadResponse uploadMedia(
            @RequestParam("file") MultipartFile file
    ) {
        return mediaService.upload(file);
    }

    /* ===================== DELETE MESSAGE ===================== */

    @DeleteMapping("/messages/{messageId}")
    public void deleteMessage(@PathVariable Long messageId) {
        messageService.deleteMessage(messageId, getCurrentUser());
    }

    /* ===================== DELETE CONVERSATION (SOFT) ===================== */

    @DeleteMapping("/conversations/{conversationId}")
    public void deleteConversation(@PathVariable Long conversationId) {
        conversationService.deleteConversationForUser(
                conversationId,
                getCurrentUser()
        );
    }

    /* ===================== UTIL ===================== */

    private MessageResponseDTO toDto(Message m) {
        MessageResponseDTO dto = new MessageResponseDTO();
        dto.setId(m.getId());
        dto.setConversationId(m.getConversation().getId());
        dto.setSenderId(m.getSender().getId());
        dto.setSenderEmail(m.getSender().getEmail());
        dto.setContent(m.getContent());
        dto.setMediaUrl(m.getMediaUrl());
        dto.setMessageType(m.getMessageType());
        dto.setStatus(m.getStatus());
        dto.setCreatedAt(m.getCreatedAt());
        return dto;
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();

        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
    
    /* ===================== MARK SEEN ===================== */

    @PostMapping("/conversations/{conversationId}/seen")
    public void markSeen(@PathVariable Long conversationId) {
        messageService.markConversationSeen(
            conversationId,
            getCurrentUser().getEmail()
        );
    }

    /* ===================== MARK DELIVERED ===================== */

    @PostMapping("/messages/{messageId}/delivered")
    public void markDelivered(@PathVariable Long messageId) {
        messageService.markDelivered(messageId);
    }

}
