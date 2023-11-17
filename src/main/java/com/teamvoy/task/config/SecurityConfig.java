package com.teamvoy.task.config;

import com.teamvoy.task.security.jwtToken.AuthEntryPoint;
import com.teamvoy.task.security.jwtToken.AuthTokenFilter;
import com.teamvoy.task.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Slf4j
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableScheduling
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    @Lazy
    private UserService userService;
    @Autowired
    private AuthEntryPoint authEntryPoint;

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/auth/*").permitAll()
                .anyRequest().authenticated()
                .and()
                .exceptionHandling().authenticationEntryPoint(authEntryPoint)
                .and()
                .addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.
                userDetailsService(userService)
                .passwordEncoder(passwordEncoder());
    }

    @Bean
    AuthenticationEntryPoint restAuthenticationEntryPoint() {
        return (request, response, authException) -> {
            log.warn("Authentication for '{} {}' failed with error: {}",
                    request.getMethod(), request.getRequestURL(), authException.getMessage());
            response.sendError(
                    UNAUTHORIZED.value(), authException.getMessage());
        };
    }
}
