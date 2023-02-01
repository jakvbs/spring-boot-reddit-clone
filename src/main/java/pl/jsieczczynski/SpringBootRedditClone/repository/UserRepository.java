package pl.jsieczczynski.SpringBootRedditClone.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.jsieczczynski.SpringBootRedditClone.dto.model.UserDto;
import pl.jsieczczynski.SpringBootRedditClone.model.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    @Query("""
            SELECT new pl.jsieczczynski.SpringBootRedditClone.dto.model.UserDto(u.id, u.createdAt, u.updatedAt, u.username, u.email, u.imageUrl, u.about, u.enabled, u.locked,
                COUNT(DISTINCT cS), COUNT(DISTINCT jS), COUNT(DISTINCT p), COUNT(DISTINCT c))
            FROM User u LEFT JOIN u.createdSubreddits cS LEFT JOIN u.joinedSubreddits jS LEFT JOIN u.posts p LEFT JOIN u.comments c WHERE u.username = :username
            GROUP BY u.id
            """)
    Optional<UserDto> findByUsernameWithStats(String username);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u JOIN u.joinedSubreddits s WHERE s.name = :name")
    Page<User> findAllBySubredditName(String name, Pageable pageable);

    boolean existsByUsername(String username);

    @Query("SELECT u FROM User u WHERE LOWER(CONCAT(u.id, ' ', u.username, ' ', u.email)) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<User> search(String search, Pageable pageable);
}
