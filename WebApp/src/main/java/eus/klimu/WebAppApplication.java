package eus.klimu;

import eus.klimu.users.domain.model.AppUser;
import eus.klimu.users.domain.model.Role;
import eus.klimu.users.domain.repository.RoleRepository;
import eus.klimu.users.domain.repository.UserRepository;
import eus.klimu.users.domain.service.definition.RoleService;
import eus.klimu.users.domain.service.definition.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@SpringBootApplication
public class WebAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebAppApplication.class, args);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * @author Jon Navaridas
     * Generate the default users and roles for the application. The function checks if the elements have already been
     * created and if they exist in the database it won't create them.
     * @param userService The service for managing the users.
     * @param roleService The service for managing the roles.
     * @return A collection of functions that will be executed at the beginning of execution.
     */
    @Bean
    public CommandLineRunner run(UserService userService, RoleService roleService) {
        return args -> {
            // Define the basic objects of the application.
            List<Role> applicationRoles = Arrays.asList(
                    new Role(null, "USER_ROLE"),
                    new Role(null, "ADMIN_ROLE")
            );
            List<AppUser> applicationUsers = Arrays.asList(
                    new AppUser(null, "klimu.admin", "klimu@admin", "klimu", "admin",
                            "popbl6_talde2-group@alumni.mondragon.edu", new ArrayList<>()),
                    new AppUser(null, "basic-user", "1234", "basic", "user",
                            "basic.user@gmail.com", new ArrayList<>())
            );

            log.info("Starting database default generation, checking if data has already been created");

            // Generate the basic user roles.
            for (Role role : applicationRoles) {
                if (roleService.getRole(role.getName()) == null) {
                    roleService.saveRole(role);
                }
            }
            // Generate the basic application users.
            for (AppUser user : applicationUsers) {
                if (userService.getUser(user.getUsername()) == null) {
                    userService.saveUser(user);
                }
            }
            // Set user roles.
            roleService.addRoleToUser("klimu.admin", "USER_ROLE");
            roleService.addRoleToUser("klimu.admin", "ADMIN_ROLE");
            roleService.addRoleToUser("basic-user", "USER_ROLE");

            log.info("Generation has ended, the application is ready to be used");
        };
    }
    
}
