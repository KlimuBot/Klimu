package eus.klimu.users.api;

import eus.klimu.users.domain.model.Role;
import eus.klimu.users.domain.model.RoleDTO;
import eus.klimu.users.domain.service.definition.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Controller
@RequestMapping("/role")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping("/all")
    public ResponseEntity<List<Role>> getAllRoles() {
        return ResponseEntity.ok().body(roleService.getAllRoles());
    }

    @GetMapping("/{roleName}")
    public ResponseEntity<Role> getRole(@PathVariable String roleName) {
        Role role = roleService.getRole(roleName);
        if (role != null) {
            return ResponseEntity.ok().body(role);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/save")
    public ResponseEntity<Role> createRole(@RequestBody RoleDTO role) {
        Role newRole = roleService.saveRole(new Role(role.getId(), role.getName()));
        if (newRole != null) {
            return ResponseEntity.created(
                    // Specify where has the object been created.
                    URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/role/save").toUriString())
            ).body(newRole);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/set")
    public ResponseEntity<Object> setUserRole(
            @RequestParam String username,
            @RequestParam String roleName
    ) {
        roleService.addRoleToUser(username, roleName);
        return ResponseEntity.ok().build();
    }

}
