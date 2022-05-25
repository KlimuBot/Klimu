package eus.klimu.users.domain.service.implementation;

import com.google.gson.Gson;
import eus.klimu.home.api.RequestMaker;
import eus.klimu.security.TokenManagement;
import eus.klimu.users.domain.model.Role;
import eus.klimu.users.domain.service.definition.RoleService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class RoleServiceImp implements RoleService {

    @Getter
    private enum RoleURL {

        BASE("/"), NAME("/name/"), GET_ALL("/all"), SET("/set"), CREATE("/create"),
        CREATE_ALL("/create/all"), UPDATE("/update"), DELETE("/delete");

        private final String name;

        RoleURL(String name) {
            this.name = "https://klimu.eus/RestAPI/role/" + name;
        }
    }

    private final Gson gson = new Gson();
    private final HttpSession session;
    private final RequestMaker requestMaker = new RequestMaker();

    @Override
    public Role getRole(long id) {
        log.info("Fetching role with id={} from repository", id);
        ResponseEntity<String> response = requestMaker.doGet(
                RoleURL.BASE.getName() + id,
                requestMaker.addTokenToHeader(
                        requestMaker.generateHeaders(null, Collections.singletonList(MediaType.APPLICATION_JSON)),
                        (String) session.getAttribute(TokenManagement.ACCESS_TOKEN),
                        (String) session.getAttribute(TokenManagement.REFRESH_TOKEN)
                ), null
        );
        if (response.getStatusCode().is2xxSuccessful() && response.hasBody()) {
            return gson.fromJson(response.getBody(), Role.class);
        }
        return null;
    }

    @Override
    public Role getRole(String roleName) {
        log.info("Fetching role {} from repository", roleName);
        ResponseEntity<String> response = requestMaker.doGet(
                RoleURL.NAME.getName() + roleName,
                requestMaker.addTokenToHeader(
                        requestMaker.generateHeaders(null, Collections.singletonList(MediaType.APPLICATION_JSON)),
                        (String) session.getAttribute(TokenManagement.ACCESS_TOKEN),
                        (String) session.getAttribute(TokenManagement.REFRESH_TOKEN)
                ), null
        );
        if (response.getStatusCode().is2xxSuccessful() && response.hasBody()) {
            return gson.fromJson(response.getBody(), Role.class);
        }
        return null;
    }

    @Override
    public List<Role> getAllRoles() {
        log.info("Fetching all roles from database");
        ResponseEntity<String> response = requestMaker.doGet(
                RoleURL.GET_ALL.getName(),
                requestMaker.addTokenToHeader(
                        requestMaker.generateHeaders(null, Collections.singletonList(MediaType.APPLICATION_JSON)),
                        (String) session.getAttribute(TokenManagement.ACCESS_TOKEN),
                        (String) session.getAttribute(TokenManagement.REFRESH_TOKEN)
                ), null
        );
        return rolesToList(response);
    }

    @Override
    public Role saveRole(Role role) {
        log.info("Saving new role {}", role.getName());
        ResponseEntity<String> response = requestMaker.doPost(
                RoleURL.CREATE.getName(),
                requestMaker.addTokenToHeader(
                        requestMaker.generateHeaders(null, Collections.singletonList(MediaType.APPLICATION_JSON)),
                        (String) session.getAttribute(TokenManagement.ACCESS_TOKEN),
                        (String) session.getAttribute(TokenManagement.REFRESH_TOKEN)
                ), gson.toJson(role, Role.class)
        );
        if (response.getStatusCode().is2xxSuccessful() && response.hasBody()) {
            return gson.fromJson(response.getBody(), Role.class);
        }
        return null;
    }

    @Override
    public List<Role> saveAllRoles(List<Role> roles) {
        log.info("Saving {} roles on the database", roles.size());
        ResponseEntity<String> response = requestMaker.doPost(
                RoleURL.CREATE.getName(),
                requestMaker.addTokenToHeader(
                        requestMaker.generateHeaders(null, Collections.singletonList(MediaType.APPLICATION_JSON)),
                        (String) session.getAttribute(TokenManagement.ACCESS_TOKEN),
                        (String) session.getAttribute(TokenManagement.REFRESH_TOKEN)
                ), gson.toJson(roles)
        );
        return rolesToList(response);
    }

    @Override
    public Role updateRole(Role role) {
        log.info("Updating role with id={} on the database", role.getId());
        ResponseEntity<String> response = requestMaker.doPut(
                RoleURL.UPDATE.getName(),
                requestMaker.addTokenToHeader(
                        requestMaker.generateHeaders(null, Collections.singletonList(MediaType.APPLICATION_JSON)),
                        (String) session.getAttribute(TokenManagement.ACCESS_TOKEN),
                        (String) session.getAttribute(TokenManagement.REFRESH_TOKEN)
                ), gson.toJson(role, Role.class)
        );
        if (response.getStatusCode().is2xxSuccessful() && response.hasBody()) {
            return gson.fromJson(response.getBody(), Role.class);
        }
        return null;
    }

    @Override
    public void deleteRole(Role role) {
        log.info("Deleting role with id={} from the database", role.getName());
        ResponseEntity<String> response = requestMaker.doDelete(
                RoleURL.DELETE.getName(),
                requestMaker.addTokenToHeader(
                        requestMaker.generateHeaders(null, Collections.singletonList(MediaType.APPLICATION_JSON)),
                        (String) session.getAttribute(TokenManagement.ACCESS_TOKEN),
                        (String) session.getAttribute(TokenManagement.REFRESH_TOKEN)
                ), gson.toJson(role, Role.class)
        );
        assert response.getStatusCode().is2xxSuccessful();
    }

    @Override
    public void addRoleToUser(String username, String roleName) {
        log.info("Adding role {} to user {}", roleName, username);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("username", username);
        body.add("roleName", roleName);

        ResponseEntity<String> response = requestMaker.doPut(
                RoleURL.UPDATE.getName(),
                requestMaker.addTokenToHeader(
                        requestMaker.generateHeaders(null, Collections.singletonList(MediaType.APPLICATION_JSON)),
                        (String) session.getAttribute(TokenManagement.ACCESS_TOKEN),
                        (String) session.getAttribute(TokenManagement.REFRESH_TOKEN)
                ), body
        );
        assert response.getStatusCode().is2xxSuccessful();
    }

    private List<Role> rolesToList(ResponseEntity<String> response) {
        if (response.getStatusCode().is2xxSuccessful() && response.hasBody()) {
            JSONArray jsonChannels = new JSONArray(response.getBody());
            List<Role> roles = new ArrayList<>();

            jsonChannels.forEach(role -> roles.add(gson.fromJson(role.toString(), Role.class)));
            return roles;
        }
        return Collections.emptyList();
    }
}
