package com.social.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MessageDeletedEvent {
    private Long messageId;
    private Long conversationId;
    private boolean hard;
}
