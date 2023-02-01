package pl.jsieczczynski.SpringBootRedditClone.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.jsieczczynski.SpringBootRedditClone.dto.PostDto;
import pl.jsieczczynski.SpringBootRedditClone.dto.SubredditDto;
import pl.jsieczczynski.SpringBootRedditClone.dto.UserDto;
import pl.jsieczczynski.SpringBootRedditClone.exceptions.AppException;
import pl.jsieczczynski.SpringBootRedditClone.model.*;
import pl.jsieczczynski.SpringBootRedditClone.repository.PostRepository;
import pl.jsieczczynski.SpringBootRedditClone.repository.SubredditRepository;
import pl.jsieczczynski.SpringBootRedditClone.repository.UserRepository;
import pl.jsieczczynski.SpringBootRedditClone.validators.FieldValueExists;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class SubredditService implements FieldValueExists {
    private static final int PAGE_SIZE = 8;
    private final AuthService authService;
    private final UserRepository userRepository;
    private final SubredditRepository subredditRepository;
    private final PostRepository postRepository;

    private static Page<SubredditDto> mapPageToDtoPage(Page<Subreddit> subreddits) {
        return subreddits.map(s -> SubredditDto.builder()
                .id(s.getId())
                .name(s.getName())
                .description(s.getDescription())
                .createdAt(s.getCreatedAt())
                .build());
    }

    public static List<SubredditDto> getAll() {
        return List.of();
    }

    public SubredditDto getByName(String name) {
        return subredditRepository.findByNameWithStats(name)
                .orElseThrow(() -> new AppException("No subreddit found with name: " + name));
    }

    public void create(SubredditDto.Create subredditDto) {
        Subreddit subreddit = new Subreddit();
        User currentUser = authService.getCurrentUser().orElseThrow(() -> new AppException("No user found"));

        subreddit.setName(subredditDto.getName());
        subreddit.setDescription(subredditDto.getDescription());
        subreddit.setAuthor(currentUser);

        subreddit.getUsers().add(currentUser);
        currentUser.getJoinedSubreddits().add(subreddit);

        subredditRepository.save(subreddit);
    }

    public void update(SubredditDto.Update subredditDto) {
        User currentUser = authService.getCurrentUser().orElseThrow(() -> new AppException("No user found"));
        System.out.println("Current user: " + currentUser.getUsername() + " with role: " + currentUser.getRole());
        Subreddit subreddit = subredditRepository.findById(subredditDto.getId())
                .orElseThrow(() -> new AppException("No subreddit found with id: " + subredditDto.getId()));
        System.out.println("Subreddit: " + subreddit.getName() + " with author: " + subreddit.getAuthor().getUsername());
        if (!currentUser.getId().equals(subreddit.getAuthor().getId()) && !currentUser.getRole().equals(Role.ADMIN)) {
            throw new AppException("You are not allowed to update this subreddit");
        }

        subreddit.setDescription(subredditDto.getDescription());
        subreddit.setImageUrl(subredditDto.getImageUrl());
        subreddit.setBannerUrl(subredditDto.getBannerUrl());
        subredditRepository.save(subreddit);
    }

    public void deleteByName(String name) {
        User currentUser = authService.getCurrentUser().orElseThrow(() -> new AppException("No user found"));
        Subreddit subreddit = subredditRepository.findByName(name)
                .orElseThrow(() -> new AppException("No subreddit found with name: " + name));
        if (!currentUser.getId().equals(subreddit.getAuthor().getId()) && !currentUser.getRole().equals(Role.ADMIN)) {
            throw new AppException("You are not allowed to delete this subreddit");
        }
        subredditRepository.deleteByName(name);
    }

    public Page<SubredditDto> findPaginated(int page, String sortField, Sort.Direction sortDirection, String search) {
        Sort sort = sortDirection == Sort.Direction.ASC ? Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(page - 1, PAGE_SIZE, sort);
        String searchValue = search == null ? "" : search;
        Page<Subreddit> result = subredditRepository.search(searchValue, pageable);
        return mapPageToDtoPage(result);
    }

    public Page<SubredditDto> findPaginatedByAuthor(int page, String sortField, Sort.Direction sortDirection, String authorName) {
        Sort sort = sortDirection == Sort.Direction.ASC ? Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(page - 1, PAGE_SIZE, sort);
        Page<Subreddit> result = subredditRepository.findAllByAuthor_Username(authorName, pageable);
        return mapPageToDtoPage(result);
    }

    public Page<SubredditDto> findPaginatedByUser(Integer page, String sortField, Sort.Direction sortDirection, String username) {
        Sort sort = sortDirection == Sort.Direction.ASC ? Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(page - 1, PAGE_SIZE, sort);
        Page<Subreddit> result = subredditRepository.findAllByUsers_Username(username, pageable);
        return mapPageToDtoPage(result);
    }

    public Page<UserDto> findPaginatedUsers(int pageNumber, String sortField, Sort.Direction sortDirection, String name) {
        Sort sort = sortDirection == Sort.Direction.ASC ? Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(pageNumber - 1, PAGE_SIZE, sort);
        Page<User> result = userRepository.findAllBySubredditName(name, pageable);
        return UserService.mapPageToDtoPage(result);
    }

    public Page<PostDto> findPaginatedPosts(int pageNumber, String sortField, Sort.Direction sortDirection, String name) {
        Sort sort = sortDirection == Sort.Direction.ASC ? Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(pageNumber - 1, PAGE_SIZE, sort);
        Page<Post> posts = postRepository.findAllBySubredditName(name, pageable);
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

    public void addUser(String name, UserDto.Add user) {
        Subreddit subreddit = subredditRepository.findByName(name)
                .orElseThrow(() -> new AppException("No subreddit found with name: " + name));
        User userToAdd = userRepository.findByUsername(user.getUsername())
                .orElseThrow(() -> new AppException("No user found with username: " + user.getUsername()));
        subreddit.getUsers().add(userToAdd);
        userToAdd.getJoinedSubreddits().add(subreddit);
    }

    public PostDto addPost(String subredditName, PostDto.Create post) {
        Subreddit subreddit = subredditRepository.findByName(subredditName)
                .orElseThrow(() -> new AppException("No subreddit found with name: " + subredditName));
        User author = authService.getCurrentUser().orElseThrow(() -> new AppException("No user found"));
        Post newPost = new Post();
        newPost.setTitle(post.getTitle());
        newPost.setBody(post.getBody());
        newPost.setAuthor(author);
        newPost.setSubreddit(subreddit);
        author.getPosts().add(newPost);
        subreddit.getPosts().add(newPost);
        postRepository.save(newPost);
        return PostDto.builder()
                .id(newPost.getId())
                .title(newPost.getTitle())
                .body(newPost.getBody())
                .createdAt(newPost.getCreatedAt())
                .username(newPost.getAuthor().getUsername())
                .build();
    }

    public void removeUser(String name, String username) {
        Subreddit subreddit = subredditRepository.findByName(name)
                .orElseThrow(() -> new AppException("No subreddit found with name: " + name));
        User userToRemove = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException("No user found with username: " + username));
        if (userToRemove.getRole().equals(Role.ADMIN)) {
            throw new AppException("You cannot remove admin from subreddit");
        }
        subreddit.getUsers().remove(userToRemove);
        userToRemove.getJoinedSubreddits().remove(subreddit);
    }

    @Override
    public boolean fieldValueExists(Object value, String fieldName) throws UnsupportedOperationException {
        if (fieldName.equals("name")) {
            return subredditRepository.existsByName(value.toString());
        }
        throw new UnsupportedOperationException("Field name " + fieldName + " not supported");
    }
}