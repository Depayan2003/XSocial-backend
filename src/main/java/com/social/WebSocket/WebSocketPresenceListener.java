package com.social.WebSocket;

import com.social.DTO.UserPresenceDTO;
import com.social.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class WebSocketPresenceListener {

    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @EventListener
    public void handleConnect(SessionConnectEvent event) {

        StompHeaderAccessor accessor =
                StompHeaderAccessor.wrap(event.getMessage());
        
        if (accessor.getUser() == null) {
                return;
        };

        String email = accessor.getUser().getName();
        
        userRepository.findByEmail(email).ifPresent(user -> {
            user.setOnline(true);
            userRepository.save(user);

            // 🔴 Broadcast ONLINE
            messagingTemplate.convertAndSend(
                    "/topic/presence",
                    new UserPresenceDTO(
                            user.getEmail(),
                            true,
                            null
                    )
            );
        });
    }

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {

        StompHeaderAccessor accessor =
                StompHeaderAccessor.wrap(event.getMessage());

        if (accessor.getUser() == null) return;

        String email = accessor.getUser().getName();

        userRepository.findByEmail(email).ifPresent(user -> {
            user.setOnline(false);
            user.setLastSeen(LocalDateTime.now());
            userRepository.save(user);

            // 🔴 Broadcast OFFLINE
            messagingTemplate.convertAndSend(
                    "/topic/presence",
                    new UserPresenceDTO(
                            user.getEmail(),
                            false,
                            user.getLastSeen()
                    )
            );
        });
    }
}
