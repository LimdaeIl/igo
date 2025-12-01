package com.book.igo.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import org.springframework.http.HttpStatus;

/**
 * 표준 에러 응답 스키마 (RFC 9457 Problem Details 확장 버전).
 *
 * <p>모든 예외 상황을 <b>일관된 포맷</b>으로 직렬화해 클라이언트에 제공합니다.</p>
 *
 * <ul>
 *   <li><b>type</b>: 문제 유형을 식별하는 URL (예: {@code https://your-domain.com/problem/user-not-found})</li>
 *   <li><b>title</b>: 짧은 오류 요약 (예: {@code USER_NOT_FOUND})</li>
 *   <li><b>status</b>: HTTP 상태 코드(정수)</li>
 *   <li><b>detail</b>: 사람에게 전달할 상세 메시지(민감 정보 포함 금지)</li>
 *   <li><b>instance</b>: 오류가 발생한 요청 경로(예: {@code /api/v1/users/1})</li>
 *   <li><b>errorCode</b>: 서비스 내부 비즈니스 코드(예: {@code U001}, 선택)</li>
 *   <li><b>data</b>: 필드 단위 오류 목록(선택, 검증 실패시 확장 필드)</li>
 * </ul>
 *
 * @apiNote 성공 응답은 {@code ApiResponse<T>}를 사용하세요.
 * @implNote {@code data}는 비어있으면 {@code null}로 직렬화되어 페이로드를 줄입니다.
 * @since 1.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        String type,              // RFC: 문제 유형 URL
        String title,             // RFC: 짧은 오류 요약 (심볼릭 코드명)
        int status,               // HTTP 상태 코드
        String detail,            // RFC: 상세 메시지
        String instance,          // RFC: 문제 발생 리소스(요청 경로)
        String errorCode,         // 서비스 내부 비즈니스 코드 (예: U001/C001 등)
        List<FieldError> data     // 필드 단위 검증 에러 목록 (확장 필드)
) {

    /**
     * 최소 정보(status, detail, 필드오류)만으로 에러 응답을 생성합니다.
     *
     * <p>기존 사용처: {@code ErrorResponse.of(status, message, data)}</p>
     *
     * @param status  HTTP 상태
     * @param message 사용자에게 보여줄 상세 메시지(detail)
     * @param data    필드 오류 목록(비어있으면 null 처리)
     * @return 표준 에러 응답
     */
    public static ErrorResponse of(HttpStatus status, String message, List<FieldError> data) {
        return new ErrorResponse(
                null, // type
                null, // title
                status.value(),
                message, // detail
                null, // instance
                null, // errorCode
                (data == null || data.isEmpty()) ? null : data
        );
    }

    /**
     * 상태/메시지로 에러 응답을 생성합니다.
     *
     * <p>기존 사용처: {@code ErrorResponse.of(status, message)}</p>
     */
    public static ErrorResponse of(HttpStatus status, String message) {
        return new ErrorResponse(
                null,
                null,
                status.value(),
                message,
                null,
                null,
                null
        );
    }

    /**
     * 상태/메시지/도메인 에러코드로 에러 응답을 생성합니다.
     *
     * <p>
     * 기존의 {@code errorCode} 파라미터는
     * <ul>
     *   <li>RFC title에 매핑(예: {@code USER_NOT_FOUND})</li>
     *   <li>errorCode에도 그대로 매핑(예: {@code U001} 대신 심볼릭 코드를 쓰는 경우)</li>
     * </ul>
     * 로 처리합니다.
     * </p>
     *
     * <p>기존 사용처: {@code ErrorResponse.of(status, message, errorCode)}</p>
     */
    public static ErrorResponse of(HttpStatus status, String message, String errorCode) {
        return new ErrorResponse(
                null,          // type (GlobalExceptionHandler에서 필요시 채움)
                errorCode,     // title ← 심볼릭 코드명 (예: USER_NOT_FOUND)
                status.value(),
                message,       // detail
                null,          // instance
                errorCode,     // errorCode ← 내부 코드로 재사용 (필요시 별도 분리 가능)
                null
        );
    }

    /**
     * 상태/메시지/도메인 에러코드/필드오류 목록으로 에러 응답을 생성합니다.
     *
     * <p>기존 사용처: {@code ErrorResponse.of(status, message, errorCode, data)}</p>
     */
    public static ErrorResponse of(HttpStatus status, String message, String errorCode,
            List<FieldError> data) {
        return new ErrorResponse(
                null,          // type
                errorCode,     // title
                status.value(),
                message,       // detail
                null,          // instance
                errorCode,     // errorCode
                (data == null || data.isEmpty()) ? null : data
        );
    }

    /**
     * RFC 필드를 모두 직접 지정할 수 있는 팩터리.
     *
     * <p>새 코드에서는 이 정적 메서드를 우선적으로 사용하는 것을 권장합니다.</p>
     */
    public static ErrorResponse of(String type,
            String title,
            HttpStatus status,
            String detail,
            String instance,
            String errorCode,
            List<FieldError> data) {
        return new ErrorResponse(
                type,
                title,
                status.value(),
                detail,
                instance,
                errorCode,
                (data == null || data.isEmpty()) ? null : data
        );
    }

    /**
     * 단일 필드 오류.
     *
     * @param field  유효성 검증 대상 필드명(경로 포함 가능)
     * @param reason 실패 사유(로컬라이즈 가능)
     */
    public record FieldError(String field, String reason) {

        public static FieldError of(String field, String reason) {
            return new FieldError(field, reason);
        }
    }
}
