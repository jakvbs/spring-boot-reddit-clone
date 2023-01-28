package pl.jsieczczynski.SpringBootRedditClone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.jsieczczynski.SpringBootRedditClone.model.Post;
import pl.jsieczczynski.SpringBootRedditClone.model.Subreddit;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllBySubreddit(Subreddit subreddit);

    int countDistinctBySubreddit(Subreddit subreddit);
}
