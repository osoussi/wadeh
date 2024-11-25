package com.exemple.backend_spring.dto;

import com.exemple.backend_spring.Security.Entity.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private String name;
    private String userprofile;
    private String password;

    private String role;

}