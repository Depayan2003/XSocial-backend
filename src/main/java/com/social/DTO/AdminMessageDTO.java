package com.social.DTO;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AdminMessageDTO {
    private String action;   // WARN or DISABLE
    private String reason;
    private LocalDateTime createdAt;
}
