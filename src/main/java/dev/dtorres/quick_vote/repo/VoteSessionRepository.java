package dev.dtorres.quick_vote.repo;

import dev.dtorres.quick_vote.model.VoteSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface VoteSessionRepository extends JpaRepository<VoteSession, Long> {
    @Query("SELECT vs FROM VoteSession vs WHERE vs.active = true ORDER BY vs.id DESC LIMIT 1")
    Optional<VoteSession> findActive();
}