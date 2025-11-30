package com.book.igo.user.presentation;

import com.book.igo.user.application.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@RestController
public class AuthController {

    private final AuthService authService;

    // 1. 회원가입
    // 2. 로그인
    // 3. 토큰 재발급
    // 4. 로그아웃

}
