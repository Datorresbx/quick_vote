package dev.dtorres.quick_vote.web;

import dev.dtorres.quick_vote.service.VotingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminApiController {
    private final VotingService votingService;

    public AdminApiController(VotingService votingService) {
        this.votingService = votingService;
    }

    @PostMapping("/start")
    public ResponseEntity<?> start(@RequestParam(name = "seconds") long seconds) {
        if (seconds <= 0) return ResponseEntity.badRequest().body(Map.of("error", "Duración inválida"));
        var session = votingService.startSession(seconds);
        return ResponseEntity.ok(Map.of("sessionId", session.getId(), "seconds", seconds));
    }

    @PostMapping("/stop")
    public Map<String, String> stop() {
        votingService.stopSession();
        return Map.of("status", "stopped");
    }
}
