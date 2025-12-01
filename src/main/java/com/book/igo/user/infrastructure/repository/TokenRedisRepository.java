package com.book.igo.user.infrastructure.repository;


import com.book.igo.user.domain.repository.TokenRepository;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class TokenRedisRepository implements TokenRepository {

    private final StringRedisTemplate template;

    private String buildKey(Long userId) {
        return "refreshToken:" + userId;
    }

    private String blacklistKey(String accessToken) {
        return "auth:blacklist:access:" + accessToken;
    }

    private String blacklistRefreshKey(String refreshToken) {
        return "auth:blacklist:refresh:" + refreshToken;
    }

    @Override
    public void saveRefreshToken(Long userId, String refreshToken, long ttlMillis) {
        String key = buildKey(userId);
        template.opsForValue().set(key, refreshToken, Duration.ofMillis(ttlMillis));
    }

    @Override
    public String findRefreshToken(Long userId) {
        return template.opsForValue().get(buildKey(userId));
    }

    @Override
    public void deleteRefreshToken(Long userId) {
        template.delete(buildKey(userId));
    }


    @Override
    public void blacklistAccessToken(String accessToken, long ttlMillis) {
        template.opsForValue()
                .set(blacklistKey(accessToken), "blacklisted", Duration.ofMillis(ttlMillis));
    }

    @Override
    public boolean isAccessTokenBlacklisted(String accessToken) {
        return template.hasKey(blacklistKey(accessToken));
    }

    @Override
    public void blacklistRefreshToken(String refreshToken, long ttlMillis) {
        template.opsForValue()
                .set(blacklistRefreshKey(refreshToken), "blacklisted", Duration.ofMillis(ttlMillis));
    }

    @Override
    public boolean isRefreshTokenBlacklisted(String refreshToken) {
        return template.hasKey(blacklistRefreshKey(refreshToken));
    }
}