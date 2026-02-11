package com.social.Repository;

import com.social.Model.Conversation;
import com.social.Model.ConversationParticipant;
import com.social.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConversationParticipantRepository
        extends JpaRepository<ConversationParticipant, Long> {

    List<ConversationParticipant> findByUserAndDeletedFalse(User user);

    Optional<ConversationParticipant>
        findByConversationAndUser(Conversation conversation, User user);

    List<ConversationParticipant>
        findByConversationAndDeletedFalse(Conversation conversation);
}
