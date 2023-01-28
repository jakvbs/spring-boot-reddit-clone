package pl.jsieczczynski.SpringBootRedditClone.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.jsieczczynski.SpringBootRedditClone.validators.unique.FieldValueExists;
import pl.jsieczczynski.SpringBootRedditClone.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService implements FieldValueExists {
    private final UserRepository userRepository;

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
