package com.exemple.backend_spring.Security.Service.ServiceAuth;

import com.exemple.backend_spring.Security.Entity.Role;
import com.exemple.backend_spring.Security.Entity.User;
import com.exemple.backend_spring.Security.Service.IServiceAuth.IServiceAuth;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final IServiceAuth iServiceAuth;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User appUser = iServiceAuth.LoadUserByUserName(username);
        if(appUser == null) throw new UsernameNotFoundException("User with " + username + "does not exist");
        String[] roles = appUser.getRoleList().stream().map(Role::getRoleName).toArray(String[]::new);
        return org.springframework.security.core.userdetails.User
                .withUsername(appUser.getUsername())
                .password(appUser.getPassword())
                .roles(roles)
                .build();
    }
}
