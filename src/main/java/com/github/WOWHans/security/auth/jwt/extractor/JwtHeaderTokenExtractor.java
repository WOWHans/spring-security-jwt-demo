package com.github.WOWHans.security.auth.jwt.extractor;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Component;

import javax.naming.AuthenticationException;

/**
 * Created by Hans on 2017/4/9.
 */
@Component
public class JwtHeaderTokenExtractor implements TokenExtractor {
    public static String HEADER_PREFIX = "Bearer ";
    @Override
    public String extract(String paylod) {
        if (StringUtils.isBlank(paylod)) throw new AuthenticationServiceException("请求头部未包含Authorization字段");

        if (paylod.length() < HEADER_PREFIX.length()) throw new AuthenticationServiceException("请求认证头部无效");
        return paylod.substring(HEADER_PREFIX.length(),paylod.length());
    }
}
