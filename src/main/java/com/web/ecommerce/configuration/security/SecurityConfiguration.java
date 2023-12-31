package com.web.ecommerce.configuration.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {


    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    public SecurityConfiguration(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .securityMatcher("/api/**")
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/auth/**")
                        .permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/orders/{orderId}")
                        .hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/orders")
                        .hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/products/**")
                        .permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/products/user-review/**")
                        .hasRole("USER")
                        .requestMatchers(HttpMethod.DELETE, "/api/products/**")
                        .hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/products/**")
                        .hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/products/{productId}/*")
                        .hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/products/**")
                        .hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/products/*/reviews")
                        .permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/carts/{id}")
                        .permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/carts/add-to-cart")
                        .permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/carts")
                        .permitAll()
                        .anyRequest()
                        .authenticated())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000",
                "http://127.0.0.1:3000",
                "http://89.134.215.228:3000",
                        "https://phommaeduhub.com",
                "https://ecommerce-frontend-henna-three.vercel.app"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


}
