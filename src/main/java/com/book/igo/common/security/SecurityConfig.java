package com.book.igo.common.security;


import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * HTTP 보안 설정의 핵심: SecurityFilterChain. - 세션: STATELESS (JWT 기반 API 스타일) - CSRF: 비활성화 (SPA/REST
     * API에 적합) - CORS: 별도 bean 설정과 연동 - URL별 인가 규칙 설정
     */
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationFilter jwtAuthenticationFilter
    ) throws Exception {

        http
                // REST API 스타일이므로 CSRF 비활성화
                .csrf(AbstractHttpConfigurer::disable)

                // CORS 설정 사용
                .cors(Customizer.withDefaults())

                // 세션을 STATELESS로 (서버가 세션/로그인 상태를 보관하지 않음)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 기본 폼 로그인, HTTP Basic 인증은 사용하지 않음 (우리는 JWT 사용)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)

                // URL 별 인가 규칙
                .authorizeHttpRequests(auth -> auth
                        // 인증 없이 누구나 접근 가능한 엔드포인트
                        .requestMatchers(
                                "/api/v1/auth/**",    // 회원가입, 로그인, 토큰 재발급 등
                                "/actuator/health",
                                "/actuator/prometheus"
                        ).permitAll()

                        // (선택) 정적 리소스, 에러 페이지 허용
                        .requestMatchers(
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/error"
                        ).permitAll()

                        // 그 외 나머지 요청은 모두 인증 필요
                        .anyRequest().authenticated()
                );

        // ⚠️ JwtAuthenticationFilter를 addFilterBefore(...)로 추가
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * CORS 설정. 프론트엔드 도메인(localhost:3000 등)에 맞게 Origin을 조정해서 사용하면 됩니다.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // TODO: 실제 프론트 주소에 맞게 수정 (예: http://localhost:3000, https://together-app.com 등)
        config.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:5173"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true); // 쿠키/인증 정보 포함 요청 허용 여부

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
