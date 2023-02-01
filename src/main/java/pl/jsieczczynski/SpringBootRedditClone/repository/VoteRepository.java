package pl.jsieczczynski.SpringBootRedditClone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.jsieczczynski.SpringBootRedditClone.model.Vote;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
}
