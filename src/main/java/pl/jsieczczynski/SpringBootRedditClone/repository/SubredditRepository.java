package pl.jsieczczynski.SpringBootRedditClone.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.jsieczczynski.SpringBootRedditClone.model.Post;
import pl.jsieczczynski.SpringBootRedditClone.model.Subreddit;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubredditRepository extends JpaRepository<Subreddit, Long> {
    @Query("SELECT s FROM Subreddit s WHERE LOWER(CONCAT(s.id, ' ', s.name, ' ', s.description)) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Subreddit> search(String search, Pageable pageable);

    Optional<Subreddit> findByName(String name);

    boolean existsByName(String name);
}
