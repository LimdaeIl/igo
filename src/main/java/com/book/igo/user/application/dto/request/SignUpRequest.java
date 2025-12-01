package com.book.igo.user.application.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record SignUpRequest(

        @Email(message = "회원가입: 이메일 형식이 올바르지 않습니다.")
        @NotBlank(message = "이메일: 이메일은 필수 입니다.")
        String email,

        @NotBlank(message = "비밀번호: 비밀번호는 필수 입니다.")
        @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-={}|\\[\\]:\";'<>?,./]).{8,}$",
                message = "비밀번호: 최소 8자, 영문자/숫자/특수문자를 각각 1개 이상 포함해야 합니다."
        )
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        String password,

        @NotBlank(message = "닉네임: 사용자명은 필수입니다.")
        @Pattern(
                regexp = "^[A-Za-z0-9가-힣!@#$%^&*()_+\\-={}|\\[\\]:\";'<>?,./]{1,12}$",
                message = "닉네임: 1~12자, 영문/숫자/한글/특수기호만 허용합니다."
        )
        String nickname
) {

}
