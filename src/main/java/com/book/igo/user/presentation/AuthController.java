package com.book.igo.user.presentation;

import com.book.igo.common.response.ApiResponse;
import com.book.igo.user.application.dto.request.LogoutRequest;
import com.book.igo.user.application.dto.request.SignInRequest;
import com.book.igo.user.application.dto.request.SignUpRequest;
import com.book.igo.user.application.dto.request.TokenReissueRequest;
import com.book.igo.user.application.dto.response.SignInResponse;
import com.book.igo.user.application.dto.response.SignUpResponse;
import com.book.igo.user.application.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@RestController
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignUpResponse>> signUp(
            @RequestBody @Valid SignUpRequest request
    ) {
        SignUpResponse response = authService.signUp(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response));
    }

    @PostMapping("/sign-in")
    public ResponseEntity<ApiResponse<SignInResponse>> signIn(
            @RequestBody @Valid SignInRequest request
    ) {
        SignInResponse response = authService.signIn(request);

        return ResponseEntity
                .ok(ApiResponse.success(response));
    }


    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<SignInResponse>> reissue(
            @RequestHeader(name = "Authorization", required = false) String accessTokenHeader,
            @RequestBody TokenReissueRequest request
    ) {
        SignInResponse response = authService.reissue(accessTokenHeader, request.refreshToken());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @RequestHeader(name = "Authorization", required = false) String accessTokenHeader,
            @RequestBody LogoutRequest request
    ) {
        authService.logout(accessTokenHeader, request.refreshToken());
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
