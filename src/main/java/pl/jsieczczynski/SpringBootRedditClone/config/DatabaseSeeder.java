package pl.jsieczczynski.SpringBootRedditClone.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.jsieczczynski.SpringBootRedditClone.model.Post;
import pl.jsieczczynski.SpringBootRedditClone.model.Role;
import pl.jsieczczynski.SpringBootRedditClone.model.Subreddit;
import pl.jsieczczynski.SpringBootRedditClone.model.User;
import pl.jsieczczynski.SpringBootRedditClone.repository.PostRepository;
import pl.jsieczczynski.SpringBootRedditClone.repository.SubredditRepository;
import pl.jsieczczynski.SpringBootRedditClone.repository.UserRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final SubredditRepository subredditRepository;
    private final PostRepository postRepository;

    @Override
    @Transactional
    public void run(String... strings) throws Exception {
        System.out.println("Application started");

        userRepository.deleteAll();
        subredditRepository.deleteAll();
        postRepository.deleteAll();

        User user = User.builder()
                .username("user")
                .email("user@example.com")
                .password(passwordEncoder.encode("password"))
                .role(Role.USER)
                .enabled(true)
                .build();
        User admin = User.builder()
                .username("admin")
                .email("admin@example.com")
                .password(passwordEncoder.encode("password"))
                .role(Role.ADMIN)
                .enabled(true)
                .build();
        userRepository.save(user);
        userRepository.save(admin);
        System.out.println("User and admin accounts created");
        System.out.println("=============================================================");
        System.out.println();

        Subreddit subreddit = Subreddit.builder()
                .name("subreddit")
                .description("subreddit description")
                .author(user)
                .users(List.of(user))
                .build();
        subredditRepository.save(subreddit);


        Post post = Post.builder()
                .title("post title")
                .author(user)
                .body("post body")
                .subreddit(subreddit)
                .build();
        postRepository.save(post);

//        System.out.println("=============================================================");
//        List<Post> posts = postRepository.findAllBySubreddit(subreddit);
//        System.out.println(posts);
//        Faker faker = new Faker();
//        List<Subreddit> subreddits = new ArrayList<>();
//        for (int i = 0; i < 100; i++) {
//            subreddits.add(Subreddit.builder()
//                    .name("subreddit-" + (i + 1))
//                    .description(faker.lorem().sentence(2, 5))
//                    .build());
//        }
//        subredditRepository.saveAll(subreddits);


    }
}
