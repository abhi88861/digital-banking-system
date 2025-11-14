package com.bank.digital_banking.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

/**
 * Keep this CorsFilter; SecurityConfig should also enable `.cors(cors -> {})`
 * so Spring Security lets this filter handle CORS preflight and headers.
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);

        // Allow any origin in dev; in production narrow this down
        config.addAllowedOriginPattern("*");

        // Allow headers including Authorization so browser requests can send Bearer token
        config.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type", "*"));
        config.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));

        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setMaxAge(3600L);

        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
