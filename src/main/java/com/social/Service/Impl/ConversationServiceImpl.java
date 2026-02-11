package com.social.Service.Impl;

import com.social.Event.ConversationChangedEvent;
import com.social.Model.Conversation;
import com.social.Model.ConversationParticipant;
import com.social.Model.User;
import com.social.Repository.ConversationParticipantRepository;
import com.social.Repository.ConversationRepository;
import com.social.Repository.UserRepository;
import com.social.Service.ConversationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConversationServiceImpl implements ConversationService {

    private final ConversationRepository conversationRepository;
    private final ConversationParticipantRepository participantRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    /* ======================================================
                       1–1 CONVERSATION
       ====================================================== */

    @Override
    @Transactional
    public Conversation getOrCreateConversation(User u1, User u2) {

        return conversationRepository
            .findDirectConversationBetweenUsers(u1, u2)
            .orElseGet(() -> {

                Conversation conversation = new Conversation();
                conversation.setGroup(false);
                conversation.setCreatedAt(LocalDateTime.now());

                ConversationParticipant p1 = new ConversationParticipant();
                p1.setConversation(conversation);
                p1.setUser(u1);
                p1.setJoinedAt(LocalDateTime.now());

                ConversationParticipant p2 = new ConversationParticipant();
                p2.setConversation(conversation);
                p2.setUser(u2);
                p2.setJoinedAt(LocalDateTime.now());

                conversation.getParticipants().add(p1);
                conversation.getParticipants().add(p2);

                Conversation saved = conversationRepository.save(conversation);

                // 🔥 REAL-TIME (AFTER COMMIT)
                publishEvent(Set.of(u1, u2), saved.getId());

                return saved;
            });
    }

    /* ======================================================
                        USER CONVERSATIONS
       ====================================================== */

    @Override
    public List<Conversation> getUserConversations(User user) {
        return participantRepository
                .findByUserAndDeletedFalse(user)
                .stream()
                .map(ConversationParticipant::getConversation)
                .distinct()
                .toList();
    }

    /* ======================================================
                          GROUP CREATE
       ====================================================== */

    @Override
    @Transactional
    public Conversation createGroup(
            String name,
            Set<User> users,
            String groupImageUrl
    ) {
        if (users.size() < 3) {
            throw new RuntimeException("Group must have at least 3 users");
        }

        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();

        User creator = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!users.contains(creator)) {
            throw new RuntimeException("Creator must be a participant");
        }

        Conversation group = new Conversation();
        group.setGroup(true);
        group.setName(name);
        group.setGroupImageUrl(groupImageUrl);
        group.setCreatedBy(creator);
        group.setCreatedAt(LocalDateTime.now());

        for (User user : users) {
            ConversationParticipant cp = new ConversationParticipant();
            cp.setConversation(group);
            cp.setUser(user);
            cp.setAdmin(user.equals(creator));
            cp.setJoinedAt(LocalDateTime.now());

            group.getParticipants().add(cp);
        }

        Conversation saved = conversationRepository.save(group);

        // 🔥 REAL-TIME (AFTER COMMIT)
        publishEvent(users, saved.getId());

        return saved;
    }

    /* ======================================================
                     DELETE CONVERSATION (SOFT)
       ====================================================== */

    @Override
    @Transactional
    public void deleteConversationForUser(Long conversationId, User user) {

        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));

        ConversationParticipant cp = participantRepository
                .findByConversationAndUser(conversation, user)
                .orElseThrow(() -> new RuntimeException("Access denied"));

        cp.setDeleted(true);
        cp.setLeftAt(LocalDateTime.now());

        participantRepository.save(cp);

        publishEvent(Set.of(user), conversationId);
    }

    /* ======================================================
                     REMOVE PARTICIPANT (ADMIN)
       ====================================================== */

    @Override
    @Transactional
    public void removeParticipant(Long groupId, Long userId, User requester) {

        Conversation group = conversationRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        ConversationParticipant adminCp =
                participantRepository.findByConversationAndUser(group, requester)
                        .orElseThrow(() -> new RuntimeException("Access denied"));

        if (!adminCp.isAdmin()) {
            throw new RuntimeException("Only admin can remove users");
        }

        User removedUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ConversationParticipant removedCp =
                participantRepository.findByConversationAndUser(group, removedUser)
                        .orElseThrow(() -> new RuntimeException("User not in group"));

        removedCp.setDeleted(true);
        removedCp.setLeftAt(LocalDateTime.now());

        participantRepository.save(removedCp);

        publishEvent(getActiveUsers(group), groupId);
    }

    /* ======================================================
                           LEAVE GROUP
       ====================================================== */

    @Override
    @Transactional
    public void leaveGroup(Long groupId, User user) {

        Conversation group = conversationRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        ConversationParticipant cp =
                participantRepository.findByConversationAndUser(group, user)
                        .orElseThrow(() -> new RuntimeException("Access denied"));

        cp.setDeleted(true);
        cp.setLeftAt(LocalDateTime.now());

        participantRepository.save(cp);

        publishEvent(getActiveUsers(group), groupId);
    }

    /* ======================================================
                          DELETE GROUP
       ====================================================== */

    @Override
    @Transactional
    public void deleteGroup(Long groupId, User requester) {

        Conversation group = conversationRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        ConversationParticipant adminCp =
                participantRepository.findByConversationAndUser(group, requester)
                        .orElseThrow(() -> new RuntimeException("Access denied"));

        if (!adminCp.isAdmin()) {
            throw new RuntimeException("Only admin can delete group");
        }

        Set<User> affectedUsers = getActiveUsers(group);

        conversationRepository.delete(group);

        publishEvent(affectedUsers, groupId);
    }

    /* ======================================================
                           HELPERS
       ====================================================== */

    private Set<User> getActiveUsers(Conversation conversation) {
        return participantRepository
                .findByConversationAndDeletedFalse(conversation)
                .stream()
                .map(ConversationParticipant::getUser)
                .collect(Collectors.toSet());
    }

    private void publishEvent(Set<User> users, Long conversationId) {
        eventPublisher.publishEvent(
                new ConversationChangedEvent(conversationId, users)
        );
    }
}
