package eus.klimu.users.domain.service.implementation;

import eus.klimu.users.domain.model.AppUser;
import eus.klimu.users.domain.repository.UserRepository;
import eus.klimu.users.domain.service.definition.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImp implements UserService {

    private final UserRepository userRepository;

    @Override
    public AppUser saveUser(AppUser user) {
        log.info("Saving user {} on the database.", user.getUsername());
        return userRepository.save(user);
    }

    @Override
    public AppUser getUser(String username) {
        log.info("Looking for user with username={}", username);
        return userRepository.findByUsername(username);
    }
}
