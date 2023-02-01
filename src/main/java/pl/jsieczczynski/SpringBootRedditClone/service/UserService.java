package pl.jsieczczynski.SpringBootRedditClone.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.jsieczczynski.SpringBootRedditClone.dto.model.UserDto;
import pl.jsieczczynski.SpringBootRedditClone.exceptions.AppException;
import pl.jsieczczynski.SpringBootRedditClone.model.Role;
import pl.jsieczczynski.SpringBootRedditClone.model.Subreddit;
import pl.jsieczczynski.SpringBootRedditClone.model.User;
import pl.jsieczczynski.SpringBootRedditClone.repository.UserRepository;
import pl.jsieczczynski.SpringBootRedditClone.validators.FieldValueExists;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService implements FieldValueExists {
    private static final int PAGE_SIZE = 8;
    private final UserRepository userRepository;
    private final AuthService authService;

    static Page<UserDto> mapPageToDtoPage(Page<User> subreddits) {
        return subreddits.map(s -> UserDto.builder()
                .id(s.getId())
                .username(s.getUsername())
                .email(s.getEmail())
                .createdAt(s.getCreatedAt())
                .build());
    }

    public UserDto getByName(String name) {
        return userRepository.findByUsernameWithStats(name)
                .orElseThrow(() -> new AppException("User not found with name - " + name));
    }

    public Page<UserDto> findPaginated(int page, String sortField, Sort.Direction sortDirection, String search) {
        Sort sort = sortDirection == Sort.Direction.ASC ? Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(page - 1, PAGE_SIZE, sort);
        String searchValue = search == null ? "" : search;
        Page<User> result = userRepository.search(searchValue, pageable);
        return mapPageToDtoPage(result);
    }

    public Page<UserDto> findPaginatedBySubreddit(Subreddit subreddit, int page, String sortField, Sort.Direction sortDirection, String search) {
        Sort sort = sortDirection == Sort.Direction.ASC ? Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(page - 1, PAGE_SIZE, sort);
        String searchValue = search == null ? "" : search;
        Page<User> result = userRepository.search(searchValue, pageable);
        return mapPageToDtoPage(result);
    }

    public UserDto update(UserDto.Update user) {
        User userToUpdate = userRepository.findByUsername(user.getUsername())
                .orElseThrow(() -> new AppException("User not found with name - " + user.getUsername()));
        userToUpdate.setImageUrl(user.getImageUrl());
        userToUpdate.setAbout(user.getAbout());
        return UserDto.builder()
                .id(userToUpdate.getId())
                .username(userToUpdate.getUsername())
                .email(userToUpdate.getEmail())
                .createdAt(userToUpdate.getCreatedAt())
                .build();
    }

    public void toggleBan(String username) {
        User currentUser = authService.getCurrentUser().orElseThrow(() -> new AppException("User not found"));
        if (!currentUser.getRole().equals(Role.ADMIN)) {
            throw new AppException("You can't ban admin");
        }
        User userToBan = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException("User not found with name - " + username));
        userToBan.setLocked(!userToBan.isLocked());
    }

    @Override
    public boolean fieldValueExists(Object value, String fieldName) throws UnsupportedOperationException {
        if (fieldName.equals("email")) {
            return userRepository.existsByEmail(value.toString());
        }
        if (fieldName.equals("username")) {
            return userRepository.existsByUsername(value.toString());
        }
        throw new UnsupportedOperationException("Field name not supported");
    }


}
