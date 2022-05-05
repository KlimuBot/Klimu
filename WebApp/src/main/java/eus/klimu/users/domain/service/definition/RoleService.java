package eus.klimu.users.domain.service.definition;

import eus.klimu.users.domain.model.Role;

import java.util.List;

public interface RoleService {

    Role saveRole(Role role);
    List<Role> getAllRoles();
    Role getRole(String roleName);
    void addRoleToUser(String username, String roleName);

}
