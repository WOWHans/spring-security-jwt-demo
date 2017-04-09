package com.github.WOWHans.security;

import com.github.WOWHans.entity.User;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Created by Hans on 2017/4/8.
 */
public interface UserService {

    Optional<User> getByUserName(String username);
}
