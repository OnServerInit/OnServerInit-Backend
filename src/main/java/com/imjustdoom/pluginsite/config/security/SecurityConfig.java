package com.imjustdoom.pluginsite.config.security;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final DaoAuthenticationProvider authProvider;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(this.authProvider);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable()

            .authorizeRequests()
            .antMatchers("/admin", "/admin/roles").hasRole("ADMIN")
            .antMatchers("/resources/create", "/account/details").authenticated()
            .antMatchers("/register", "/login").not().authenticated()

            .anyRequest().permitAll()

            .and()
            .formLogin().loginProcessingUrl("/login");
    }
}