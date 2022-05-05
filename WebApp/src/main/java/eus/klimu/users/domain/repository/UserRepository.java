package eus.klimu.users.domain.repository;

import eus.klimu.users.domain.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<AppUser, Long> {

    AppUser findByUsername(String username);

}
