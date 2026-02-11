package com.social.DTO;

import com.social.Model.Enums.MessageStatus;
import com.social.Model.Enums.MessageType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ChatMessageDTO {

    private Long id;
    private Long conversationId;
    private String content;
    private String mediaUrl;
    private MessageType messageType;
    private MessageStatus status;
    private String senderEmail;
    private LocalDateTime createdAt;

    // Factory method (VERY IMPORTANT)
    public static ChatMessageDTO from(com.social.Model.Message message) {
        ChatMessageDTO dto = new ChatMessageDTO();
        dto.setId(message.getId());
        dto.setConversationId(message.getConversation().getId());
        dto.setContent(message.getContent());
        dto.setMediaUrl(message.getMediaUrl());
        dto.setMessageType(message.getMessageType());
        dto.setStatus(message.getStatus());
        dto.setSenderEmail(message.getSender().getEmail());
        dto.setCreatedAt(message.getCreatedAt());
        return dto;
    }
}
