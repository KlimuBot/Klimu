package eus.klimu.users.api;

import eus.klimu.users.domain.model.AppUser;
import eus.klimu.users.domain.service.definition.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{username}")
    public ResponseEntity<AppUser> getUser(@PathVariable String username) {
        return ResponseEntity.ok().body(userService.getUser(username));
    }

    @GetMapping("/create")
    public String getCreateUser() {
        return "users/create_user";
    }

    @PostMapping("/create")
    public ResponseEntity<AppUser> saveUser(@RequestBody AppUser user) {
        AppUser newUser = userService.saveUser(user);
        if (user != null) {
            return ResponseEntity.created(
                    // Specify where has the object been created.
                    URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/user/save").toUriString())
            ).body(newUser);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

}
