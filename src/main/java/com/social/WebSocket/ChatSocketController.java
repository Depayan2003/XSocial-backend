package com.social.WebSocket;

import com.social.DTO.ChatMessageDTO;
import com.social.DTO.MessageResponseDTO;
import com.social.DTO.SeenMessageDTO;
import com.social.Model.Conversation;
import com.social.Model.Message;
import com.social.Model.User;
import com.social.Model.Enums.MessageStatus;
import com.social.Repository.ConversationRepository;
import com.social.Repository.MessageRepository;
import com.social.Repository.UserRepository;
import com.social.Service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatSocketController {

    private final MessageService messageService;
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

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

    /* ===================== SEND MESSAGE ===================== */

    @MessageMapping("/chat.send")
    public void sendMessage(ChatMessageDTO dto, Principal principal) {

        String email = principal.getName();

        User sender = userRepository.findByEmail(email)
                .orElseThrow();

        Conversation conversation =
                conversationRepository.findByIdWithParticipants(
                        dto.getConversationId()
                ).orElseThrow();

        Message saved = messageService.sendMessage(
                conversation,
                sender,
                dto.getContent(),
                dto.getMediaUrl(),
                dto.getMessageType()
        );

        MessageResponseDTO response = toDto(saved);

        // ✅ SEND ONCE, ONLY TO ACTIVE PARTICIPANTS
        conversation.getParticipants().stream()
            .filter(cp -> !cp.isDeleted())
            .forEach(cp ->
                messagingTemplate.convertAndSendToUser(
                    cp.getUser().getEmail(),
                    "/queue/messages",
                    response
                )
            );
    }

    /* ===================== SEEN ===================== */

    @MessageMapping("/chat.seen")
    public void markSeen(SeenMessageDTO dto, Principal principal) {

        String viewerEmail = principal.getName();

        messageService.markConversationSeen(
                dto.getConversationId(),
                viewerEmail
        );

        Conversation conversation =
                conversationRepository.findByIdWithParticipants(
                        dto.getConversationId()
                ).orElseThrow();

        conversation.getParticipants().stream()
            .filter(cp -> !cp.isDeleted())
            .filter(cp -> !cp.getUser().getEmail().equals(viewerEmail))
            .forEach(cp ->
                messagingTemplate.convertAndSendToUser(
                    cp.getUser().getEmail(),
                    "/queue/seen",
                    dto.getConversationId()
                )
            );
    }
}
