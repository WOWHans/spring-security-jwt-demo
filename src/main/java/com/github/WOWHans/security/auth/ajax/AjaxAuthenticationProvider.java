package com.github.WOWHans.security.auth.ajax;

import com.github.WOWHans.entity.User;
import com.github.WOWHans.model.UserContext;
import com.github.WOWHans.security.UserService;
import com.github.WOWHans.security.UserServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Hans on 2017/4/8.
 *
 * 1. 对用户凭证与 数据库、LDAP或其他系统用户数据，进行验证。
 * 2. 如果用户名和密码不匹配数据库中的记录，身份验证异常将会被抛出。
 * 3. 创建用户上下文，你需要一些你需要的用户数据来填充（例如 用户名 和用户密码）
 * 4. 在成功验证委托创建JWT令牌的是在* AjaxAwareAuthenticationSuccessHandler 中实现
 */
@Component
public class AjaxAuthenticationProvider implements AuthenticationProvider {
    private static final Logger log = LoggerFactory.getLogger(AjaxAuthenticationProvider.class);
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Resource(name = "userService")
    private UserService userService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Assert.notNull(authentication, "没有提供凭据");

        String username = (String) authentication.getPrincipal();
        String password = (String) authentication.getCredentials();
        //1.对用户凭证与 数据库、LDAP或其他系统用户数据，进行验证。
        User user = userService.getByUserName(username).orElseThrow(() -> new UsernameNotFoundException("未发现用户名:" + username));
        log.debug("该用户名:{}的密码:{}",user.getUsername(),user.getPassword());
        //2.如果用户名和密码不匹配数据库中的记录，身份验证异常将会被抛出。
        if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("认证失败，用户名或密码无效");
        }

        if (user.getRoles() == null) throw new InsufficientAuthenticationException("该用户没有被赋予角色权限");


        List<GrantedAuthority> authorities = user.getRoles()
                .stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getRole().authority()))
                .collect(Collectors.toList());

        UserContext userContext = UserContext.create(username, authorities);
        return new UsernamePasswordAuthenticationToken(userContext, null, userContext.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
