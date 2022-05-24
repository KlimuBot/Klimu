package eus.klimu.users.domain.service.implementation;

import com.google.gson.Gson;
import eus.klimu.home.api.RequestMaker;
import eus.klimu.security.TokenManagement;
import eus.klimu.users.domain.model.AppUser;
import eus.klimu.users.domain.repository.UserRepository;
import eus.klimu.users.domain.service.definition.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImp implements UserService, UserDetailsService {

    private final HttpSession session;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final RequestMaker requestMaker = new RequestMaker();

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String password = (String) session.getAttribute("password");

        if (username != null && password != null) {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

                MultiValueMap<String, String> requestBody = requestMaker.generateBody(username, password);
                ResponseEntity<String> response = requestMaker.doPost(RequestMaker.SERVER_LOGIN_URL, headers, requestBody);

                if (response.getStatusCode().is2xxSuccessful() && response.hasBody()) {
                    JSONObject responseBody = new JSONObject(response.getBody());

                    // Check if the response body contains the tokens.
                    if (responseBody.has(TokenManagement.ACCESS_TOKEN) && responseBody.has(TokenManagement.REFRESH_TOKEN)) {
                        // Put the tokens as headers for the request.
                        headers = requestMaker.addTokenToHeader(
                                new HttpHeaders(),
                                responseBody.getString(TokenManagement.ACCESS_TOKEN),
                                responseBody.getString(TokenManagement.REFRESH_TOKEN)
                        );
                        // Get the user from the request.
                        ResponseEntity<String> appUserResponse = requestMaker
                                .doGet(RequestMaker.SERVER_USER_URL + username, headers, null);

                        // Check if the user was found.
                        if (appUserResponse.getStatusCode().is2xxSuccessful() && appUserResponse.hasBody()) {
                            AppUser user = new Gson().fromJson(appUserResponse.getBody(), AppUser.class);

                            if (user != null) {
                                log.info("User {} found on the database", username);

                                // Get the user roles as SimpleGrantedAuthorities for Spring Security.
                                Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
                                if (user.getRoles() != null && !user.getRoles().isEmpty()) {
                                    user.getRoles().forEach(role -> authorities.add(
                                            new SimpleGrantedAuthority(role.getName())
                                    ));
                                }

                                // Create a Spring Security user to check on with.
                                return new User(
                                        user.getUsername(),
                                        user.getPassword(),
                                        authorities
                                );
                            }
                        }
                        JSONObject object = new JSONObject(appUserResponse.getBody());
                        if (object.has(RequestMaker.ERROR_MSG)) {
                            log.error(object.getString(RequestMaker.ERROR_MSG));
                            throw new UsernameNotFoundException(object.getString(RequestMaker.ERROR_MSG));
                        }
                    }
                }
            } catch (RestClientException e) {
                log.error(e.getMessage());
                throw new UsernameNotFoundException(e.getMessage());
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
