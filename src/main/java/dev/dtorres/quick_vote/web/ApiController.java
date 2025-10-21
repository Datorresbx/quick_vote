package dev.dtorres.quick_vote.web;

import dev.dtorres.quick_vote.model.VoteOption;
import dev.dtorres.quick_vote.model.VoteSession;
import dev.dtorres.quick_vote.service.VotingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ApiController {
    private final VotingService votingService;

    public ApiController(VotingService votingService) {
        this.votingService = votingService;
    }

    @PostMapping("/vote/{option}")
    public ResponseEntity<?> vote(@PathVariable("option") String optionStr) {
        VoteOption option;
        try {
            option = VoteOption.valueOf(optionStr.toUpperCase());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", "Opción inválida"));
        }
        boolean ok = votingService.submitVote(option);
        if (!ok) return ResponseEntity.status(409).body(Map.of("status", "closed"));
        return ResponseEntity.ok(Map.of("status", "ok"));
    }

    @GetMapping("/results")
    public Map<VoteOption, Long> results() {
        return votingService.getResultsForActiveOrLast();
    }

    @GetMapping("/session-status")
    public Map<String, Object> sessionStatus() {
        Optional<VoteSession> active = votingService.getActiveSession();
        boolean open = active.filter(VoteSession::isOpen).isPresent();
        long remaining = 0;
        if (active.isPresent()) {
            Instant now = Instant.now();
            if (now.isBefore(active.get().getEndTime())) {
                remaining = Duration.between(now, active.get().getEndTime()).toSeconds();
            }
        }
        return Map.of(
                "open", open,
                "remainingSeconds", remaining
        );
    }
}
