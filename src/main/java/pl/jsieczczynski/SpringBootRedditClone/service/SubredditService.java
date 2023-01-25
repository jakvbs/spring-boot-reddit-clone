package pl.jsieczczynski.SpringBootRedditClone.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.jsieczczynski.SpringBootRedditClone.dto.SubredditDto;
import pl.jsieczczynski.SpringBootRedditClone.exceptions.SpringRedditException;
import pl.jsieczczynski.SpringBootRedditClone.mapper.SubredditMapper;
import pl.jsieczczynski.SpringBootRedditClone.repository.SubredditRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubredditService {
    private final SubredditRepository subredditRepository;
    private final SubredditMapper subredditMapper;

    @Transactional(readOnly = true)
    public List<SubredditDto> getAll() {
        return subredditRepository.findAll().stream()
                .map(subredditMapper::mapSubredditToDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public SubredditDto findOne(String name) {
        return subredditRepository.findByName(name)
                .map(subredditMapper::mapSubredditToDto)
                .orElseThrow(() -> new SpringRedditException("No subreddit found with name: " + name));
    }

    public void save(SubredditDto subredditDto) {
        subredditRepository.save(subredditMapper.mapDtoToSubreddit(subredditDto));
    }

    @Transactional(readOnly = true)
    public SubredditDto getById(Long id) {
        return subredditRepository.findById(id)
                .map(subredditMapper::mapSubredditToDto)
                .orElseThrow(() -> new SpringRedditException("No subreddit found with id: " + id));
    }

    public void deleteById(Long id) {
        subredditRepository.deleteById(id);
    }
}
