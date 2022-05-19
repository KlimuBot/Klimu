package eus.klimu.security.filter;

import eus.klimu.security.TokenManagement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Slf4j
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private static final String TOKEN_URL = "http://localhost:8080/login/grant-access";
    private static final String USERNAME_HEADER = "username";
    private static final String PASSWORD_HEADER = "password";

    private final AuthenticationManager authenticationManager;
    private final RestTemplate restTemplate = new RestTemplate();

    public AuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        // Get the user parameters.
        String username = request.getParameter(USERNAME_HEADER);
        String password = request.getParameter(PASSWORD_HEADER);
        log.info("Trying to log user {}", username);

        // Set password on session.
        HttpSession session = request.getSession();
        session.setAttribute(PASSWORD_HEADER, password);

        // Create an authentication token for the user.
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                username, password
        );
        return authenticationManager.authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authentication) throws IOException, ServletException {
        User user = (User) authentication.getPrincipal();
        TokenManagement tokenManagement = new TokenManagement();
        HttpSession session = request.getSession();

        String username = user.getUsername();
        String password = (String) session.getAttribute(PASSWORD_HEADER);

        ResponseEntity<Object> serverResponse = restTemplate.getForEntity(
                TOKEN_URL, Object.class,
                tokenManagement.generateHttpRequest(username, password)
        );
        session.removeAttribute(PASSWORD_HEADER);

        if (serverResponse.getStatusCode().is2xxSuccessful()) {
            // Generate access and refresh tokens.
            HttpHeaders headers = serverResponse.getHeaders();

            String accessToken = headers.getValuesAsList(TokenManagement.ACCESS_TOKEN).get(0);
            String refreshToken = headers.getValuesAsList(TokenManagement.REFRESH_TOKEN).get(0);

            // Save the tokens on the session and redirect the user to index.
            tokenManagement.setTokenOnSession(accessToken, refreshToken, request.getSession());
            response.sendRedirect("/subscription");
        }
        response.setHeader("errorMsg", "El usuario o contraseña no son correctos");
        response.sendRedirect("/login/sign-in");
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException, ServletException {
        HttpSession session = request.getSession();
        session.removeAttribute(PASSWORD_HEADER);

        response.setHeader("errorMsg", "El usuario o contraseña no son correctos");
        response.sendRedirect("/login/sign-in");
    }
}
