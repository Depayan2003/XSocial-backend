package com.social.Model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "conversations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "is_group", nullable = false)
    private boolean group;

    private String name;
    private String groupImageUrl;

    @OneToMany(
        mappedBy = "conversation",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private Set<ConversationParticipant> participants = new HashSet<>();

    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;
}
