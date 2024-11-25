package com.exemple.backend_spring.Security.Controller;


import com.exemple.backend_spring.Security.Entity.Role;
import com.exemple.backend_spring.Security.Entity.User;
import com.exemple.backend_spring.Security.Service.IServiceAuth.IServiceAuth;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(path = "/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IServiceAuth iServiceAuth;

    @PostMapping("/addUser")
    public ResponseEntity<User> addUser(@RequestBody User appUser) {
        User createdUser = iServiceAuth.createAppUser(appUser);
        return ResponseEntity.ok(createdUser);
    }

    // Action pour ajouter un nouveau rôle
    @PostMapping("/addRole")
    public ResponseEntity<Role> addRole(@RequestBody Role appRole) {
        Role createdRole = iServiceAuth.createAppRole(appRole);
        return ResponseEntity.ok(createdRole);
    }

    // Action pour ajouter un rôle existant à un utilisateur existant
    @PostMapping("/addRoleToUser")
    public ResponseEntity<String> assignRoleToUser(@RequestParam String username, @RequestParam String roleName) {
        iServiceAuth.addRoleToUser(username, roleName);
        return ResponseEntity.ok("Role " + roleName + " assigned to user " + username);
    }

}
