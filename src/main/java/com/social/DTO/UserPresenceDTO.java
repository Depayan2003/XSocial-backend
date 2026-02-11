package com.social.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class UserPresenceDTO {
	private String email;
    private boolean online;
    private LocalDateTime lastSeen;
}
