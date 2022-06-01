package eus.klimu.klimudesktop.security.filter;

import eus.klimu.klimudesktop.connection.RequestMaker;
import eus.klimu.klimudesktop.security.TokenManagement;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Slf4j
public class AuthorizationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        boolean error = false;
        if (
                !request.getServletPath().startsWith("/css") &&
                !request.getServletPath().startsWith("/js") &&
                !request.getServletPath().startsWith("/media") &&
                !request.getServletPath().startsWith("/login")
        ) {
            HttpSession session = request.getSession();
            TokenManagement tokenManagement = new TokenManagement();

            // Check the access token.
            String accessToken = (String) session.getAttribute(TokenManagement.ACCESS_TOKEN);
            if (accessToken != null && accessToken.startsWith(TokenManagement.TOKEN_SIGNATURE_NAME)) {
                try {
                    UsernamePasswordAuthenticationToken authToken = tokenManagement
                            .getUsernamePasswordToken(session, accessToken);
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } catch (Exception accessException) {
                    String refreshToken = (String) session.getAttribute(TokenManagement.REFRESH_TOKEN);
                    try {
                        UsernamePasswordAuthenticationToken authToken = tokenManagement
                                .getUsernamePasswordToken(session, refreshToken);
                        SecurityContextHolder.getContext().setAuthentication(authToken);

                        // Generate a new access and refresh token for the user.
                        String json = tokenManagement.getTokensAsJSON(accessToken, refreshToken).toString();
                        ResponseEntity<JSONObject> refreshResponse = new RequestMaker().doGetJSON(RequestMaker.REFRESH_URL, json);

                        if (refreshResponse.getStatusCode().is2xxSuccessful() && refreshResponse.hasBody()) {
                            JSONObject tokens = refreshResponse.getBody();

                            if (
                                tokens != null && tokens.has(TokenManagement.ACCESS_TOKEN) &&
                                tokens.has(TokenManagement.REFRESH_TOKEN)
                            ) {
                                tokenManagement.setTokenOnSession(
                                        tokens.getString(TokenManagement.ACCESS_TOKEN),
                                        tokens.getString(TokenManagement.REFRESH_TOKEN),
                                        session
                                );
                            }
                            else if (tokens != null && tokens.has(RequestMaker.ERROR_MSG)) {
                                throw new ServletException(tokens.getString(RequestMaker.ERROR_MSG));
                            } else {
                                throw new ServletException("Could not refresh the tokens");
                            }
                        }
                    } catch (Exception refreshException) {
                        response.setHeader(RequestMaker.ERROR_MSG, refreshException.getMessage());
                        response.sendError(HttpServletResponse.SC_FORBIDDEN);
                        error = true;
                    }
                }
            }
        }
        if (!error) {
            filterChain.doFilter(request, response);
        }
    }

}
