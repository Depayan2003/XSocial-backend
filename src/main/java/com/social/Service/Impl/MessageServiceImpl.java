package com.social.Service.Impl;

import com.social.DTO.MessageDeletedEvent;
import com.social.DTO.MessageDeliveredEvent;
import com.social.DTO.MessageSeenEvent;
import com.social.Model.Conversation;
import com.social.Model.Message;
import com.social.Model.User;
import com.social.Model.Enums.MessageStatus;
import com.social.Model.Enums.MessageType;
import com.social.Repository.MessageRepository;
import com.social.Service.MessageService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    @Transactional
    public Message sendMessage(
            Conversation conversation,
            User sender,
            String content,
            String mediaUrl,
            MessageType messageType
    ) {

        boolean allowed = conversation.getParticipants().stream()
                .anyMatch(cp -> cp.getUser().equals(sender) && !cp.isDeleted());

        if (!allowed) {
            throw new RuntimeException("Sender not in conversation");
        }

        Message msg = new Message();
        msg.setConversation(conversation);
        msg.setSender(sender);
        msg.setContent(content);
        msg.setMediaUrl(mediaUrl);
        msg.setMessageType(mediaUrl == null ? MessageType.TEXT : messageType);
        msg.setStatus(MessageStatus.SENT);
        msg.setCreatedAt(LocalDateTime.now());

        return messageRepository.save(msg);
    }

    @Override
    public List<Message> getConversationMessages(Conversation conversation) {
        return messageRepository.findByConversationOrderByCreatedAtAsc(conversation);
    }

    @Override
    @Transactional
    public void markDelivered(Long messageId) {

        Message msg = messageRepository.findById(messageId)
            .orElseThrow(() -> new RuntimeException("Message not found"));

        if (msg.getStatus() != MessageStatus.SENT) return;

        msg.setStatus(MessageStatus.DELIVERED);
        messageRepository.save(msg);

        Long conversationId = msg.getConversation().getId();

        // 🔥 notify sender ONLY
        messagingTemplate.convertAndSendToUser(
            msg.getSender().getEmail(),
            "/queue/delivered",
            new MessageDeliveredEvent(messageId, conversationId)
        );
    }


    @Override
    @Transactional
    public void markConversationSeen(Long conversationId, String viewerEmail) {

        messageRepository.markConversationSeen(conversationId, viewerEmail);

        // 🔥 notify all OTHER users in conversation
        messagingTemplate.convertAndSend(
            "/topic/conversations/" + conversationId + "/seen",
            new MessageSeenEvent(conversationId)
        );
    }


    @Override
    @Transactional
    public void deleteMessage(Long messageId, User requester) {

        Message msg = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));

        boolean isSender = msg.getSender().equals(requester);

        boolean isAdmin =
                requester.getRole() != null &&
                requester.getRole().toString().equals("ADMIN");

        if (!isSender && !isAdmin) {
            throw new RuntimeException("Not allowed to delete this message");
        }

        Long conversationId = msg.getConversation().getId();

        // 🔥 ADMIN → HARD DELETE
        if (isAdmin) {
            messageRepository.delete(msg);

            messagingTemplate.convertAndSend(
                "/topic/messages/deleted",
                new MessageDeletedEvent(messageId, conversationId, true)
            );
            return;
        }

        // 🔥 USER → SOFT DELETE
        msg.setContent("This message was deleted");
        msg.setMediaUrl(null);
        msg.setStatus(MessageStatus.DELETED);

        messageRepository.save(msg);

        // 🔔 REAL-TIME DELETE EVENT
        messagingTemplate.convertAndSend(
            "/topic/messages/deleted",
            new MessageDeletedEvent(messageId, conversationId, false)
        );
    }

}
