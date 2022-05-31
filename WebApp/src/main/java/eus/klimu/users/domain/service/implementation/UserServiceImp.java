package eus.klimu.users.domain.service.implementation;

import com.google.gson.Gson;
import eus.klimu.home.api.RequestMaker;
import eus.klimu.security.TokenManagement;
import eus.klimu.users.domain.model.AppUser;
import eus.klimu.users.domain.model.AppUserDTO;
import eus.klimu.users.domain.service.definition.UserService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImp implements UserService, UserDetailsService {

    @Getter
    private enum UserURL {

        BASE("/"), NAME("/username/"), GET_ALL("/all"), CREATE("/create"),
        CREATE_ALL("/create/all"), UPDATE("/update"), DELETE("/delete");

        private final String name;

        UserURL(String name) {
            this.name = "https://klimu.eus/RestAPI/user/" + name;
        }
    }

    private final Gson gson = new Gson();
    private final HttpSession session;
    private final PasswordEncoder passwordEncoder;
    private final RequestMaker requestMaker = new RequestMaker();

    @Nullable
    private JSONObject getTokensFromServer(String username, String password) {
        ResponseEntity<String> response = requestMaker.doPost(
                RequestMaker.SERVER_LOGIN_URL,
                requestMaker.generateHeaders(MediaType.APPLICATION_FORM_URLENCODED, null),
                requestMaker.generateBody(username, password)
        );
        if (response.getStatusCode().is2xxSuccessful() && response.hasBody()) {
            return new JSONObject(response.getBody());
        }
        return null;
    }

    @Nullable
    private AppUser getUserFromServer(String username, JSONObject tokens) {
        ResponseEntity<String> appUserResponse = requestMaker.doGet(
                RequestMaker.SERVER_USER_URL + username,
                requestMaker.addTokenToHeader(
                        new HttpHeaders(),
                        tokens.getString(TokenManagement.ACCESS_TOKEN),
                        tokens.getString(TokenManagement.REFRESH_TOKEN)
                ), null
        );
        // Check if the user was found.
        if (appUserResponse.getStatusCode().is2xxSuccessful() && appUserResponse.hasBody()) {
            return gson.fromJson(appUserResponse.getBody(), AppUser.class);
        }
        return null;
    }

    private User generateUser(AppUser user) {
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

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String password = (String) session.getAttribute("password");

        if (username != null && password != null) {
            try {
                // Get the tokens for accessing the server.
                JSONObject tokens = getTokensFromServer(username, password);
                if (tokens != null) {
                    if (tokens.has(TokenManagement.ACCESS_TOKEN) && tokens.has(TokenManagement.REFRESH_TOKEN)) {
                        // Get the user from the request.
                        AppUser user = getUserFromServer(username, tokens);

                        if (user != null) {
                            log.info("User {} found on the database", username);
                            return generateUser(user);
                        }
                    }
                    else if (tokens.has(RequestMaker.ERROR_MSG)) {
                        log.error(tokens.getString(RequestMaker.ERROR_MSG));
                        throw new UsernameNotFoundException(tokens.getString(RequestMaker.ERROR_MSG));
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
    public AppUser getUser(long id) {
        log.info("Fetching user with id={}", id);
        ResponseEntity<String> response = requestMaker.doGet(
                UserURL.BASE.getName() + id,
                requestMaker.addTokenToHeader(
                        requestMaker.generateHeaders(null, Collections.singletonList(MediaType.APPLICATION_JSON)),
                        (String) session.getAttribute(TokenManagement.ACCESS_TOKEN),
                        (String) session.getAttribute(TokenManagement.REFRESH_TOKEN)
                ), null
        );
        if (response.getStatusCode().is2xxSuccessful() && response.hasBody()) {
            return gson.fromJson(response.getBody(), AppUser.class);
        }
        return null;
    }

    @Override
    public AppUser getUser(String username) {
        log.info("Looking for user with username={}", username);
        ResponseEntity<String> response = requestMaker.doGet(
                UserURL.NAME.getName() + username,
                requestMaker.addTokenToHeader(
                        requestMaker.generateHeaders(null, Collections.singletonList(MediaType.APPLICATION_JSON)),
                        (String) session.getAttribute(TokenManagement.ACCESS_TOKEN),
                        (String) session.getAttribute(TokenManagement.REFRESH_TOKEN)
                ), null
        );
        if (response.getStatusCode().is2xxSuccessful() && response.hasBody()) {
            return gson.fromJson(response.getBody(), AppUser.class);
        }
        return null;
    }

    @Override
    public AppUser saveUser(AppUser user) {
        log.info("Saving user {} on the database", user.getUsername());

        // Encode the user password.
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Save the user.
        ResponseEntity<String> response = requestMaker.doPost(
                UserURL.CREATE.getName(),
                requestMaker.generateHeaders(MediaType.APPLICATION_JSON, Collections.singletonList(MediaType.APPLICATION_JSON)),
                gson.toJson(AppUserDTO.fromAppUser(user), AppUserDTO.class)
        );
        if (response.getStatusCode().is2xxSuccessful() && response.hasBody()) {
            return gson.fromJson(response.getBody(), AppUser.class);
        }
        return null;
    }

    @Override
    public AppUser updateUser(AppUser user) {
        ResponseEntity<String> response = requestMaker.doPut(
                UserURL.UPDATE.getName(),
                requestMaker.addTokenToHeader(
                        requestMaker.generateHeaders(null, Collections.singletonList(MediaType.APPLICATION_JSON)),
                        (String) session.getAttribute(TokenManagement.ACCESS_TOKEN),
                        (String) session.getAttribute(TokenManagement.REFRESH_TOKEN)
                ), gson.toJson(AppUserDTO.fromAppUser(user), AppUserDTO.class)
        );
        if (response.getStatusCode().is2xxSuccessful() && response.hasBody()) {
            return gson.fromJson(response.getBody(), AppUser.class);
        }
        return null;
    }

    @Override
    public void deleteUser(AppUser user) {
        ResponseEntity<String> response = requestMaker.doDelete(
                UserURL.DELETE.getName(),
                requestMaker.addTokenToHeader(
                        requestMaker.generateHeaders(null, Collections.singletonList(MediaType.APPLICATION_JSON)),
                        (String) session.getAttribute(TokenManagement.ACCESS_TOKEN),
                        (String) session.getAttribute(TokenManagement.REFRESH_TOKEN)
                ), gson.toJson(AppUserDTO.fromAppUser(user), AppUserDTO.class)
        );
        assert response.getStatusCode().is2xxSuccessful();
    }
}
