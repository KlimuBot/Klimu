package eus.klimu.klimudesktop.connection;

import eus.klimu.klimudesktop.security.TokenManagement;
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

    public static final String SERVER_LOGIN_URL = "https://klimu.eus/RestAPI/login";
    public static final String SERVER_USER_URL = "https://klimu.eus/RestAPI/user/username/";

    public static final String TOKEN_URL = "https://klimu.eus/RestAPI/login";
    public static final String TOKEN_AUTH_URL = "https://klimu.eus/RestAPI/access/auth/";
    public static final String USER_FROM_TOKEN = "https://klimu.eus/RestAPI/user/from-token/";
    public static final String REFRESH_URL = "https://klimu.eus/RestAPI/access/refresh";

    private final RestTemplate restTemplate = new RestTemplate();

    public HttpHeaders generateHeaders(@Nullable MediaType contentType, @Nullable List<MediaType> acceptType) {
        HttpHeaders headers = new HttpHeaders();

        if (contentType != null) {
            headers.setContentType(contentType);
        }
        if (acceptType != null) {
            headers.setAccept(acceptType);
        }
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
     * @param body The body for the request, can be null.
     * @return The response from the server as a JSON object.
     */
    public ResponseEntity<String> doPost(String url, HttpHeaders headers, @Nullable String body) {
        HttpEntity<String> request;
        if (body != null) {
            request = new HttpEntity<>(body, headers);
        } else {
            request = new HttpEntity<>(headers);
        }
        return restTemplate.exchange(url, HttpMethod.POST, request, String.class);
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

    /**
     * Make a PUT request to a specific URL.
     * @param url The URL of the server method.
     * @param headers The headers for the request.
     * @param body The body for the request, can be null.
     * @return The response from the server as a JSON object.
     */
    public ResponseEntity<String> doPut(String url, HttpHeaders headers, @Nullable String body) {
        HttpEntity<String> request;
        if (body != null) {
            request = new HttpEntity<>(body, headers);
        } else {
            request = new HttpEntity<>(headers);
        }
        return restTemplate.exchange(url, HttpMethod.PUT, request, String.class);
    }

    /**
     * Make a PUT request to a specific URL.
     * @param url The URL of the server method.
     * @param headers The headers for the request.
     * @param body The body for the request, can be null.
     * @return The response from the server as a JSON object.
     */
    public ResponseEntity<String> doPut(String url, HttpHeaders headers, @Nullable MultiValueMap<String, String> body) {
        if (body != null) {
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
            return restTemplate.exchange(url, HttpMethod.PUT, request, String.class);
        } else {
            HttpEntity<String> request = new HttpEntity<>(headers);
            return restTemplate.exchange(url, HttpMethod.PUT, request, String.class);
        }
    }

    /**
     * Make a DELETE request to a specific URL.
     * @param url The URL of the server method.
     * @param headers The headers for the request.
     * @param body The body for the request, can be null.
     * @return The response from the server as a JSON object.
     */
    public ResponseEntity<String> doDelete(String url, HttpHeaders headers, @Nullable String body) {
        HttpEntity<String> request;
        if (body != null) {
            request = new HttpEntity<>(body, headers);
        } else {
            request = new HttpEntity<>(headers);
        }
        return restTemplate.exchange(url, HttpMethod.DELETE, request, String.class);
    }
}
