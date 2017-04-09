package com.github.WOWHans.security.auth.jwt;

import com.github.WOWHans.config.JwtSettings;
import com.github.WOWHans.model.UserContext;
import com.github.WOWHans.model.token.RawAccessJwtToken;
import com.github.WOWHans.security.auth.JwtAuthenticationToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Hans on 2017/4/9.
 */
@Component
public class JwtAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private JwtSettings jwtSettings;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        RawAccessJwtToken rawAccessJwtToken = (RawAccessJwtToken) authentication.getCredentials();
        Jws<Claims> claimsJws = rawAccessJwtToken.parseClaims(jwtSettings.getTokenSigningKey());
        String subject = claimsJws.getBody().getSubject();
        List<String> scopes = claimsJws.getBody().get("scopes",List.class);
        List<GrantedAuthority> authorities = scopes
                .stream()
                .map(s -> new SimpleGrantedAuthority(s))
                .collect(Collectors.toList());

        UserContext userContext = UserContext.create(subject,authorities);
        return new JwtAuthenticationToken(userContext.getAuthorities(),userContext);
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return JwtAuthenticationToken.class.isAssignableFrom(aClass);
    }
}
