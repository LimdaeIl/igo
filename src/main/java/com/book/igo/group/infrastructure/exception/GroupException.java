package com.book.igo.group.infrastructure.exception;

import com.book.igo.common.exception.AppException;
import com.book.igo.common.exception.ErrorCode;

public class GroupException extends AppException {

    public GroupException(ErrorCode errorCode, Object... args) {
        super(errorCode, args);
    }

    public GroupException(ErrorCode errorCode) {
        super(errorCode);
    }
}
