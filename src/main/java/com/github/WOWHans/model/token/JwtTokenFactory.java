package com.github.WOWHans.model.token;

import com.github.WOWHans.config.JwtSettings;
import com.github.WOWHans.model.Scopes;
import com.github.WOWHans.model.UserContext;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by Hans on 2017/4/9.
 */
@Component
public class JwtTokenFactory {

    @Autowired
    private JwtSettings jwtSettings;

    public AccessJwtToken createAccessJwtToken(UserContext userContext) {
        if (StringUtils.isBlank(userContext.getUsername()))
            throw new IllegalArgumentException("无用户名无法创建JWT");
        if (CollectionUtils.isEmpty(userContext.getAuthorities()))
            throw new IllegalArgumentException("该用户没有被赋予权限");

        Claims claims = Jwts.claims()
                .setSubject(userContext.getUsername());
        claims.put("scopes",userContext.getAuthorities().stream().map(s -> s.toString()).collect(Collectors.toList()));

        Date currentDate = new Date();

        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuer(jwtSettings.getTokenIssuer())
                .setIssuedAt(currentDate)
                .setExpiration(DateUtils.addMinutes(currentDate,jwtSettings.getTokenExpirationTime()))
                .signWith(SignatureAlgorithm.HS512,jwtSettings.getTokenSigningKey())
                .compact();

        return new AccessJwtToken(token,claims);
    }

    public AccessJwtToken createRefreshToken(UserContext userContext) {
        if (StringUtils.isBlank(userContext.getUsername()))
            throw new IllegalArgumentException("无用户名无法创建JWT");

        Date currentDate = new Date();
        Claims claims = Jwts.claims()
                .setSubject(userContext.getUsername());
        claims.put("scopes", Arrays.asList(Scopes.REFRESH_TOKEN.authority()));

        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuer(jwtSettings.getTokenIssuer())
                .setIssuedAt(currentDate)
                .setId(UUID.randomUUID().toString())
                .setExpiration(DateUtils.addMinutes(currentDate,jwtSettings.getRefreshTokenExpTime()))
                .signWith(SignatureAlgorithm.HS512,jwtSettings.getTokenSigningKey())
                .compact();

        return new AccessJwtToken(token,claims);
    }
}
