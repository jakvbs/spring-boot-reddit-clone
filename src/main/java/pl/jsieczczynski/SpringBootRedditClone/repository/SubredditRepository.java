package pl.jsieczczynski.SpringBootRedditClone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.jsieczczynski.SpringBootRedditClone.model.Subreddit;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubredditRepository extends JpaRepository<Subreddit, Long> {
    List<Subreddit> findByNameContainingIgnoreCase(String name);

    Optional<Subreddit> findByName(String name);
}
