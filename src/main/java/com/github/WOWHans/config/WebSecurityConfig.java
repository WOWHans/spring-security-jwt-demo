package com.github.WOWHans.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.WOWHans.RestAuthenticationEntryPoint;
import com.github.WOWHans.security.auth.ajax.AjaxAuthenticationProvider;
import com.github.WOWHans.security.auth.ajax.AjaxLoginProcessingFilter;
import com.github.WOWHans.security.auth.jwt.JwtAuthenticationProvider;
import com.github.WOWHans.security.auth.jwt.JwtTokenAuthenticationProcessingFilter;
import com.github.WOWHans.security.auth.jwt.SkipPathRequestMatcher;
import com.github.WOWHans.security.auth.jwt.extractor.TokenExtractor;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

/**
 * Created by Hans on 2017/4/9.
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    public static final String JWT_TOKEN_HEADER_PARAM = "X-Authorization";
    public static final String FORM_BASED_LOGIN_ENTRY_POINT = "/api/auth/login";
    public static final String TOKEN_BASED_AUTH_ENTRY_POINT = "/api/**";
    public static final String TOKEN_REFRESH_ENTRY_POINT = "/api/auth/token";

    @Autowired private RestAuthenticationEntryPoint authenticationEntryPoint;
    @Autowired private AuthenticationFailureHandler failureHandler;
    @Autowired private AuthenticationSuccessHandler successHandler;
    @Autowired private AjaxAuthenticationProvider ajaxAuthenticationProvider;
    @Autowired private JwtAuthenticationProvider jwtAuthenticationProvider;

    @Autowired private TokenExtractor tokenExtractor;
    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private ObjectMapper objectMapper;

    AjaxLoginProcessingFilter buildAjaxLoginProcessingFilter(){
        AjaxLoginProcessingFilter filter = new AjaxLoginProcessingFilter(FORM_BASED_LOGIN_ENTRY_POINT,successHandler,failureHandler,objectMapper);
        filter.setAuthenticationManager(this.authenticationManager);
        return filter;
    }

    JwtTokenAuthenticationProcessingFilter buildJwtTokenAuthenticationProcessingFilter() {
        List<String> routeToSkip = Lists.newArrayList(TOKEN_REFRESH_ENTRY_POINT,FORM_BASED_LOGIN_ENTRY_POINT);
        SkipPathRequestMatcher skipPathRequestMatcher = new SkipPathRequestMatcher(routeToSkip,TOKEN_BASED_AUTH_ENTRY_POINT);
        JwtTokenAuthenticationProcessingFilter filter = new JwtTokenAuthenticationProcessingFilter(failureHandler,tokenExtractor,skipPathRequestMatcher);
        filter.setAuthenticationManager(this.authenticationManager);
        return filter;
    }
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(ajaxAuthenticationProvider);
        auth.authenticationProvider(jwtAuthenticationProvider);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint)
                .and()
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                    .authorizeRequests()
                        .antMatchers(FORM_BASED_LOGIN_ENTRY_POINT).permitAll()
                        .antMatchers(TOKEN_REFRESH_ENTRY_POINT).permitAll()
                        .antMatchers("/console/**").permitAll()
                .and()
                    .authorizeRequests()
                        .antMatchers(TOKEN_BASED_AUTH_ENTRY_POINT).authenticated()
                .and()
                    .addFilterBefore(buildAjaxLoginProcessingFilter(), UsernamePasswordAuthenticationFilter.class)
                    .addFilterBefore(buildJwtTokenAuthenticationProcessingFilter(), UsernamePasswordAuthenticationFilter.class);

    }
}
