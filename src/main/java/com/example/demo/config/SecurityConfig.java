package com.example.demo.config;

import com.example.demo.authentication.CustomJwtFilter;
import com.example.demo.user.CustomJdbcUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomJdbcUserDetailsService customJdbcUserDetailsService;

    private final CustomJwtFilter customJwtFilter;

    @Autowired
    public SecurityConfig(
            CustomJdbcUserDetailsService customJdbcUserDetailsService,
            CustomJwtFilter customJwtFilter
    ) {
        this.customJdbcUserDetailsService = customJdbcUserDetailsService;
        this.customJwtFilter = customJwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .headers((header) -> header.frameOptions((HeadersConfigurer.FrameOptionsConfig::disable)))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("api/user/create").permitAll()
                        .requestMatchers("api/auth/login").permitAll()
                        .requestMatchers("api/**").hasAnyRole("USER", "ADMIN")
                        .anyRequest().permitAll()
                )
                .addFilterBefore(customJwtFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(customJdbcUserDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());

        return new ProviderManager(authenticationProvider);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
