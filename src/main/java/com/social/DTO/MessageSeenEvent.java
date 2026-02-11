package com.social.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MessageSeenEvent {
    private Long conversationId;
}
