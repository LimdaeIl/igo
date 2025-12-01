package com.book.igo.common.jwt;

import com.book.igo.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum JwtErrorCode implements ErrorCode {

    // 입력/형식
    NOT_FOUND_TOKEN(HttpStatus.UNAUTHORIZED, "JWT: 토큰을 찾을 수 없습니다."),            // null/blank
    INVALID_BEARER_TOKEN(HttpStatus.UNAUTHORIZED, "JWT: 유효하지 않은 토큰입니다."),
    MALFORMED_TOKEN(HttpStatus.BAD_REQUEST, "JWT: JWT 형식이 잘못되었습니다."),
    UNSUPPORTED_TOKEN(HttpStatus.BAD_REQUEST, "JWT: 지원하지 않는 토큰입니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "JWT: 리프레시 토큰을 찾을 수 없습니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "JWT: 유효하지 않은 리프레시 토큰입니다."),

    // 무결성/시간
    TAMPERED_TOKEN(HttpStatus.UNAUTHORIZED, "JWT: 서명이 위조되었거나 무결성이 손상되었습니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "JWT: 만료된 토큰입니다."),
    PREMATURE_TOKEN(HttpStatus.UNAUTHORIZED, "JWT: 아직 활성화되지 않은 토큰입니다."), // nbf/iat 이슈

    // 클레임/정책
    INVALID_CLAIMS(HttpStatus.UNAUTHORIZED, "JWT: 필수 클레임이 없거나 유효하지 않습니다."),
    BLACKLISTED_TOKEN(HttpStatus.UNAUTHORIZED, "JWT: 폐기된(블랙리스트) 토큰입니다.");

    private final HttpStatus status;
    private final String message;

    @Override
    public HttpStatus getHttpStatus() {
        return status;
    }

    @Override
    public String getMessageTemplate() {
        return message;
    }
}
