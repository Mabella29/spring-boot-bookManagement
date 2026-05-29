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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;


@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthFilter jwtAuthFilter;
    private final CorsProperties corsProperties;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOrigins(corsProperties.getAllowedOrigins());
                    config.setAllowedMethods(corsProperties.getAllowedMethods());
                    config.setAllowedHeaders(corsProperties.getAllowedHeaders());
                    config.setExposedHeaders(corsProperties.getExposedHeaders());
                    config.setAllowCredentials(corsProperties.isAllowCredentials());
                    config.setMaxAge(corsProperties.getMaxAge());
                    return config;
                }))
                .csrf(csrf -> csrf.disable())
                .httpBasic(httpBasic -> httpBasic.disable())
                .formLogin(formLogin -> formLogin.disable())
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("login", "register").permitAll()
                        .pathMatchers("/api/v1/users/login", "/api/v1/users/register").permitAll()
                        .pathMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/webjars/**"
                        ).permitAll()

                       //rbac
                        .pathMatchers(HttpMethod.DELETE, "/api/v1/books/**").hasAuthority("ADMIN")
                        .pathMatchers(HttpMethod.PUT, "/api/v1/books/**").hasAuthority("ADMIN")

                        .pathMatchers(HttpMethod.POST, "/api/v1/books").hasAuthority("ADMIN")
                        .pathMatchers(HttpMethod.GET, "/api/v1/books/search").permitAll()
                        .pathMatchers(HttpMethod.GET, "/api/v1/books/**").permitAll()
                        .pathMatchers("/api/v1/cart/**").hasAnyAuthority("ADMIN", "USER")
                        .pathMatchers(HttpMethod.PUT,"/api/v1/orders/{orderId}/status").hasAnyAuthority("ADMIN")
                        .pathMatchers(HttpMethod.GET,"/api/v1/orders/all").hasAuthority("ADMIN")
                        .pathMatchers("/api/v1/orders/**").hasAnyAuthority("ADMIN","USER")

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
