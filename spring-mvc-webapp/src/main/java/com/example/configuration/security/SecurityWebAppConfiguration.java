package com.example.configuration.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;

@Configuration
@EnableWebSecurity
public class SecurityWebAppConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception{
        //add user for in-memory authentication
        User.UserBuilder users=User.withDefaultPasswordEncoder(); //NOT FOR PRODUCTION !

        auth.inMemoryAuthentication()
                .withUser(users.username("admin").password("pass").roles("ADMIN"));
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/").permitAll()
                .antMatchers("/","index","/css/*","/js/*", "/resources/*", "/webjars/*").permitAll()
//                .anyRequest().authenticated() //should be after all matchers
                .and()
                .httpBasic();
    }
}
