package pl.jsieczczynski.SpringBootRedditClone.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.jsieczczynski.SpringBootRedditClone.dto.SubredditDto;
import pl.jsieczczynski.SpringBootRedditClone.model.Subreddit;

import java.util.Optional;

@Repository
public interface SubredditRepository extends JpaRepository<Subreddit, Long> {
    @Query("SELECT s FROM Subreddit s WHERE LOWER(CONCAT(s.id, ' ', s.name, ' ', s.description)) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Subreddit> search(String search, Pageable pageable);

    Page<Subreddit> findAllByAuthor_Username(String username, Pageable pageable);

    Page<Subreddit> findAllByUsers_Username(String username, Pageable pageable);

    @Query("""
            SELECT new pl.jsieczczynski.SpringBootRedditClone.dto.SubredditDto(s.id, s.createdAt, s.updatedAt, s.name, s.description, s.imageUrl, s.bannerUrl, COUNT(DISTINCT p), COUNT(DISTINCT u), s.author.username)
            FROM Subreddit s LEFT JOIN s.posts p LEFT JOIN s.users u WHERE s.name = :name
            GROUP BY s.id, s.author.username
            """)
    Optional<SubredditDto> findByNameWithStats(String name);

    Optional<Subreddit> findByName(String name);

    boolean existsByName(String name);

    void deleteByName(String name);
}
