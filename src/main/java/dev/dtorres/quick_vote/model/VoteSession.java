package dev.dtorres.quick_vote.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "vote_sessions")
public class VoteSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Instant startTime;

    @Column(nullable = false)
    private Instant endTime;

    @Column(nullable = false)
    private boolean active;

    public VoteSession() {}

    public VoteSession(Instant startTime, Instant endTime, boolean active) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.active = active;
    }

    public Long getId() { return id; }
    public Instant getStartTime() { return startTime; }
    public void setStartTime(Instant startTime) { this.startTime = startTime; }
    public Instant getEndTime() { return endTime; }
    public void setEndTime(Instant endTime) { this.endTime = endTime; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    @Transient
    public boolean isOpen() {
        Instant now = Instant.now();
        return active && now.isBefore(endTime) && now.isAfter(startTime);
    }
}