package com.book_management.book.infrastructure.config;

import com.book_management.book.infrastructure.filter.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.http.HttpMethod;



@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(csrf -> csrf.disable())
                .httpBasic(httpBasic -> httpBasic.disable())
                .formLogin(formLogin -> formLogin.disable())
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("login", "register").permitAll()
                        .pathMatchers("/api/v1/users/login", "/api/v1/users/register").permitAll()

                       //rbac
                        .pathMatchers(HttpMethod.DELETE, "/api/v1/books/**").hasAuthority("ADMIN")
                        .pathMatchers(HttpMethod.PUT, "/api/v1/books/**").hasAuthority("ADMIN")

                        .pathMatchers(HttpMethod.POST, "/api/v1/books").hasAuthority("ADMIN")
                        .pathMatchers(HttpMethod.POST, "/api/v1/books/search").hasAnyAuthority("ADMIN","USER")
                        .pathMatchers(HttpMethod.GET, "/api/v1/books/**").hasAnyAuthority("ADMIN", "USER")
                        .pathMatchers("/api/v1/cart/**").hasAnyAuthority("ADMIN", "USER")

                        .anyExchange().authenticated()
                )
                .addFilterAt(jwtAuthFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }


    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
