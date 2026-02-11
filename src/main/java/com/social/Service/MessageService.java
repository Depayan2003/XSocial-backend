package com.social.Service;

import com.social.Model.Conversation;
import com.social.Model.Message;
import com.social.Model.User;
import com.social.Model.Enums.MessageType;

import java.util.List;

public interface MessageService {

    Message sendMessage(
        Conversation conversation,
        User sender,
        String content,
        String mediaUrl,
        MessageType messageType
    );

    List<Message> getConversationMessages(Conversation conversation);

    // 🔴 ADD THESE
    void markDelivered(Long messageId);

    void markConversationSeen(Long conversationId, String viewerEmail);
    
    void deleteMessage(Long messageId, User requester);

}
