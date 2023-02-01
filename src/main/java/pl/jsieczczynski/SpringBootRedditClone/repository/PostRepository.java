package pl.jsieczczynski.SpringBootRedditClone.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.jsieczczynski.SpringBootRedditClone.model.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findAllBySubredditName(String name, Pageable pageable);

    Page<Post> findAllByAuthor_Username(String username, Pageable pageable);
}
