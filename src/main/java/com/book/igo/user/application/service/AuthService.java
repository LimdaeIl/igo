package com.book.igo.user.application.service;

import com.book.igo.common.jwt.JwtErrorCode;
import com.book.igo.common.jwt.JwtTokenProvider;
import com.book.igo.common.jwt.TokenException;
import com.book.igo.user.application.dto.request.SignInRequest;
import com.book.igo.user.application.dto.request.SignUpRequest;
import com.book.igo.user.application.dto.response.SignInResponse;
import com.book.igo.user.application.dto.response.SignUpResponse;
import com.book.igo.user.domain.entity.User;
import com.book.igo.user.domain.repository.TokenRepository;
import com.book.igo.user.domain.repository.UserRepository;
import com.book.igo.user.infrastructure.exception.UserErrorCode;
import com.book.igo.user.infrastructure.exception.UserException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public SignUpResponse signUp(SignUpRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new UserException(UserErrorCode.EMAIL_DUPLICATED);
        }

        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .nickName(request.nickname())
                .build();

        User savedUser = userRepository.save(user);

        return SignUpResponse.from(savedUser);
    }


    @Transactional
    public SignInResponse signIn(SignInRequest request) {

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UserException(UserErrorCode.EMAIL_NOT_FOUND));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new UserException(UserErrorCode.PASSWORD_INCORRECT);
        }

        String accessToken = jwtTokenProvider.generateAccessToken(user);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user);

        long refreshTtlMs = jwtTokenProvider.getTtlMsByRefreshToken(refreshToken);

        tokenRepository.saveRefreshToken(
                user.getId(),
                refreshToken,
                refreshTtlMs
        );

        return SignInResponse.of(user, accessToken, refreshToken);
    }

    @Transactional
    public SignInResponse reissue(String accessTokenHeader, String refreshToken) {

        // 1. RT 파싱 및 검증 (서명+만료)
        Long userId = jwtTokenProvider.getUserIdByRefreshToken(refreshToken);

        // 1-1. 이미 블랙리스트에 올라간 RT인지 체크 (재사용 시도 방지)
        if (tokenRepository.isRefreshTokenBlacklisted(refreshToken)) {
            // 재사용 공격으로 판단해도 되고, 그냥 INVALID_REFRESH_TOKEN으로 처리해도 됨
            throw new TokenException(JwtErrorCode.INVALID_REFRESH_TOKEN);
        }

        // 2. Redis에 저장된 RT와 일치 여부 체크
        String storedRefresh = tokenRepository.findRefreshToken(userId);
        if (storedRefresh == null) {
            throw new TokenException(JwtErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }
        if (!storedRefresh.equals(refreshToken)) {
            throw new TokenException(JwtErrorCode.INVALID_REFRESH_TOKEN);
        }

        // 3. 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        // 4. 사용한 RT는 남은 TTL 만큼 블랙리스트 등록 (JWT Rotation 핵심)
        long refreshTtl = jwtTokenProvider.getTtlMsByRefreshToken(refreshToken);
        if (refreshTtl > 0) {
            tokenRepository.blacklistRefreshToken(refreshToken, refreshTtl);
        }

        // 5. AT가 헤더에 있으면, 남은 TTL 만큼 블랙리스트에 등록
        if (accessTokenHeader != null && !accessTokenHeader.isBlank()) {

            long accessTtl = jwtTokenProvider.getTtlMsByAccessToken(accessTokenHeader);
            if (accessTtl > 0) {
                tokenRepository.blacklistAccessToken(accessTokenHeader, accessTtl);
            }
        }

        // 6. 새 AT/RT 발급 (Rotation)
        String newAccessToken = jwtTokenProvider.generateAccessToken(user);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(user);
        long newRefreshTtl = jwtTokenProvider.getTtlMsByRefreshToken(newRefreshToken);

        // 7. 새 RT를 Redis에 저장 (사용 가능한 RT는 항상 '한 개'만 유지)
        tokenRepository.saveRefreshToken(userId, newRefreshToken, newRefreshTtl);

        // 8. 새 토큰들 응답
        return SignInResponse.of(user, newAccessToken, newRefreshToken);
    }

    @Transactional
    public void logout(String accessTokenHeader, String refreshToken) {
        // 1. 액세스 토큰은 남은 TTL 만큼 블랙리스트 (선택)
        if (accessTokenHeader != null && !accessTokenHeader.isBlank()) {
            // "Bearer " 제거가 필요하면 여기서 한 번 정리해 주세요
            long accessTtl = jwtTokenProvider.getTtlMsByAccessToken(accessTokenHeader);
            if (accessTtl > 0) {
                tokenRepository.blacklistAccessToken(accessTokenHeader, accessTtl);
            }
        }

        // 2. 리프레시 토큰 처리 (필수)
        if (refreshToken == null || refreshToken.isBlank()) {
            return;
        }

        Long userId = jwtTokenProvider.getUserIdByRefreshToken(refreshToken);

        String stored = tokenRepository.findRefreshToken(userId);
        if (stored != null && !stored.equals(refreshToken)) {
            log.warn("Logout: stored refresh token and request token mismatch. userId={}, stored={}, request={}",
                    userId, stored.substring(0, 20), refreshToken.substring(0, 20));
        }
        tokenRepository.deleteRefreshToken(userId);

        long refreshTtl = jwtTokenProvider.getTtlMsByRefreshToken(refreshToken);
        if (refreshTtl > 0) {
            tokenRepository.blacklistRefreshToken(refreshToken, refreshTtl);
        }
    }
}
