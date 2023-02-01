package pl.jsieczczynski.SpringBootRedditClone.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.jsieczczynski.SpringBootRedditClone.dto.model.CommentDto;
import pl.jsieczczynski.SpringBootRedditClone.dto.model.PostDto;
import pl.jsieczczynski.SpringBootRedditClone.exceptions.AppException;
import pl.jsieczczynski.SpringBootRedditClone.model.Comment;
import pl.jsieczczynski.SpringBootRedditClone.model.Post;
import pl.jsieczczynski.SpringBootRedditClone.model.User;
import pl.jsieczczynski.SpringBootRedditClone.model.Vote;
import pl.jsieczczynski.SpringBootRedditClone.repository.CommentRepository;
import pl.jsieczczynski.SpringBootRedditClone.repository.PostRepository;

import java.util.Optional;
import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService {
    private static final int PAGE_SIZE = 8;
    private final PostRepository postRepository;
    private final AuthService authService;
    private final CommentRepository commentRepository;

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

    public void deleteById(Long id) {
        postRepository.deleteById(id);
    }

    public PostDto update(PostDto postDto) {
        Post post = postRepository.findById(postDto.getId()).orElseThrow(() -> new AppException("Post with id " + postDto.getId() + " not found"));
        post.setTitle(postDto.getTitle());
        post.setBody(postDto.getBody());
        postRepository.save(post);
        return postDto;
    }

    public Page<PostDto> findPaginatedByUser(int pageNumber, String sortField, Sort.Direction sortDirection, String username) {
        Sort sort = sortDirection == Sort.Direction.ASC ? Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(pageNumber - 1, PAGE_SIZE, sort);
        Page<Post> posts = postRepository.findAllByAuthor_Username(username, pageable);
        Optional<User> currentUser = authService.getCurrentUser();
        return posts.map(post -> {
            int voteScore = post.getVotes().stream().mapToInt(Vote::getDirection).sum();
            int userVote = currentUser.map(user -> post.getVotes().stream()
                    .filter(vote -> vote.getUser().getId().equals(user.getId()))
                    .mapToInt(Vote::getDirection).findFirst().orElse(0)).orElse(0);
            return PostDto.builder()
                    .id(post.getId())
                    .title(post.getTitle())
                    .body(post.getBody())
                    .createdAt(post.getCreatedAt())
                    .username(post.getAuthor().getUsername())
                    .voteScore(voteScore)
                    .userVote(userVote)
                    .build();
        });
    }

    public void addComment(CommentDto comment) {
        Post post = postRepository.findById(comment.getPostId()).orElseThrow(() -> new AppException("Post with id " + comment.getPostId() + " not found"));
        User user = authService.getCurrentUser().orElseThrow(() -> new AppException("User not found"));
        Comment newComment = new Comment();
        newComment.setBody(comment.getBody());
        newComment.setPost(post);
        newComment.setAuthor(user);
        user.getComments().add(newComment);
        post.getComments().add(newComment);
        postRepository.save(post);
    }

    public Page<CommentDto> findPaginatedComments(int pageNumber, String sortField, Sort.Direction sortDirection, Long id) {
        Sort sort = sortDirection == Sort.Direction.ASC ? Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(pageNumber - 1, PAGE_SIZE, sort);
        Page<Comment> comments = commentRepository.findAllByPost_Id(id, pageable);
        return comments.map(comment -> {
            int voteScore = comment.getVotes().stream().mapToInt(Vote::getDirection).sum();
            int userVote = authService.getCurrentUser().map(user -> comment.getVotes().stream()
                    .filter(vote -> vote.getUser().getId().equals(user.getId()))
                    .mapToInt(Vote::getDirection).findFirst().orElse(0)).orElse(0);
            return CommentDto.builder()
                    .id(comment.getId())
                    .body(comment.getBody())
                    .postId(comment.getPost().getId())
                    .createdAt(comment.getCreatedAt())
                    .updatedAt(comment.getUpdatedAt())
                    .author(comment.getAuthor().getUsername())
                    .voteScore(voteScore)
                    .userVote(userVote)
                    .build();
        });
    }

    public CommentDto getCommentById(long id) {
        Comment comment = commentRepository.findById(id).orElseThrow(() -> new AppException("Comment with id " + id + " not found"));
        int voteScore = comment.getVotes().stream().mapToInt(Vote::getDirection).sum();
        int userVote = authService.getCurrentUser().map(user -> comment.getVotes().stream()
                .filter(vote -> vote.getUser().getId().equals(user.getId()))
                .mapToInt(Vote::getDirection).findFirst().orElse(0)).orElse(0);
        return CommentDto.builder()
                .id(comment.getId())
                .body(comment.getBody())
                .postId(comment.getPost().getId())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .author(comment.getAuthor().getUsername())
                .voteScore(voteScore)
                .userVote(userVote)
                .build();
    }

    public void deleteCommentById(Long commentId) {
        commentRepository.deleteById(commentId);
    }

    public void updateComment(CommentDto comment) {
        Comment commentToUpdate = commentRepository.findById(comment.getId()).orElseThrow(() -> new AppException("Comment with id " + comment.getId() + " not found"));
        commentToUpdate.setBody(comment.getBody());
        commentRepository.save(commentToUpdate);
    }
}
