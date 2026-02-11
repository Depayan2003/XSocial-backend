package com.social.Event;

import com.social.Model.User;
import lombok.Getter;

import java.util.Set;

@Getter
public class ConversationChangedEvent {

    private final Long conversationId;
    private final Set<User> users;

    public ConversationChangedEvent(Long conversationId, Set<User> users) {
        this.conversationId = conversationId;
        this.users = users;
    }
}
