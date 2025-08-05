package com.example.udtbe.global.config;

import static com.example.udtbe.domain.member.entity.enums.Role.ROLE_ADMIN;
import static com.example.udtbe.domain.member.entity.enums.Role.ROLE_GUEST;
import static com.example.udtbe.domain.member.entity.enums.Role.ROLE_USER;

import com.example.udtbe.global.security.handler.CustomAccessDeniedHandler;
import com.example.udtbe.global.security.handler.CustomAuthenticationEntryPoint;
import com.example.udtbe.global.security.handler.CustomOauth2FailureHandler;
import com.example.udtbe.global.security.handler.CustomOauth2SuccessHandler;
import com.example.udtbe.global.security.service.CustomOauth2UserService;
import com.example.udtbe.global.token.filter.TokenAuthenticationFilter;
import com.example.udtbe.global.token.filter.TokenExceptionFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOauth2UserService customOauth2UserService;
    private final CustomOauth2SuccessHandler customOauth2SuccessHandler;
    private final CustomOauth2FailureHandler customOauth2FailureHandler;
    private final TokenAuthenticationFilter tokenAuthenticationFilter;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final ObjectMapper objectMapper;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf((auth) -> auth.disable())
                .formLogin((auth) -> auth.disable())
                .httpBasic((auth) -> auth.disable())
                .sessionManagement(
                        (session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        http
                .authorizeHttpRequests(
                        (auth) -> auth
                                .requestMatchers("/",
                                        "/api/auth/reissue/token",
                                        "/api/admin/signin").permitAll()
                                .requestMatchers("/api/survey").hasAnyAuthority(ROLE_GUEST.name())
                                .requestMatchers("/api/admin/**").hasAnyAuthority(ROLE_ADMIN.name())
                                .requestMatchers("/api/**")
                                .hasAnyAuthority(ROLE_USER.name(), ROLE_ADMIN.name())
                                .anyRequest().authenticated()
                )
                .oauth2Login(
                        (oauth2) -> oauth2
                                .userInfoEndpoint(
                                        (userInfoEndpointConfig -> userInfoEndpointConfig.userService(
                                                customOauth2UserService))
                                )
                                .successHandler(customOauth2SuccessHandler)
                                .failureHandler(customOauth2FailureHandler)
                )

                .addFilterBefore(tokenAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new TokenExceptionFilter(objectMapper),
                        tokenAuthenticationFilter.getClass())

                .exceptionHandling((exception) -> exception
                        .authenticationEntryPoint(new CustomAuthenticationEntryPoint(objectMapper))
                        .accessDeniedHandler(customAccessDeniedHandler)
                );

        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring()
                .requestMatchers(
                        "/error", "/favicon.ico", "/api/auth/temp-signup",
                        "/api/auth/temp-signin", "/swagger-ui/**", "/v3/api-docs/**",
                        "/swagger-ui.html", "/actuator/health", "/actuator/prometheus",
                        "/actuator/metric"
                );
    }

    // Spring Security cors Bean 등록
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(
                Arrays.asList(
                        "https://www.banditbool.com",
                        "https://banditbool.com",
                        "https://dev.banditbool.com",
                        "http://localhost:3000",
                        "https://localhost:3000",
                        "http://localhost:8080",
                        "https://local.banditbool.com:3000",
                        "http://3.34.143.98",
                        "https://3.34.143.98"
                ));
        configuration.setAllowedMethods(
                Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setExposedHeaders(Arrays.asList("Set-Cookie", "Authorization"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3000L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
