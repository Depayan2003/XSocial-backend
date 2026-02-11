package com.social.Model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "admin_actions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User admin;

    @ManyToOne
    private User targetUser;   // ✅ REQUIRED

    private String action;     // WARN / DISABLE

    private String reason;     // ✅ structured

    private LocalDateTime createdAt;
}
