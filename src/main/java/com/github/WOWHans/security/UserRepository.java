package com.github.WOWHans.security;

import com.github.WOWHans.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * Created by Hans on 2017/4/8.
 */
public interface UserRepository extends JpaRepository<User,Long> {

    @Query("select u from User u left join fetch u.roles r where u.username=:username")
    Optional<User> findByUsername(@Param("username") String username);
}
