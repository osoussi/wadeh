package com.exemple.backend_spring.service;

import com.exemple.backend_spring.Security.Entity.User;
import com.exemple.backend_spring.Security.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User registerUser (User user) {
        return userRepository.save(user);
    }

    public List<User> selectAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> editUser (Long id, User userDetails) {
        return userRepository.findById(id).map(user -> {
            user.setUsername(userDetails.getUsername());
            user.setUserprofile(userDetails.getUserprofile());
            user.setPassword(userDetails.getPassword());
            return userRepository.save(user);
        });
    }

    public void deleteUser (Long id) {
        userRepository.deleteById(id);
    }

    public Optional<User> loginUser (String number, String password) {
        return userRepository.findAll().stream()
                .filter(user -> user.getUserprofile().equals(number) && user.getPassword().equals(password))
                .findFirst();
    }
}