package eus.klimu.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.servlet.http.HttpSession;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

@Slf4j
public class TokenManagement {

    /**
     * Access token expiration time.
     * 5 minutes in milliseconds.
     */
    public static final int ACCESS_TIME = 5 * 60 * 1000;

    /**
     * Refresh token expiration time.
     * 6 hours in milliseconds.
     */
    public static final int REFRESH_TIME = 6 * 60 * 60 * 1000;

    public static final String TOKEN_SIGNATURE_NAME = "Bearer ";
    public static final String ACCESS_TOKEN = "accessToken";
    public static final String REFRESH_TOKEN = "refreshToken";

    private final Algorithm algorithm;
    private final JWTVerifier verifier;

    public TokenManagement() {
        // Este secret hay que moverlo a un archivo seguro y cargarlo de ahi.
        algorithm = Algorithm.HMAC256("klimu-secret".getBytes(StandardCharsets.UTF_8));
        verifier = JWT.require(algorithm).build();
    }

    public HttpEntity<MultiValueMap<String, String>> generateHttpRequest(String username, String password) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("username", username);
        map.add("password", password);

        return new HttpEntity<>(map, headers);
    }

    public String generateToken(User user, String requestURL, int durationTime) {
        return JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + durationTime))
                .withIssuer(requestURL)
                .withClaim("roles", user.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .sign(algorithm);
    }

    public void setTokenOnSession(String accessToken, String refreshToken, HttpSession session) {
        session.setAttribute(ACCESS_TOKEN, TOKEN_SIGNATURE_NAME + accessToken);
        session.setAttribute(REFRESH_TOKEN, TOKEN_SIGNATURE_NAME + refreshToken);
        log.info("The user has been authenticated, their tokens have been generated");
    }

    public UsernamePasswordAuthenticationToken getUsernamePasswordToken(String authToken) throws JWTVerificationException {
        String token = authToken.substring(TOKEN_SIGNATURE_NAME.length());
        DecodedJWT decodedJWT = verifier.verify(token);

        String username = decodedJWT.getSubject();
        String[] roles = decodedJWT.getClaim("roles").asArray(String.class);

        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        stream(roles).forEach(role -> authorities.add(new SimpleGrantedAuthority(role)));

        return new UsernamePasswordAuthenticationToken(
                username, null, authorities
        );
    }

    public User getUserFromToken(String authToken) {
        String token = authToken.substring(TOKEN_SIGNATURE_NAME.length());
        DecodedJWT decodedJWT = verifier.verify(token);

        String username = decodedJWT.getSubject();
        Collection<String> roles = decodedJWT.getClaim("roles").asList(String.class);

        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        roles.forEach(role -> authorities.add(new SimpleGrantedAuthority(role)));

        return new User(username, "null", authorities);
    }

}
