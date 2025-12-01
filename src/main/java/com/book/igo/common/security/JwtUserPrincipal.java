package com.book.igo.common.security;

import com.book.igo.user.domain.entity.UserRole;

/**
 * JWT에서 추출한 현재 로그인 사용자 정보.
 * 컨트롤러에서 @AuthenticationPrincipal JwtUserPrincipal 로 바로 주입 받을 예정.
 */
public record JwtUserPrincipal(
        Long id,
        String email,
        String nickName,
        UserRole role
) {
}