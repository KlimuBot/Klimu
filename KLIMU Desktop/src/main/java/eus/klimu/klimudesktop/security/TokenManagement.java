package eus.klimu.klimudesktop.security;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.google.gson.Gson;
import eus.klimu.klimudesktop.connection.RequestMaker;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collection;

@Slf4j
public class TokenManagement {

    public static final String TOKEN_SIGNATURE_NAME = "Bearer ";
    public static final String ACCESS_TOKEN = "accessToken";
    public static final String REFRESH_TOKEN = "refreshToken";

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
}
