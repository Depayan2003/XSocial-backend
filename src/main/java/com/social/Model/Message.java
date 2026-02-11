package com.social.Model;

import com.social.Model.Enums.MessageStatus;
import com.social.Model.Enums.MessageType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "messages",
    indexes = {
        @Index(name = "idx_conversation_created_at", columnList = "conversation_id, createdAt")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Conversation this message belongs to
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;

    /**
     * Sender of the message
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    /**
     * Text content (for TEXT messages)
     */
    @Column(columnDefinition = "TEXT")
    private String content;

    /**
     * Media URL (image/video/audio/file)
     */
    private String mediaUrl;

    /**
     * Message type
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageType messageType;

    /**
     * Delivery/read status
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageStatus status;

    /**
     * Soft delete flag
     */
    private boolean deleted = false;

    /**
     * Message creation time
     */
    @Column(nullable = false)
    private LocalDateTime createdAt;
}
