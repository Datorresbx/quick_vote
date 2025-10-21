package dev.dtorres.quick_vote.repo;

import dev.dtorres.quick_vote.model.Vote;
import dev.dtorres.quick_vote.model.VoteOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    long countBySessionIdAndOption(Long sessionId, VoteOption option);

    @Query("SELECT v.option as option, COUNT(v) as cnt FROM Vote v WHERE v.sessionId = :sessionId GROUP BY v.option")
    List<Object[]> countGroupedByOption(@Param("sessionId") Long sessionId);
}