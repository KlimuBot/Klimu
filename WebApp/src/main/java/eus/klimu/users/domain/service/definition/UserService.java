package eus.klimu.users.domain.service.definition;

import eus.klimu.users.domain.model.AppUser;

public interface UserService {

    AppUser saveUser(AppUser user);
    AppUser getUser(String username);

}
