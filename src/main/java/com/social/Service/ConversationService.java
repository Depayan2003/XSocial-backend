package com.social.Service;

import com.social.Model.Conversation;
import com.social.Model.User;

import java.util.List;
import java.util.Set;

public interface ConversationService {

    Conversation getOrCreateConversation(User u1, User u2);

    List<Conversation> getUserConversations(User user);
    
    Conversation createGroup(
            String name,
            Set<User> participants,
            String groupImageUrl
        );

	void deleteConversationForUser(Long conversationId, User user);

	void removeParticipant(Long groupId, Long userId, User requester);

	void leaveGroup(Long groupId, User user);

	void deleteGroup(Long groupId, User requester);

}
