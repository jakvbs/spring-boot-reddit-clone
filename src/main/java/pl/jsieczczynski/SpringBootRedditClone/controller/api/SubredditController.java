package pl.jsieczczynski.SpringBootRedditClone.controller.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.jsieczczynski.SpringBootRedditClone.dto.model.SubredditDto;
import pl.jsieczczynski.SpringBootRedditClone.service.SubredditService;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/subreddits")
public class SubredditController {
    private final SubredditService subredditService;

    @GetMapping
    public ResponseEntity<List<SubredditDto>> getAllSubreddits() {
        return ResponseEntity.ok(SubredditService.getAll());
    }

    @GetMapping("/{name}")
    public ResponseEntity<SubredditDto> findOne(@PathVariable String name) {
        return ResponseEntity.ok(subredditService.getByName(name));
    }
}
