package com.social.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MessageDeliveredEvent {
    private Long messageId;
    private Long conversationId;
}
