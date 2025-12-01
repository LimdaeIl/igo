package com.book.igo.user.application.dto.request;


public record TokenReissueRequest(
        String refreshToken
) {
}