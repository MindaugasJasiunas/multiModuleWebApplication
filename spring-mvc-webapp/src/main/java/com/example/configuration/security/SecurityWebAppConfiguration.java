package com.example.configuration.security;

import com.example.demo.service.authentication.JpaUserDetailsService;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityWebAppConfiguration extends WebSecurityConfigurerAdapter {
    private JpaUserDetailsService jpaUserDetailsService;
    private PasswordEncoder passwordEncoder;

    public SecurityWebAppConfiguration(JpaUserDetailsService jpaUserDetailsService, PasswordEncoder passwordEncoder) {
        this.jpaUserDetailsService = jpaUserDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(jpaUserDetailsService).passwordEncoder(passwordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/"/*,"index","/css/*","/js/*"*/).permitAll()
                .antMatchers("/resources/**", "/webjars/**").permitAll()
                .antMatchers("/api/v1/items").permitAll()  // REST endpoint call in main page for items
                .antMatchers("/item/**").permitAll()
                .antMatchers("/cart").permitAll()
                .antMatchers("/login").permitAll()
                .antMatchers("/register").permitAll()
                .antMatchers("/forgot").permitAll()

                .antMatchers("/checkout").hasRole("CUSTOMER")

                .antMatchers("/admin/**").hasRole("ADMIN")

                .anyRequest().authenticated() //should be after all matchers
                .and()
                .httpBasic();
    }
}
