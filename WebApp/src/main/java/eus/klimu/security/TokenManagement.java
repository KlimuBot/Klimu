package eus.klimu.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.sun.deploy.net.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
public class TokenManagement {

    private static final String TOKEN_SIGNATURE_NAME = "Bearer ";

    private final Algorithm algorithm;
    private final JWTVerifier verifier;

    public TokenManagement() {
        // Este secret hay que moverlo a un archivo seguro y cargarlo de ahi.
        algorithm = Algorithm.HMAC256("klimu-secret".getBytes(StandardCharsets.UTF_8));
        verifier = JWT.require(algorithm).build();
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
        session.setAttribute("accessToken", TOKEN_SIGNATURE_NAME + accessToken);
        session.setAttribute("refreshToken", TOKEN_SIGNATURE_NAME + refreshToken);
        log.info("The user has been authenticated, their tokens have been generated.");
    }

}
