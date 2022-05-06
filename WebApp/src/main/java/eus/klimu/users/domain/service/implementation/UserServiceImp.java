package eus.klimu.users.domain.service.implementation;

import eus.klimu.users.domain.model.AppUser;
import eus.klimu.users.domain.repository.UserRepository;
import eus.klimu.users.domain.service.definition.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImp implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user = userRepository.findByUsername(username);

        if (user != null) {
            log.info("User {} found on the database", username);

            // Get the user roles as SimpleGrantedAuthorities for Spring Security.
            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
            user.getRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority(role.getName())));

            // Create a Spring Security user to check on with.
            return new User(
                    user.getUsername(),
                    user.getPassword(),
                    authorities
            );
        } else {
            log.error("User with username {} couldn't be found", username);
            throw new UsernameNotFoundException("User with username" + username + "couldn't be found");
        }
    }

    @Override
    public AppUser saveUser(AppUser user) {
        log.info("Saving user {} on the database", user.getUsername());

        // Encode the user password.
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    @Override
    public AppUser getUser(String username) {
        log.info("Looking for user with username={}", username);
        return userRepository.findByUsername(username);
    }
}
