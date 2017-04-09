package com.github.WOWHans.model;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

/**
 * Created by Hans on 2017/4/9.
 * 创建用户上下文，你需要一些你需要的用户数据来填充（例如 用户名 和用户密码）
 */
public class UserContext {
    private String username;
    private List<GrantedAuthority> authorities;

    public UserContext(String username, List<GrantedAuthority> authorities) {
        this.username = username;
        this.authorities = authorities;
    }

    public static UserContext create(String username, List<GrantedAuthority> authorities) {
        if (StringUtils.isBlank(username)) throw new IllegalArgumentException("用户名为空: " + username);
        return new UserContext(username, authorities);
    }

    public String getUsername() {
        return username;
    }

    public List<GrantedAuthority> getAuthorities() {
        return authorities;
    }
}
