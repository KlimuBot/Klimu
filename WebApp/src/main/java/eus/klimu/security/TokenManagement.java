package eus.klimu.security;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.google.gson.Gson;
import eus.klimu.home.api.RequestMaker;
import eus.klimu.users.domain.model.AppUser;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.client.HttpClientErrorException;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

@Slf4j
public class TokenManagement {

    public static final String TOKEN_SIGNATURE_NAME = "Bearer ";
    public static final String ACCESS_TOKEN = "accessToken";
    public static final String REFRESH_TOKEN = "refreshToken";

    private final Gson gson = new Gson();

    public void setTokenOnSession(String accessToken, String refreshToken, HttpSession session) {
        session.setAttribute(ACCESS_TOKEN, accessToken);
        session.setAttribute(REFRESH_TOKEN, refreshToken);
        log.info("The user has been authenticated, their tokens have been generated");
    }

    public JSONObject getTokensAsJSON(String accessToken, String refreshToken) {
        JSONObject json = new JSONObject();

        json.append(TokenManagement.ACCESS_TOKEN, accessToken);
        json.append(TokenManagement.REFRESH_TOKEN, refreshToken);

        return json;
    }

    public UsernamePasswordAuthenticationToken getUsernamePasswordToken(HttpSession session, String authToken)
            throws JWTVerificationException {
        HttpHeaders headers = new HttpHeaders();
        headers.add(ACCESS_TOKEN, (String) session.getAttribute(ACCESS_TOKEN));
        headers.add(REFRESH_TOKEN, (String) session.getAttribute(REFRESH_TOKEN));

        ResponseEntity<String> response = new RequestMaker().doGet(
                RequestMaker.TOKEN_AUTH_URL + authToken, headers, null
        );
        if (response.hasBody()) {
            JSONObject json = new JSONObject(response.getBody());
            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();

            json.getJSONArray("authorities").forEach(role ->
                authorities.add(new SimpleGrantedAuthority(((JSONObject) role).getString("role")))
            );
            return new UsernamePasswordAuthenticationToken(json.getString("principal"), null, authorities);
        } else {
            return null;
        }
    }

    @Nullable
    public AppUser getUserFromTokens(HttpSession session) {
        RequestMaker requestMaker = new RequestMaker();

        try {
            // Try getting the user with the access token.
            ResponseEntity<String> response = requestMaker.doGet(
                    RequestMaker.USER_FROM_TOKEN + session.getAttribute(TokenManagement.ACCESS_TOKEN),
                    requestMaker.addTokenToHeader(
                            requestMaker.generateHeaders(null, Collections.singletonList(MediaType.APPLICATION_JSON)),
                            (String) session.getAttribute(TokenManagement.ACCESS_TOKEN),
                            (String) session.getAttribute(TokenManagement.REFRESH_TOKEN)
                    ), null
            );
            if (response.getStatusCode().is2xxSuccessful() && response.hasBody()) {
                return gson.fromJson(response.getBody(), AppUser.class);
            }
            else if (response.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {

                // Try getting the user with the refresh token.
                response = requestMaker.doGet(
                        RequestMaker.USER_FROM_TOKEN + session.getAttribute(TokenManagement.REFRESH_TOKEN),
                        requestMaker.addTokenToHeader(
                                requestMaker.generateHeaders(null, Collections.singletonList(MediaType.APPLICATION_JSON)),
                                (String) session.getAttribute(TokenManagement.ACCESS_TOKEN),
                                (String) session.getAttribute(TokenManagement.REFRESH_TOKEN)
                        ), null
                );
                if (response.getStatusCode().is2xxSuccessful() && response.hasBody()) {
                    return gson.fromJson(response.getBody(), AppUser.class);
                }
            }
            return null;
        } catch (HttpClientErrorException e) {
            return null;
        }
    }
}
