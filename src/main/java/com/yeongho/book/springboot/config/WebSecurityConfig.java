package com.yeongho.book.springboot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.cors().configurationSource(corsConfigurationSource())
                .and()
                .csrf().disable()
                .formLogin().disable();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        corsConfiguration.addAllowedOrigin("*");
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        /*
         * /* : 경로의 하위 경로만 매핑, ex] aaa/* : aaa/bbb, aaa/ccc 매핑 가능, But aaa/bbb/ccc or aaa/bbb/ddd 매핑 불가능
         * /** : 경로의 모든 하위 경로 매핑 ex] aaa/* : aaa/bbb, aaa/bbb/ccc, aaa/b/c/d 모두 가능
         */
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }
}

