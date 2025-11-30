package com.book.igo.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;


@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        Boolean success,
        T data
) {

    /* ---- SUCCESS ---- */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(Boolean.TRUE, data);
    }

    public static <T> ApiResponse<T> success(Boolean isSucceeded, T data) {
        return new ApiResponse<>(isSucceeded, data);
    }

    /* ---- ERROR ---- */
    public static <T> ApiResponse<T> error(Boolean isSucceeded) {
        return new ApiResponse<>(isSucceeded, null);
    }

    public static <T> ApiResponse<T> error(Boolean isSucceeded, T data) {
        return new ApiResponse<>(isSucceeded, data);
    }
}