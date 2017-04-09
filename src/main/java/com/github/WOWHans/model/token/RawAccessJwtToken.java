package com.github.WOWHans.model.token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

/**
 * Created by Hans on 2017/4/9.
 */
public class RawAccessJwtToken implements JwtToken {

    private String token;

    public RawAccessJwtToken(String token) {
        this.token = token;
    }

    public Jws<Claims> parseClaims(String signingKey) {
        return Jwts.parser().setSigningKey(signingKey).parseClaimsJws(token);
    }

    @Override
    public String getToken() {
        return null;
    }
}
