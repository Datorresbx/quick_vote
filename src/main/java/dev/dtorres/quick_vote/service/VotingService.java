package dev.dtorres.quick_vote.service;

import dev.dtorres.quick_vote.model.Vote;
import dev.dtorres.quick_vote.model.VoteOption;
import dev.dtorres.quick_vote.model.VoteSession;
import dev.dtorres.quick_vote.repo.VoteRepository;
import dev.dtorres.quick_vote.repo.VoteSessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

@Service
public class VotingService {
    private final VoteRepository voteRepository;
    private final VoteSessionRepository sessionRepository;

    public VotingService(VoteRepository voteRepository, VoteSessionRepository sessionRepository) {
        this.voteRepository = voteRepository;
        this.sessionRepository = sessionRepository;
    }

    public Optional<VoteSession> getActiveSession() {
        return sessionRepository.findActive();
    }

    @Transactional
    public VoteSession startSession(long durationSeconds) {
        // close any previous active session
        sessionRepository.findActive().ifPresent(vs -> {
            vs.setActive(false);
            sessionRepository.save(vs);
        });
        Instant now = Instant.now();
        VoteSession vs = new VoteSession(now, now.plusSeconds(durationSeconds), true);
        return sessionRepository.save(vs);
    }

    @Transactional
    public void stopSession() {
        sessionRepository.findActive().ifPresent(vs -> {
            vs.setActive(false);
            sessionRepository.save(vs);
        });
    }

    @Transactional
    public boolean submitVote(VoteOption option) {
        Optional<VoteSession> active = sessionRepository.findActive();
        if (active.isEmpty()) return false;
        VoteSession vs = active.get();
        if (!vs.isOpen()) {
            // auto-deactivate if time passed
            if (Instant.now().isAfter(vs.getEndTime())) {
                vs.setActive(false);
                sessionRepository.save(vs);
            }
            return false;
        }
        voteRepository.save(new Vote(option, vs.getId()));
        return true;
    }

    @Transactional(readOnly = true)
    public Map<VoteOption, Long> getResultsForActiveOrLast() {
        Optional<VoteSession> active = sessionRepository.findActive();
        VoteSession vs = active.orElseGet(() -> sessionRepository.findAll().stream()
                .reduce((first, second) -> second).orElse(null));
        Map<VoteOption, Long> map = new EnumMap<>(VoteOption.class);
        for (VoteOption vo : VoteOption.values()) {
            map.put(vo, 0L);
        }
        if (vs == null) return map;
        for (Object[] row : voteRepository.countGroupedByOption(vs.getId())) {
            VoteOption option = (VoteOption) row[0];
            Long cnt = (Long) row[1];
            map.put(option, cnt);
        }
        return map;
    }
}