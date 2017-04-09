package com.github.WOWHans.security;

import com.github.WOWHans.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Created by Hans on 2017/4/8.
 */
@Service(value = "userService")
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public Optional<User> getByUserName(String username) {
        return userRepository.findByUsername(username);
    }
}
