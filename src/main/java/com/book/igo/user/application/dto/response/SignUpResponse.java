package com.book.igo.user.application.dto.response;

import com.book.igo.user.domain.entity.User;
import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record SignUpResponse(
        Long id,
        String email,
        String nickName,
        String phoneNumber,
        String profileImage,
        String profileMessage,
        String mbti,
        boolean notificationEnabled,
        boolean isDeleted
) {

    public static SignUpResponse from(User savedUser) {
        return SignUpResponse.builder()
                .id(savedUser.getId())
                .email(savedUser.getEmail())
                .nickName(savedUser.getNickName())
                .phoneNumber(savedUser.getPhoneNumber())
                .profileImage(savedUser.getProfileImage())
                .profileMessage(savedUser.getProfileMessage())
                .mbti(savedUser.getMbti())
                .notificationEnabled(savedUser.isNotificationEnabled())
                .isDeleted(savedUser.isDeleted())
                .build();
    }
}
