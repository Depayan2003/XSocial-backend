package com.social.DTO;

import com.social.Model.Enums.MessageStatus;
import com.social.Model.Enums.MessageType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class MessageResponseDTO {

    private Long id;
    private Long conversationId;
    private Long senderId;
    private String senderEmail;
    private String content;
    private String mediaUrl;
    private MessageType messageType;
    private MessageStatus status;
    private LocalDateTime createdAt;
}
