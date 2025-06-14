package com.epam.hw.storage;

import com.epam.hw.entity.User;
import com.epam.hw.repository.UserRepository;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Getter
public class Auth {
    private User loggedInUser = null;

    private final UserRepository userRepository;

    @Autowired
    public Auth(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean logIn(String username, String password) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.getPassword().equals(password)) {
                if (user.getTrainee() != null) {
                    loggedInUser = user;
                    return true;
                }
            }
        }
        return false;
    }

    public boolean logOut() {
        if (loggedInUser != null) {
            loggedInUser = null;
            return true;
        }
        return false;
    }

}
