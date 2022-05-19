package eus.klimu.users.domain.service.implementation;

import eus.klimu.security.TokenManagement;
import eus.klimu.users.domain.model.AppUser;
import eus.klimu.users.domain.repository.UserRepository;
import eus.klimu.users.domain.service.definition.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImp implements UserService, UserDetailsService {

    private static final String SERVER_LOGIN_URL = "http://localhost:8080/RestAPI/login";
    private static final String SERVER_USER_URL = "http://localhost:8080/RestAPI/user/";

    private final RestTemplate restTemplate = new RestTemplate();
    private final HttpSession session;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String password = (String) session.getAttribute("password");

        if (username != null && password != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("username", username);
            map.add("password", password);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
            ResponseEntity<Void> response = restTemplate.postForEntity(SERVER_LOGIN_URL, request, Void.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                String accessToken = request.getHeaders().getValuesAsList(TokenManagement.ACCESS_TOKEN).get(0);
                String refreshToken = request.getHeaders().getValuesAsList(TokenManagement.REFRESH_TOKEN).get(0);

                if (accessToken != null && refreshToken != null) {
                    headers.add(TokenManagement.ACCESS_TOKEN, accessToken);
                    headers.add(TokenManagement.REFRESH_TOKEN, refreshToken);

                    HttpEntity<String> userRequest = new HttpEntity<>(headers);
                    ResponseEntity<AppUser> appUserResponse =
                            restTemplate.getForEntity(SERVER_USER_URL + username, AppUser.class, userRequest);

                    if (appUserResponse.getStatusCode().is2xxSuccessful() && appUserResponse.hasBody()) {
                        AppUser user = appUserResponse.getBody();

                        if (user != null) {
                            log.info("User {} found on the database", username);

                            // Get the user roles as SimpleGrantedAuthorities for Spring Security.
                            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
                            if (user.getRoles() != null && !user.getRoles().isEmpty()) {
                                user.getRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority(role.getName())));
                            }

                            // Create a Spring Security user to check on with.
                            return new User(
                                    user.getUsername(),
                                    user.getPassword(),
                                    authorities
                            );
                        }
                    }
                }
            }
        }
        log.error("User with username {} couldn't be found", username);
        throw new UsernameNotFoundException("User with username" + username + "couldn't be found");
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
