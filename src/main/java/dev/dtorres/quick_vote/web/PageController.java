package dev.dtorres.quick_vote.web;

import dev.dtorres.quick_vote.service.VotingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {
    private final VotingService votingService;

    public PageController(VotingService votingService) {
        this.votingService = votingService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("title", "Votación");
        return "index";
    }

    @GetMapping("/admin")
    public String admin(Model model) {
        return "admin";
    }
}
