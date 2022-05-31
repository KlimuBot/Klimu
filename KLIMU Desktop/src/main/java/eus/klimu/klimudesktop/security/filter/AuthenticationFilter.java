package eus.klimu.klimudesktop.security.filter;

import eus.klimu.klimudesktop.connection.RequestMaker;
import eus.klimu.klimudesktop.security.TokenManagement;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.MultiValueMap;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Collections;

@Slf4j
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final RequestMaker requestMaker = new RequestMaker();

    public AuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        // Get the user parameters.
        String username = request.getParameter(RequestMaker.USERNAME_HEADER);
        String password = request.getParameter(RequestMaker.PASSWORD_HEADER);
        log.info("Trying to log user {}", username);

        // Set password on session.
        HttpSession session = request.getSession();
        session.setAttribute(RequestMaker.PASSWORD_HEADER, password);

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
        String password = (String) session.getAttribute(RequestMaker.PASSWORD_HEADER);
        session.removeAttribute(RequestMaker.PASSWORD_HEADER);

        // Generate the request.
        HttpHeaders headers = requestMaker.generateHeaders(
                MediaType.APPLICATION_FORM_URLENCODED,
                Collections.singletonList(MediaType.APPLICATION_JSON)
        );
        MultiValueMap<String, String> map = requestMaker.generateBody(username, password);
        ResponseEntity<String> serverResponse = requestMaker.doPost(RequestMaker.TOKEN_URL, headers, map);

        if (serverResponse.getStatusCode().is2xxSuccessful()) {
            JSONObject body = new JSONObject(serverResponse.getBody());

            // Save the tokens on the session and redirect the user to index.
            tokenManagement.setTokenOnSession(
                    body.getString(TokenManagement.ACCESS_TOKEN),
                    body.getString(TokenManagement.REFRESH_TOKEN),
                    request.getSession()
            );
            response.sendRedirect("/channel/subscription");
        } else {
            response.setHeader(RequestMaker.ERROR_MSG, "El usuario o contraseña no son correctos");
            response.sendRedirect("/login/sign-in");
        }
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException, ServletException {
        HttpSession session = request.getSession();
        session.removeAttribute(RequestMaker.PASSWORD_HEADER);

        response.setHeader(RequestMaker.ERROR_MSG, "El usuario o contraseña no son correctos");
        response.sendRedirect("/login/sign-in");
    }
}
