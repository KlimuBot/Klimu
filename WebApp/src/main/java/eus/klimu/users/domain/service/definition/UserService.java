package eus.klimu.users.domain.service.definition;

import eus.klimu.users.domain.model.AppUser;

public interface UserService {

    AppUser getUser(long id);
    AppUser getUser(String username);
    AppUser saveUser(AppUser user);
    AppUser updateUser(AppUser user);
    void deleteUser(AppUser user);

}
