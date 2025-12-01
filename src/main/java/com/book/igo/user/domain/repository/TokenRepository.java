package com.book.igo.user.domain.repository;

public interface TokenRepository {

    void saveRefreshToken(Long userId, String refreshToken, long ttlMillis);

    String findRefreshToken(Long userId);

    void deleteRefreshToken(Long userId);

    void blacklistAccessToken(String accessToken, long ttlMillis);

    void blacklistRefreshToken(String refreshToken, long ttlMillis);

    boolean isAccessTokenBlacklisted(String accessToken);

    boolean isRefreshTokenBlacklisted(String refreshToken);

}
