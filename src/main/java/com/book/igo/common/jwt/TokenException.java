package com.book.igo.common.jwt;

import com.book.igo.common.exception.AppException;
import com.book.igo.common.exception.ErrorCode;

public class TokenException extends AppException {

    public TokenException(ErrorCode errorCode) {
        super(errorCode);
    }

    public TokenException(JwtErrorCode errorCode, Object... args) {
        super(errorCode, args);
    }
}
