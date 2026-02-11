package com.social.Repository;

import com.social.Model.Conversation;
import com.social.Model.Message;
import com.social.Model.Enums.MessageStatus;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByConversationOrderByCreatedAtAsc(
            Conversation conversation
    );

    List<Message> findByConversationAndStatus(
            Conversation conversation,
            MessageStatus status
    );

    // 🔴 DELIVERED
    @Modifying
    @Query("""
        UPDATE Message m
        SET m.status = 'DELIVERED'
        WHERE m.id = :messageId
    """)
    void markDelivered(@Param("messageId") Long messageId);

    // 🔴 SEEN (bulk, safe)
    @Modifying
    @Query("""
        UPDATE Message m
        SET m.status = 'SEEN'
        WHERE m.conversation.id = :conversationId
        AND m.sender.email <> :viewerEmail
        AND m.status <> 'SEEN'
    """)
    void markConversationSeen(
        @Param("conversationId") Long conversationId,
        @Param("viewerEmail") String viewerEmail
    );
}
