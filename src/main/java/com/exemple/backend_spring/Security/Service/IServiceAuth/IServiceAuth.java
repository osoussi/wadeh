package com.exemple.backend_spring.Security.Service.IServiceAuth;

import com.exemple.backend_spring.Security.Entity.Role;
import com.exemple.backend_spring.Security.Entity.User;


public interface IServiceAuth {

    User createAppUser(User appUser);
    Role createAppRole(Role appRole);
    void addRoleToUser(String username, String role);
    User LoadUserByUserName(String number);
}
