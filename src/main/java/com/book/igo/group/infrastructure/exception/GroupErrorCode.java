package com.book.igo.group.infrastructure.exception;

import com.book.igo.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum GroupErrorCode implements ErrorCode {

    HOST_USER_NOT_FOUND(HttpStatus.NOT_FOUND, "모임: 호스트 사용자를 찾을 수 없습니다."),
    INVALID_TIME_RANGE(HttpStatus.BAD_REQUEST, "모임: 종료 시간은 시작 시간보다 뒤여야 합니다."),
    INVALID_MAX_PARTICIPANTS(HttpStatus.BAD_REQUEST, "모임: 최대 인원은 최소 2명 이상이어야 합니다."),
    TAG_NOT_FOUND(HttpStatus.NOT_FOUND, "모임: 요청한 태그를 찾을 수 없습니다.");

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
