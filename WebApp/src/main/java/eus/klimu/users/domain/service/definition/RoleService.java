package eus.klimu.users.domain.service.definition;

import eus.klimu.users.domain.model.Role;

import java.util.List;

public interface RoleService {

    Role getRole(long id);
    Role getRole(String roleName);
    List<Role> getAllRoles();
    Role saveRole(Role role);
    List<Role> saveAllRoles(List<Role> roles);
    Role updateRole(Role role);
    void deleteRole(Role role);
    void addRoleToUser(String username, String roleName);

}
