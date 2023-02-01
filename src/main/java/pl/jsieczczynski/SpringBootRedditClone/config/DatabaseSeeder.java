package pl.jsieczczynski.SpringBootRedditClone.config;

import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.jsieczczynski.SpringBootRedditClone.model.*;
import pl.jsieczczynski.SpringBootRedditClone.repository.*;
import pl.jsieczczynski.SpringBootRedditClone.utils.Helpers;

import java.util.*;

@Component
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final SubredditRepository subredditRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final VoteRepository voteRepository;

    @Override
    @Transactional
    public void run(String... strings) {
        System.out.println("Application started");

        Faker faker = new Faker();

        Optional<User> adminOption = userRepository.findByUsername("admin");
        if (adminOption.isPresent()) {
            System.out.println("Database already seeded");
            return;
        }

        userRepository.deleteAll();
        subredditRepository.deleteAll();
        postRepository.deleteAll();

        User admin = new User();
        admin.setUsername("admin");
        admin.setEmail("admin@example.com");
        admin.setPassword(passwordEncoder.encode("password"));
        admin.setRole(Role.ADMIN);
        admin.setEnabled(true);
        userRepository.save(admin);

        System.out.println("Admin created");

        List<User> users = new ArrayList<>();
        String passwordEncoded = passwordEncoder.encode("password");
        for (int i = 0; i < 1000; i++) {
            User user = new User();
            user.setUsername("user-" + (i + 1));
            user.setEmail("user-" + (i + 1) + "@example.com");
            user.setAbout(faker.lorem().paragraph());
            user.setPassword(passwordEncoded);
            user.setRole(Role.USER);
            user.setEnabled(true);
            users.add(user);
        }
        userRepository.saveAll(users);

        System.out.println("Users created");

        List<Subreddit> subreddits = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            Subreddit subreddit = new Subreddit();
            subreddit.setName("subreddit-" + (i + 1));
            subreddit.setDescription(faker.lorem().sentence(2, 5));
            User author = Helpers.randomElementFrom(users);
            subreddit.setAuthor(author);
            subreddit.setUsers(Set.of(author));
            subreddits.add(subreddit);
        }
        subredditRepository.saveAll(subreddits);

        List<Post> posts = new ArrayList<>();
        subreddits.forEach(s -> {
            s.getAuthor().getCreatedSubreddits().add(s);
            Set<User> subredditUsers = new HashSet<>();
            for (int i = 0; i < Helpers.randomFromRange(100, 600); i++) {
                User user = Helpers.randomElementFrom(users);
                subredditUsers.add(user);
                user.getJoinedSubreddits().add(s);
            }
            subredditUsers.add(s.getAuthor());
            s.setUsers(subredditUsers);

            for (int i = 0; i < Helpers.randomFromRange(10, 50); i++) {
                Post post = new Post();
                post.setTitle(faker.lorem().sentence(2, 5));
                post.setBody(faker.lorem().paragraph());
                post.setAuthor(Helpers.randomElementFrom(s.getUsers()));
                post.setSubreddit(s);
                posts.add(post);
            }
        });

        List<Vote> postVotes = new ArrayList<>();
        posts.forEach(p -> {
            for (int i = 0; i < Helpers.randomFromRange(1, 100); i++) {
                User user = Helpers.randomElementFrom(p.getSubreddit().getUsers());
                Vote vote = new Vote();
                vote.setPost(p);
                vote.setUser(user);
                vote.setDirection(1);
                p.getVotes().add(vote);
            }
            for (int i = 0; i < Helpers.randomFromRange(1, 100); i++) {
                User user = Helpers.randomElementFrom(p.getSubreddit().getUsers());
                Vote vote = new Vote();
                vote.setPost(p);
                vote.setUser(user);
                vote.setDirection(-1);
                p.getVotes().add(vote);
            }
        });

        List<Comment> comments = new ArrayList<>();
        posts.forEach(p -> {
            for (int i = 0; i < Helpers.randomFromRange(1, 10); i++) {
                Comment comment = new Comment();
                comment.setBody(faker.lorem().paragraph());
                comment.setAuthor(Helpers.randomElementFrom(p.getSubreddit().getUsers()));
                comment.setPost(p);
                comments.add(comment);
            }
        });

        List<Vote> commentVotes = new ArrayList<>();
        comments.forEach(c -> {
            for (int i = 0; i < Helpers.randomFromRange(1, 100); i++) {
                User user = Helpers.randomElementFrom(c.getPost().getSubreddit().getUsers());
                Vote vote = new Vote();
                vote.setComment(c);
                vote.setUser(user);
                vote.setDirection(1);
                c.getVotes().add(vote);
            }
            for (int i = 0; i < Helpers.randomFromRange(1, 100); i++) {
                User user = Helpers.randomElementFrom(c.getPost().getSubreddit().getUsers());
                Vote vote = new Vote();
                vote.setComment(c);
                vote.setUser(user);
                vote.setDirection(-1);
                c.getVotes().add(vote);
            }
        });
        userRepository.saveAll(users);
        subredditRepository.saveAll(subreddits);
        postRepository.saveAll(posts);
        commentRepository.saveAll(comments);
        voteRepository.saveAll(postVotes);
        voteRepository.saveAll(commentVotes);

        System.out.println("Subreddits created");
    }
}
