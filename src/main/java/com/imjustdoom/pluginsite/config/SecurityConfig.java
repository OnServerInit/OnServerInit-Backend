package com.imjustdoom.pluginsite.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                // Disable default configurations
                .logout().disable()
                .httpBasic().disable()
                .formLogin().disable()

                // Disable session creation
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED).and()

                // Disable csrf (shouldn't need it as its just a backend api now)
                .csrf().disable()


//                .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint).and()

                // Permit all (use method security for controller access)
                .authorizeRequests().anyRequest().permitAll()
                .and()
                .rememberMe().key("uniqueAndSecret");
    }
}
