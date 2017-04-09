package com.github.WOWHans.security.auth.jwt.extractor;

/**
 * Created by Hans on 2017/4/9.
 */
public interface TokenExtractor {
    String extract(String paylod);
}
