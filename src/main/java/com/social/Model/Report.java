package com.social.Model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User reporter;

    @ManyToOne
    private User reportedUser;

    private String reason;

    private boolean resolved = false;

    private LocalDateTime createdAt;
}
