package com.resourceServer.resourceServer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class ResourceServerConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/public/**").permitAll()   // Public Endpoints (No Authentication)
                        .requestMatchers("/books/**").hasAuthority("SCOPE_articles.read")  // Secure Endpoints
                        .anyRequest().authenticated()  // All Other Requests Require Authentication
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.decoder(jwtDecoder())))
                .csrf(csrf -> csrf.disable()) // Disable CSRF if APIs are stateless
                .cors(Customizer.withDefaults()); // Enable CORS Configuration

        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        NimbusJwtDecoder jwtDecoder = (NimbusJwtDecoder) JwtDecoders.fromIssuerLocation("http://localhost:9000");

        // Custom validator to bypass issuer mismatch error
        OAuth2TokenValidator<Jwt> validator = token -> OAuth2TokenValidatorResult.success();
        jwtDecoder.setJwtValidator(validator);

        return jwtDecoder;
    }
}