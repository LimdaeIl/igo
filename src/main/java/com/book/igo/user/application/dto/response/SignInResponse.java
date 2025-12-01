package com.book.igo.user.application.dto.response;

import com.book.igo.user.domain.entity.User;

public record SignInResponse(
        Long userId,
        String email,
        String nickName,
        String accessToken,
        String refreshToken
) {

    public static SignInResponse of(User user, String accessToken, String refreshToken) {
        return new SignInResponse(
                user.getId(),
                user.getEmail(),
                user.getNickName(),
                accessToken,
                refreshToken
        );
    }
}