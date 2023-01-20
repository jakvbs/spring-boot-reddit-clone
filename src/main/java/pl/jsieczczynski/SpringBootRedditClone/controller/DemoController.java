package pl.jsieczczynski.SpringBootRedditClone.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/demo-controller")
public class DemoController {

    @GetMapping
    public ResponseEntity<String> sayHello() {
        return ResponseEntity.ok("Hello from secured endpoint");
    }

    @GetMapping("/users")
    public List<String> getUsers() {
        return List.of("user1", "user2", "user3");
    }
}
