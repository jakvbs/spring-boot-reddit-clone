package pl.jsieczczynski.SpringBootRedditClone.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.jsieczczynski.SpringBootRedditClone.dto.CreateSubredditDto;
import pl.jsieczczynski.SpringBootRedditClone.dto.SubredditDto;
import pl.jsieczczynski.SpringBootRedditClone.dto.UpdateSubredditDto;
import pl.jsieczczynski.SpringBootRedditClone.exceptions.SpringRedditException;
import pl.jsieczczynski.SpringBootRedditClone.model.Subreddit;
import pl.jsieczczynski.SpringBootRedditClone.model.User;
import pl.jsieczczynski.SpringBootRedditClone.repository.PostRepository;
import pl.jsieczczynski.SpringBootRedditClone.repository.SubredditRepository;
import pl.jsieczczynski.SpringBootRedditClone.repository.UserRepository;
import pl.jsieczczynski.SpringBootRedditClone.validators.unique.FieldValueExists;

import java.util.List;

@Slf4j
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

    private SubredditDto mapToDto(Subreddit subreddit) {
        return SubredditDto.builder()
                .id(subreddit.getId())
                .name(subreddit.getName())
                .description(subreddit.getDescription())
                .createdAt(subreddit.getCreatedAt())
                .numberOfPosts(postRepository.countDistinctBySubreddit(subreddit))
                .numberOfUsers(userRepository.countDistinctBySubreddits_Id(subreddit.getId()))
                .author(subreddit.getAuthor().getUsername())
                .build();
    }

    public List<SubredditDto> getAll() {
        return subredditRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    public SubredditDto getById(Long id) {
        return subredditRepository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new SpringRedditException("No subreddit found with id: " + id));
    }

    public SubredditDto getByName(String name) {
        return subredditRepository.findByName(name)
                .map(this::mapToDto)
                .orElseThrow(() -> new SpringRedditException("No subreddit found with name: " + name));
    }

    public void create(CreateSubredditDto subredditDto) {
        Subreddit subreddit = new Subreddit();
        User currentUser = authService.getCurrentUserOrThrow();

        subreddit.setName(subredditDto.getName());
        subreddit.setDescription(subredditDto.getDescription());
        subreddit.setAuthor(currentUser);
        subreddit.getUsers().add(currentUser);
        currentUser.getSubreddits().add(subreddit);

        subredditRepository.save(subreddit);
    }

    public void update(UpdateSubredditDto subredditDto) {
        Subreddit subreddit = subredditRepository.findById(subredditDto.getId())
                .orElseThrow(() -> new SpringRedditException("No subreddit found with id: " + subredditDto.getId()));
        subreddit.setDescription(subredditDto.getDescription());
        subredditRepository.save(subreddit);
    }

    public void deleteById(Long id) {
        subredditRepository.deleteById(id);
    }

    public Page<SubredditDto> findPaginated(int page, String sortField, Sort.Direction sortDirection, String search) {
        Sort sort = sortDirection == Sort.Direction.ASC ? Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(page - 1, PAGE_SIZE, sort);
        Page<Subreddit> result;
        if (search != null) {
            result = subredditRepository.search(search, pageable);
        } else {
            result = subredditRepository.findAll(pageable);
        }

        return mapPageToDtoPage(result);
    }


    @Override
    public boolean fieldValueExists(Object value, String fieldName) throws UnsupportedOperationException {
        if (fieldName.equals("name")) {
            return subredditRepository.existsByName(value.toString());
        }
        throw new UnsupportedOperationException("Field name " + fieldName + " not supported");
    }
}