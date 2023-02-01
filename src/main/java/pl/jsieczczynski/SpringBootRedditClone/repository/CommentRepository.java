package pl.jsieczczynski.SpringBootRedditClone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.jsieczczynski.SpringBootRedditClone.model.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
}
