package pl.jsieczczynski.SpringBootRedditClone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.jsieczczynski.SpringBootRedditClone.model.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByUserUsername(String username);

    boolean existsByUserUsername(String username);

    void deleteByUserUsername(String username);
}
