package dev.dtorres.quick_vote.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "votes")
public class Vote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VoteOption option;

    @Column(nullable = false)
    private Long sessionId;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public Vote() {}

    public Vote(VoteOption option, Long sessionId) {
        this.option = option;
        this.sessionId = sessionId;
    }

    public Long getId() { return id; }
    public VoteOption getOption() { return option; }
    public void setOption(VoteOption option) { this.option = option; }
    public Long getSessionId() { return sessionId; }
    public void setSessionId(Long sessionId) { this.sessionId = sessionId; }
    public Instant getCreatedAt() { return createdAt; }
}