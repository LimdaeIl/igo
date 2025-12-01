package com.book.igo.user.infrastructure.exception;

import com.book.igo.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {
    USER_INVALID(HttpStatus.BAD_REQUEST, "회원: 잘못된 회원 정보입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "회원: 회원 정보를 찾을 수 없습니다."),
    USER_DELETED(HttpStatus.BAD_REQUEST, "회원: 해당 회원은 삭제되었습니다."),
    RT_NOT_FOUND(HttpStatus.NOT_FOUND, "회원: 해당 RT 토큰은 찾을 수 없습니다."),
    RT_JTI_INCORRECT(HttpStatus.BAD_REQUEST, "회원: 저장된 RT 토큰의 JTI와 틀립니다."),
    RT_BLACKLIST(HttpStatus.FORBIDDEN, "회원: 해당 RT 토큰은 블랙리스트로 등록되어 있습니다."),
    AT_BLACKLIST(HttpStatus.FORBIDDEN, "회원: 해당 AT 토큰은 블랙리스트로 등록되어 있습니다."),

    PASSWORD_SAME_BEFORE(HttpStatus.BAD_REQUEST, "비밀번호: 이전 비밀번호와 동일한 비밀번호 입니다."),
    PASSWORD_INVALID(HttpStatus.BAD_REQUEST, "비밀번호: 비밀번호 형식이 올바르지 않습니다."),
    PASSWORD_INCORRECT(HttpStatus.BAD_REQUEST, "비밀번호: 비밀번호가 틀립니다."),
    PASSWORD_NULL(HttpStatus.BAD_REQUEST, "비밀번호: 비밀번호 입력은 필수입니다."),

    EMAIL_DUPLICATED(HttpStatus.CONFLICT, "이메일: 이미 사용 중인 이메일입니다."),
    EMAIL_SAME_BEFORE(HttpStatus.CONFLICT, "이메일: 이전 이메일과 동일한 이메일 입니다."),
    EMAIL_INVALID(HttpStatus.BAD_REQUEST, "이메일: 이메일 형식이 올바르지 않습니다."),
    EMAIL_NULL(HttpStatus.BAD_REQUEST, "이메일: 이메일 입력은 필수 입니다."),
    EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "이메일: 이메일을 찾을 수 없습니다."),
    USER_IS_LOCKED(HttpStatus.FORBIDDEN, "이메일: 삭제된 회원의 이메일 입니다."),

    EMAIL_VERIFICATION_COOLTIME(HttpStatus.TOO_MANY_REQUESTS, "이메일 인증: 재요청 대기 시간입니다."),
    EMAIL_VERIFICATION_BLOCKED(HttpStatus.FORBIDDEN, "이메일 인증: 시도 횟수 초과로 잠시 차단되었습니다."),
    EMAIL_VERIFICATION_NOT_REQUESTED(HttpStatus.BAD_REQUEST, "이메일 인증: 먼저 인증 코드를 요청해 주세요."),
    EMAIL_VERIFY_CODE_MISMATCH(HttpStatus.BAD_REQUEST, "이메일 인증: 인증 코드가 일치하지 않습니다."),

    AGREEMENT_TERMS_OF_SERVICE(HttpStatus.BAD_REQUEST, "동의: 서비스 동의 승인은 필수 입니다."),
    AGREEMENT_PRIVACY(HttpStatus.BAD_REQUEST, "동의: 개인정보 동의 승인은 필수 입니다."),
    AGREEMENT_MARKETING(HttpStatus.BAD_REQUEST, "동의: 마케팅 활용 동의 승인은 필수 입니다."),

    ADDRESS_NOT_FOUND(HttpStatus.NOT_FOUND, "주소: 회원 주소를 찾을 수 없습니다."),
    ADDRESS_LAST_CANNOT_DELETE(HttpStatus.BAD_REQUEST, "주소: 회원 주소는 최소 1개 이상이어야만 합니다.");

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