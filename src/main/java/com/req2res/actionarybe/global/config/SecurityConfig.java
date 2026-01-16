package com.req2res.actionarybe.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.req2res.actionarybe.global.security.JwtAuthenticationFilter;
import com.req2res.actionarybe.global.security.JwtTokenProvider;

import lombok.RequiredArgsConstructor;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtTokenProvider tokenProvider;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
			.csrf(csrf -> csrf.disable())
			.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests(auth -> auth
				.requestMatchers("/ws/**")
				.permitAll()
				.requestMatchers("/api/auth/login", "/api/auth/signup", "/api/auth/refresh",
					"/swagger", "/swagger-ui.html", "/swagger-ui/**",
					"/api-docs", "/api-docs/**", "/v3/api-docs/**")
				.permitAll()
                .requestMatchers(HttpMethod.GET, "/api/members/me/info")
                .authenticated()
                .requestMatchers(HttpMethod.GET, "/api/members/**")
                .permitAll()
                .requestMatchers(HttpMethod.GET, "/api/posts/**")
                .permitAll()
				.requestMatchers(HttpMethod.GET, "/api/search/**")
				.permitAll()
				.requestMatchers(HttpMethod.GET, "/api/studies/my", "/api/studies/*/participating/**")
				.authenticated()
				.requestMatchers(HttpMethod.GET, "/api/studies/**", "/api/users/*/points")
				.permitAll()
					.requestMatchers(HttpMethod.GET, "/api/ai-summary/**").permitAll()
					.anyRequest().authenticated()
			)
			.addFilterBefore(new JwtAuthenticationFilter(tokenProvider),
				UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(
                "http://13.209.205.33:5173",
                "http://localhost:5173"
        )); // 프론트 주소
        config.setAllowedMethods(List.of("*"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
		return cfg.getAuthenticationManager();
	}
}

