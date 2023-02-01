package pl.jsieczczynski.SpringBootRedditClone.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.jsieczczynski.SpringBootRedditClone.dto.PostDto;
import pl.jsieczczynski.SpringBootRedditClone.exceptions.AppException;
import pl.jsieczczynski.SpringBootRedditClone.model.Post;
import pl.jsieczczynski.SpringBootRedditClone.model.Vote;
import pl.jsieczczynski.SpringBootRedditClone.repository.PostRepository;

import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    public PostDto getById(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new AppException("Post with id " + id + " not found"));

        Set<Vote> votes = post.getVotes();
        return PostDto.builder()
                .id(post.getId())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .numberOfComments(post.getComments().size())
                .voteScore(votes.stream().reduce(0, (a, b) -> a + b.getDirection(), Integer::sum))
                .userVote(votes.stream().filter(v -> v.getUser().getId().equals(post.getAuthor().getId())).findFirst().map(Vote::getDirection).orElse(0))
                .title(post.getTitle())
                .body(post.getBody())
                .username(post.getAuthor().getUsername())
                .subredditName(post.getSubreddit().getName())
                .build();
    }
}
