package eus.klimu.security.filter;

import eus.klimu.security.TokenManagement;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.client.RestTemplate;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Slf4j
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private static final String TOKEN_URL = "http://klimu.eus/RestAPI/login";
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
        session.removeAttribute(PASSWORD_HEADER);

        ResponseEntity<JSONObject> serverResponse = restTemplate.postForEntity(
                TOKEN_URL, tokenManagement.generateHttpRequest(username, password), JSONObject.class
        );
        if (serverResponse.getStatusCode().is2xxSuccessful()) {
            JSONObject body = serverResponse.getBody();

            if (body != null) {
                // Save the tokens on the session and redirect the user to index.
                tokenManagement.setTokenOnSession(
                        body.getString(TokenManagement.ACCESS_TOKEN),
                        body.getString(TokenManagement.REFRESH_TOKEN),
                        request.getSession()
                );
                response.sendRedirect("/subscription");
            }
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
