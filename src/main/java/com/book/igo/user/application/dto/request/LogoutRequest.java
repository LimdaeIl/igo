package com.book.igo.user.application.dto.request;

public record LogoutRequest(
        String refreshToken
) {
}