package com.social.Event;

import com.social.DTO.ConversationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.event.TransactionPhase;

@Component
@RequiredArgsConstructor
public class ConversationEventListener {

    private final SimpMessagingTemplate messagingTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleConversationChanged(ConversationChangedEvent event) {

        ConversationEvent payload =
                new ConversationEvent(event.getConversationId());

        event.getUsers().forEach(user ->
            messagingTemplate.convertAndSendToUser(
                user.getEmail(),
                "/queue/conversations",
                payload
            )
        );
    }
}
