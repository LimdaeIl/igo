package com.book.igo.common.jwt;

import com.book.igo.user.domain.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.IncorrectClaimException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Jwts.SIG;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.MissingClaimException;
import io.jsonwebtoken.PrematureJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j(topic = "JwtTokenProvider")
@Getter
@Component
public class JwtTokenProvider {

    private final String issuer;
    private final long accessTokenValidityInMs;
    private final long refreshTokenValidityInMs;
    private final SecretKey accessTokenKey;
    private final SecretKey refreshTokenKey;

    private static final String PREFIX_BEARER = "Bearer ";
    private static final String CLAIM_USER_ROLE = "USER_ROLE";
    private static final long DEFAULT_CLOCK_SKEW_SECONDS = 120; // 2분 오차 허용

    public JwtTokenProvider(
            @Value("${jwt.issuer:igo-app}") String issuer,
            @Value("${jwt.secret.access}") String accessSecretBase64,
            @Value("${jwt.secret.refresh}") String refreshSecretBase64,
            @Value("${jwt.refresh-token-validity-ms:3600000}") long refreshTokenValidityInMs,
            @Value("${jwt.access-token-validity-ms:900000}") long accessTokenValidityInMs
    ) {
        this.issuer = issuer;
        this.accessTokenKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(accessSecretBase64));
        this.refreshTokenKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(refreshSecretBase64));
        this.refreshTokenValidityInMs = refreshTokenValidityInMs;
        this.accessTokenValidityInMs = accessTokenValidityInMs;
    }

    public String generateAccessToken(User user) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + accessTokenValidityInMs);

        return Jwts.builder()
                .header().type("JWT")
                .and()
                .subject(String.valueOf(user.getId()))
                .issuedAt(now)
                .expiration(exp)
                .claim("email", user.getEmail())
                .claim("nickName", user.getNickName())
                .claim(CLAIM_USER_ROLE, user.getRole().name())
                .id(UUID.randomUUID().toString())
                .signWith(accessTokenKey, SIG.HS256)
                .compact();
    }

    public String generateRefreshToken(User user) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + refreshTokenValidityInMs);

        return Jwts.builder()
                .header().type("JWT")
                .and()
                .subject(String.valueOf(user.getId()))
                .issuer(issuer)
                .issuedAt(now)
                .expiration(exp)
                .claim("type", "refresh")
                .claim(CLAIM_USER_ROLE, user.getRole().name())
                .id(UUID.randomUUID().toString())
                .signWith(refreshTokenKey, SIG.HS256)
                .compact();
    }

    public Long getUserIdByRefreshToken(String refreshToken) {
        Claims claims = parseRefreshTokenClaims(refreshToken);
        return Long.parseLong(claims.getSubject());
    }

    public Long getUserIdByAccessToken(String accessToken) {
        Claims claims = parseAccessTokenClaims(accessToken);
        return Long.parseLong(claims.getSubject());
    }

    public long getTtlMsByRefreshToken(String refreshToken) {
        Claims claims = parseRefreshTokenClaims(refreshToken);
        return Math.max(claims.getExpiration().getTime() - System.currentTimeMillis(), 0);
    }

    public long getTtlMsByAccessToken(String accessToken) {
        Claims claims = parseAccessTokenClaims(accessToken);
        return Math.max(claims.getExpiration().getTime() - System.currentTimeMillis(), 0);
    }

    public String getEmailByAccessToken(String accessToken) {
        Claims claims = parseAccessTokenClaims(accessToken);
        return claims.get("email", String.class);
    }

    public String getNickNameByAccessToken(String accessToken) {
        Claims claims = parseAccessTokenClaims(accessToken);
        return claims.get("nickName", String.class);
    }

    public String getUserRoleByAccessToken(String accessToken) {
        Claims claims = parseAccessTokenClaims(accessToken);
        return claims.get(CLAIM_USER_ROLE, String.class);
    }

    private Claims parseRefreshTokenClaims(String refreshToken) {
        return getClaims(refreshToken, refreshTokenKey);
    }

    private Claims parseAccessTokenClaims(String accessToken) {
        return getClaims(accessToken, accessTokenKey);
    }

    private Claims getClaims(String token, SecretKey key) {
        String stripped = stripBearer(token);

        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .clockSkewSeconds(DEFAULT_CLOCK_SKEW_SECONDS)
                    .build()
                    .parseSignedClaims(stripped)
                    .getPayload();

        } catch (ExpiredJwtException e) {
            throw new TokenException(JwtErrorCode.EXPIRED_TOKEN);
        } catch (PrematureJwtException e) {
            throw new TokenException(JwtErrorCode.PREMATURE_TOKEN);
        } catch (UnsupportedJwtException e) {
            throw new TokenException(JwtErrorCode.UNSUPPORTED_TOKEN);
        } catch (MalformedJwtException e) {                 // 구조/인코딩 손상
            throw new TokenException(JwtErrorCode.MALFORMED_TOKEN);
        } catch (SecurityException | SignatureException e) {// 서명 위조/키 불일치
            throw new TokenException(JwtErrorCode.TAMPERED_TOKEN);
        } catch (MissingClaimException |    // 필수 클레임 누락
                 IncorrectClaimException e) {
            throw new TokenException(JwtErrorCode.INVALID_CLAIMS);
        } catch (JwtException e) {
            log.debug("JWT parse error: {}", e.toString());
            throw new TokenException(JwtErrorCode.INVALID_BEARER_TOKEN);
        }
    }

    private String stripBearer(String token) {
        if (token == null || token.isBlank()) {
            throw new TokenException(JwtErrorCode.NOT_FOUND_TOKEN);
        }

        String t = token.trim();
        if (t.regionMatches(true, 0, PREFIX_BEARER, 0, PREFIX_BEARER.length())) {
            t = t.substring(PREFIX_BEARER.length()).trim();
            if (t.isEmpty()) {
                throw new TokenException(JwtErrorCode.NOT_FOUND_TOKEN);
            }
        }
        return t;
    }
}
