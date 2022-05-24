package eus.klimu.home.api;

import eus.klimu.security.TokenManagement;
import lombok.NoArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@NoArgsConstructor
public class RequestMaker {

    public static final String USERNAME_HEADER = "username";
    public static final String PASSWORD_HEADER = "password";

    public static final String ERROR_MSG = "errorMsg";

    public static final String SERVER_LOGIN_URL = "http://klimu.eus/RestAPI/login";
    public static final String SERVER_USER_URL = "http://klimu.eus/RestAPI/user/username/";

    public static final String TOKEN_URL = "http://klimu.eus/RestAPI/login";
    public static final String TOKEN_AUTH_URL = "http://klimu.eus/RestAPI/access/auth/";
    public static final String REFRESH_URL = "http://klimu.eus/RestAPI/access/refresh";

    private final RestTemplate restTemplate = new RestTemplate();

    public HttpHeaders generateHeaders(MediaType contentType, List<MediaType> acceptType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(contentType);
        headers.setAccept(acceptType);
        return headers;
    }

    public HttpHeaders addTokenToHeader(HttpHeaders headers, String accessToken, String refreshToken) {
        headers.add(TokenManagement.ACCESS_TOKEN, accessToken);
        headers.add(TokenManagement.REFRESH_TOKEN, refreshToken);
        return headers;
    }

    public MultiValueMap<String, String> generateBody(String username, String password) {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add(USERNAME_HEADER, username);
        map.add(PASSWORD_HEADER, password);
        return map;
    }

    /**
     * Make a GET request to a specific URL.
     * @param url The URL of the server method.
     * @param headers The headers for the request.
     * @param body The body for the request, can be null.
     * @return The response from the server as a JSON object.
     */
    public ResponseEntity<String> doGet(String url, HttpHeaders headers, @Nullable String body) {
        HttpEntity<String> request;
        if (body != null) {
            request = new HttpEntity<>(body, headers);
        } else {
            request = new HttpEntity<>(headers);
        }
        return restTemplate.exchange(url, HttpMethod.GET, request, String.class);
    }

    /**
     * Make a GET request to a specific URL with a JSON.
     * @param url The URL of the server method.
     * @param json A json object for the request.
     * @return A response with a JSON object.
     */
    public ResponseEntity<JSONObject> doGetJSON(String url, String json) {
        return restTemplate.getForEntity(url, JSONObject.class, json);
    }

    /**
     * Make a POST request to a specific URL.
     * @param url The URL of the server method.
     * @param headers The headers for the request.
     * @param body The body for the request.
     * @return The response from the server as a JSON object.
     */
    public ResponseEntity<String> doPost(String url, HttpHeaders headers, MultiValueMap<String, String> body) {
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        return restTemplate.exchange(url, HttpMethod.POST, request, String.class);
    }

}
