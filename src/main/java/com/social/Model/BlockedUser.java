package com.social.Model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "blocked_users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class BlockedUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User blocker;

    @ManyToOne
    private User blocked;

    private LocalDateTime createdAt;
}

