package com.book.igo.common.security;

import com.book.igo.common.jwt.JwtTokenProvider;
import com.book.igo.common.jwt.TokenException;
import com.book.igo.user.domain.entity.UserRole;
import com.book.igo.user.domain.repository.TokenRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final TokenRepository tokenRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        // Authorization 헤더가 없으면 그냥 다음 필터로 넘김 (익명 요청)
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }

        // 블랙리스트 체크
        if (tokenRepository.isAccessTokenBlacklisted(authorizationHeader)) {
            log.debug("Blacklisted access token: {}", authorizationHeader);
            // 블랙리스트 토큰은 인증 없이 다음 필터로 진행 (결국 401/403)
            // 인증 없이 통과 → SecurityContext 비어 있음
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // JwtTokenProvider 안에서 Bearer 제거 + 검증 + 예외 변환까지 수행
            Long userId = jwtTokenProvider.getUserIdByAccessToken(authorizationHeader);
            String email = jwtTokenProvider.getEmailByAccessToken(authorizationHeader);
            String nickName = jwtTokenProvider.getNickNameByAccessToken(authorizationHeader);
            String roleName = jwtTokenProvider.getUserRoleByAccessToken(authorizationHeader); // "USER"
            UserRole role = UserRole.valueOf(roleName);

            JwtUserPrincipal principal = new JwtUserPrincipal(userId, email, nickName, role);

            // Spring Security 권한 규칙: "ROLE_" prefix
            SimpleGrantedAuthority authority =
                    new SimpleGrantedAuthority("ROLE_" + role.name());

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            principal,
                            null,
                            List.of(authority)
                    );

            authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (TokenException ex) {
            log.debug("JWT authentication failed: {}", ex.getErrorCode(), ex);
        }

        filterChain.doFilter(request, response);
    }
}