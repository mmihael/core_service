package dev.mm.core.coreservice.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static dev.mm.core.coreservice.constants.Uris.API_LOGIN;
import static dev.mm.core.coreservice.constants.Uris.API_LOGOUT;

@Configuration
public class SecurityConfiguration {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Configuration
    public static class BaseSecurity extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .exceptionHandling()
                    .authenticationEntryPoint((req, res, e) -> res.setStatus(HttpStatus.UNAUTHORIZED.value()))
                    .and()
                    .authorizeRequests()
                    .antMatchers("/", "/public/**", "/static/**", "/favicon.ico", "/manifest.json").permitAll()
                    .anyRequest().authenticated()
                    .and()
                    .headers().frameOptions().disable() // to enable h2 console
                    .and()
                    .formLogin()
                    .loginProcessingUrl(API_LOGIN)
                    .failureHandler((req, res, e) -> res.setStatus(HttpStatus.UNAUTHORIZED.value()))
                    .successHandler((req, res, e) -> res.setStatus(HttpStatus.ACCEPTED.value()))
                    .and()
                    .logout()
                    .logoutUrl(API_LOGOUT)
                    .logoutSuccessHandler((req, res, e) -> res.setStatus(HttpStatus.OK.value()))
                    .and()
                    .httpBasic().disable()
                    .csrf().disable();
        }
    }
}
