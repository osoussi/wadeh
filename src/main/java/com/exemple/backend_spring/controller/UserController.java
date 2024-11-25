package com.exemple.backend_spring.controller;


import com.exemple.backend_spring.Security.Entity.Role;
import com.exemple.backend_spring.Security.Entity.User;
import com.exemple.backend_spring.Security.Repository.RoleRepository;
import com.exemple.backend_spring.dto.UserDTO;
import com.exemple.backend_spring.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users/")
@RequiredArgsConstructor
public class UserController {
    private final AuthenticationManager authenticationManager;
    private final JwtEncoder jwtEncoder;
    private final UserService userService;
    private final RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder=new BCryptPasswordEncoder();
    @PostMapping(path = "/register")
    public Map<String, String> registerUser (UserDTO userDTO) {

        // Create and set user properties
        User user = new User();
        user.setUsername(userDTO.getName());
        user.setUserprofile(userDTO.getUserprofile());
        // Encode the password before saving
        String encodedPassword = passwordEncoder.encode(userDTO.getPassword());
        user.setPassword(encodedPassword);

        // Check and assign role
        String roleName = user.getRoleList() != null ? String.valueOf(user.getRoleList()) : "USER";
        Optional<Role> roleOptional = roleRepository.findByRoleName(roleName);
        if (roleOptional.isEmpty()) {
            throw new IllegalArgumentException("Role not found");
        }
        Role role = roleOptional.get();
        user.setRoleList(Collections.singletonList(role));

        // Save the new user
        User savedUser = userService.registerUser(user);

        // Authenticate the newly registered user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
        );

        // Generate JWT token, similar to the login method
        Instant instant = Instant.now();
        String scope = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        JwtClaimsSet jwtClaimsSet = JwtClaimsSet
                .builder()
                .issuedAt(instant)
                .expiresAt(instant.plus(Duration.ofMinutes(60)))
                .subject(user.getUsername())
                .claim("scope", scope)
                .build();

        JwtEncoderParameters jwtEncoderParameters = JwtEncoderParameters.from(
                JwsHeader.with(MacAlgorithm.HS512).build(),
                jwtClaimsSet
        );

        String jwt = jwtEncoder.encode(jwtEncoderParameters).getTokenValue();

        // Return the access token
        return Map.of("access-token", jwt);
    }


    @GetMapping("/")
    public ResponseEntity<List<User>> selectAllUsers() {
        List<User> users = userService.selectAllUsers();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> editUser (@PathVariable Long id, @RequestBody UserDTO userDTO) {
        User user = new User();
        user.setUsername(userDTO.getName());
        user.setUserprofile(userDTO.getUserprofile());
        user.setPassword(userDTO.getPassword());

        Optional<User> updatedUser  = userService.editUser (id, user);
        return updatedUser .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser (@PathVariable Long id) {
        userService.deleteUser (id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    public ResponseEntity<User> loginUser (@RequestBody UserDTO userDTO) {
        Optional<User> user = userService.loginUser (userDTO.getUserprofile(), userDTO.getPassword());
        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(401).build()); // Unauthorized
    }
}
