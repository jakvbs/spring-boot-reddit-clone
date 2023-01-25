package pl.jsieczczynski.SpringBootRedditClone.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.jsieczczynski.SpringBootRedditClone.dto.SubredditDto;
import pl.jsieczczynski.SpringBootRedditClone.model.User;
import pl.jsieczczynski.SpringBootRedditClone.service.SubredditService;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/subreddits")
public class SubredditController {
    private final SubredditService subredditService;

    @GetMapping
    public ResponseEntity<List<SubredditDto>> getAllSubreddits(@AuthenticationPrincipal User user) {
        System.out.println("====================================");
        System.out.println(user.getId());
        System.out.println("====================================");
        return ResponseEntity.ok(subredditService.getAll());
    }

    @GetMapping("/{name}")
    public ResponseEntity<SubredditDto> findOne(@PathVariable String name) {
        return ResponseEntity.ok(subredditService.findOne(name));
    }
}
