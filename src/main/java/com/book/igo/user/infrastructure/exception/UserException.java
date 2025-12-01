package com.book.igo.user.infrastructure.exception;

import com.book.igo.common.exception.AppException;

public class UserException extends AppException {
    public UserException(UserErrorCode code) { super(code); }

    public UserException(UserErrorCode errorCode, Object... args) {
        super(errorCode, args);
    }

}
