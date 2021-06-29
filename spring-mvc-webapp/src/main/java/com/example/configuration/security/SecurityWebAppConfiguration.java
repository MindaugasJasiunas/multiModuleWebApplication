package com.example.configuration.security;

import com.example.demo.service.authentication.JpaUserDetailsService;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

@Configuration
@EnableWebSecurity
public class SecurityWebAppConfiguration extends WebSecurityConfigurerAdapter {
    private final JpaUserDetailsService jpaUserDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final PersistentTokenRepository persistentTokenRepository;

    public SecurityWebAppConfiguration(JpaUserDetailsService jpaUserDetailsService, PasswordEncoder passwordEncoder, PersistentTokenRepository persistentTokenRepository) {
        this.jpaUserDetailsService = jpaUserDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.persistentTokenRepository = persistentTokenRepository;
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
                .antMatchers("/login").permitAll()
                .antMatchers("/register").permitAll()
                .antMatchers("/forgot").permitAll()
                .antMatchers("/verify/**").permitAll()  // link to confirm email/change password

                .antMatchers("/cart", "/addToCart/*", "/checkout").hasRole("CUSTOMER")

                .antMatchers("/admin/**").hasRole("ADMIN")

                .anyRequest().authenticated() //should be after all matchers
                .and()
//                .httpBasic();
                .formLogin(loginConfigurer->{               //override default '/login' page
                    loginConfigurer
                            .loginProcessingUrl("/login")    //login processing URL
                            .loginPage("/login").permitAll() //login form URL
                            .successForwardUrl("/")          //redirected after success login to
                            .defaultSuccessUrl("/")          //URL added when login succeeded (can be "?success")
                            .failureUrl("/login?error")      //redirected when login fails
                            .usernameParameter("username")
                            .passwordParameter("password");  //id & name in form's*/
                })
                .rememberMe() // When logged with remember-me - new entry persisted to DB
                    .tokenRepository(persistentTokenRepository)
                    .userDetailsService(jpaUserDetailsService)
                .and()
                .logout(logoutConfigurer-> {
                    logoutConfigurer
                            .logoutUrl("/logout").permitAll()
                            .clearAuthentication(true)
                            .invalidateHttpSession(true)
                            .deleteCookies("JSESSIONID", "remember-me")
                            .logoutSuccessUrl("/login?logout");
                });
    }
}
