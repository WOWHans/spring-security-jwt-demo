package com.github.WOWHans.security.auth.jwt;

import io.jsonwebtoken.lang.Assert;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Hans on 2017/4/9.
 */
public class SkipPathRequestMatcher implements RequestMatcher {
    private OrRequestMatcher orRequestMatcher;
    private RequestMatcher requestMatcher;

    public SkipPathRequestMatcher(List<String> routeToSkip, String processingRoute) {
        Assert.notNull(routeToSkip, "暂无要允许通过的路由请求");
        List<RequestMatcher> matchers = routeToSkip.stream()
                .map(r -> new AntPathRequestMatcher(r))
                .collect(Collectors.toList());
        orRequestMatcher = new OrRequestMatcher(matchers);
        requestMatcher = new AntPathRequestMatcher(processingRoute);

    }

    @Override
    public boolean matches(HttpServletRequest request) {
        if (orRequestMatcher.matches(request)) {
            return false;
        }
        return requestMatcher.matches(request) ? true : false;
    }
}
