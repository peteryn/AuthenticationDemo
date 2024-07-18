package org.example.authenticationdemo.services;

import org.example.authenticationdemo.models.User;
import org.example.authenticationdemo.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean registerUser(User user) {
        return userRepository.insert(user);
    }

    public boolean loginUser(User user) {
        return userRepository.checkUserCredentials(user);
    }
}
