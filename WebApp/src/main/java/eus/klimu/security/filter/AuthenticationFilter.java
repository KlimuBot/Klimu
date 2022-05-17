package eus.klimu.security.filter;

import eus.klimu.security.TokenManagement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Slf4j
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    public AuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        // Get the user parameters.
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        log.info("Trying to log user {}", username);

        // Set password on session.
        HttpSession session = request.getSession();
        session.setAttribute("password", password);

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

        log.info("{} | {}", user.getUsername(), user.getPassword());

        // Generate access and refresh tokens.
        String accessToken = tokenManagement.generateToken(
                user, request.getRequestURL().toString(), TokenManagement.ACCESS_TIME
        );
        String refreshToken = tokenManagement.generateToken(
                user, request.getRequestURL().toString(), TokenManagement.REFRESH_TIME
        );
        // Save the tokens on the session and redirect the user to index.
        tokenManagement.setTokenOnSession(accessToken, refreshToken, request.getSession());
        response.sendRedirect("/subscription");
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException, ServletException {
        HttpSession session = request.getSession();
        session.removeAttribute("password");

        response.sendRedirect("/login/sign-in?error=User-not-found");
    }
}
