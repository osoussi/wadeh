package com.exemple.backend_spring.Security.Config;

import com.exemple.backend_spring.Security.Entity.User;
import com.exemple.backend_spring.Security.Service.IServiceAuth.IServiceAuth;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/")
public class SecurityController {
    private final AuthenticationManager authenticationManager;
    private final JwtEncoder jwtEncoder;

    private final IServiceAuth iServiceAuth;

    @GetMapping(path = "/profile")
    public Authentication authentication(Authentication authentication){
        return authentication;
    }

    @PostMapping(path = "/login")
    public Map<String, String> login(String username, String password) {
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));

        Instant instant = Instant.now();
        String scope = authenticate.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        // Fetch user details (assuming a UserService)
        User user = iServiceAuth.LoadUserByUserName(username);

        JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                .issuedAt(instant)
                .expiresAt(instant.plus(Duration.ofMinutes(60)))
                .subject(username)
                .claim("scope", scope)
                .claim("userId", user.getId())  // Add userId to claims
                .build();

        JwtEncoderParameters jwtEncoderParameters = JwtEncoderParameters.from(
                JwsHeader.with(MacAlgorithm.HS512).build(),
                jwtClaimsSet);

        String jwt = jwtEncoder.encode(jwtEncoderParameters).getTokenValue();

        // Return the token and user information
        return Map.of(
                "access-token", jwt,
                "userId", String.valueOf(user.getId())
        );
    }


}
