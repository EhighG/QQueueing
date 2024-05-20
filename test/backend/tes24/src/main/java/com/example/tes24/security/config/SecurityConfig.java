package com.example.tes24.security.config;

import com.example.tes24.properties.AllowedUrlProperties;
import com.example.tes24.security.filter.AnonymousFilter;
import com.example.tes24.security.filter.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.Assert;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.time.Duration;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final String[] allowedUrls;
    private final String[] allowedPatterns;
    private final String[] anonymous;

    public SecurityConfig(AllowedUrlProperties allowedUrlProperties) {
        Assert.notNull(allowedUrlProperties, "allowedUrlProperties must not be null");
        Assert.notNull(allowedUrlProperties.getUrls(), "urls must not be null");
        Assert.notNull(allowedUrlProperties.getPatterns(), "patterns must not be null");
        Assert.notNull(allowedUrlProperties.getAnonymous(), "anonymous must not be null");

        this.allowedUrls = allowedUrlProperties.getUrls().toArray(new String[0]);
        this.allowedPatterns = allowedUrlProperties.getPatterns().toArray(new String[0]);
        this.anonymous = allowedUrlProperties.getAnonymous().toArray(new String[0]);
    }


    @Bean
    public CorsConfiguration corsConfiguration() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
//        corsConfiguration.setAllowedOrigins(List.of("http://localhost:3000", "http://70.12.245.30:3000"));
//        corsConfiguration.setAllowedMethods(List.of("OPTIONS", "GET", "POST", "PATCH", "PUT", "DELETE"));
        corsConfiguration.setAllowedOrigins(Arrays.asList("*"));
        corsConfiguration.setAllowedMethods(Arrays.asList("OPTIONS", "GET", "POST", "PATCH", "PUT", "DELETE"));
        corsConfiguration.setAllowedHeaders(Arrays.asList("*"));
        corsConfiguration.setMaxAge(Duration.ofMinutes(30));
        return corsConfiguration;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(CorsConfiguration corsConfiguration) {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationProvider... authenticationProvider) {
        return new ProviderManager(authenticationProvider);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity httpSecurity,
            CorsConfigurationSource corsConfigurationSource,
            AuthenticationEntryPoint loginEntryPoint,
            AnonymousFilter anonymousFilter,
            JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {

        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(anonymousFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAt(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers(allowedUrls).permitAll()
                        .requestMatchers(allowedPatterns).permitAll()
                        .requestMatchers(anonymous).anonymous()
                        .anyRequest().authenticated())
//                .exceptionHandling(customizer -> customizer.authenticationEntryPoint(loginEntryPoint))
                .build();
    }
}
