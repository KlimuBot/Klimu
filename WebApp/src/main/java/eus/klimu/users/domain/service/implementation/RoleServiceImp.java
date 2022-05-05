package eus.klimu.users.domain.service.implementation;

import eus.klimu.users.domain.model.Role;
import eus.klimu.users.domain.repository.RoleRepository;
import eus.klimu.users.domain.repository.UserRepository;
import eus.klimu.users.domain.service.definition.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class RoleServiceImp implements RoleService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public Role saveRole(Role role) {
        log.info("Saving new role {}", role.getName());
        return roleRepository.save(role);
    }

    @Override
    public List<Role> getAllRoles() {
        log.info("Fetching all roles from database");
        return roleRepository.findAll();
    }

    @Override
    public Role getRole(String roleName) {
        log.info("Fetching role {} from repository", roleName);
        return roleRepository.findByName(roleName);
    }

    @Override
    public void addRoleToUser(String username, String roleName) {
        log.info("Adding role {} to user {}", roleName, username);
        userRepository.findByUsername(username)
                .getRoles().add(roleRepository.findByName(roleName));
    }
}
