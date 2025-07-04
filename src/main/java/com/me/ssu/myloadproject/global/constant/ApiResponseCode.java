package com.me.ssu.myloadproject.global.constant;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

/**
 * @JsonFormat(shape = JsonFormat.Shape.OBJECT)
 *  Json으로 직렬화할 때 어떻게 표현할지
 *
 */

/**
 * OK(200, "R20000", "정상");
 */
/**
 * Json으로 직렬화(객체를 저장하거나 전송할 수 있게 문자열이나 바이트 형태로 바꾸는 과정)
 *  직렬화 객체 -> Json
 *  역직렬화 Json -> 객체
 * {
 *   "status": 200,
 *   "code": "R20000",
 *   "message": "정상"
 * }
 */
@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ApiResponseCode {
	OK(200, "R20000", "정상"),

	BAD_REQUEST(400, "R40000", "비정상적인 요청입니다."),
	INVALID_INPUT_VALUE(400, "R40001", "유효하지 않은 값입니다."),
	INVALID_TYPE_VALUE(400, "R40002", "유효하지 않은 값입니다."),
	BODY_READ_FAILED(400, "R40003", "BODY JSON parse error"),
	BIZ_DEFAULT_ERROR(400, "R40010", "처리할 수 없는 요청입니다."),
	ENCRYPTION_FAILURE(400, "R40010-001", "암호화 실패"),
	REFRESH_TOKEN_RENEW_FAILURE(400, "R40010-002", "리프래시 토큰 갱신 실패"),
	USER_SIGNUP_FAILURE(400, "R40010-003", "가입이 불가능합니다."),
	USER_CERTIFICATION_NOT_FOUND(400, "R40010-004", "사용자 인증 데이터가 없습니다."),

	// 회원
	MEMBER_CERT_CREATION_FAILURE(400, "R40032-001", "회원인증 생성실패"),
	MEMBER_CERT_VERIFICATION_FAILURE(400, "R40032-002", "회원인증 검증실패"),

	UNAUTHORIZED(401, "R40100", "인증이 필요한 API 입니다."),
	EXPIRED_TOKEN(401, "R40101", "만료된 토큰입니다."),
	EXPIRED_OR_INVALID_TOKEN(401, "R40102", "만료되었거나 유효하지 않은 토큰입니다."),
	INVALID_TOKEN(401, "R40103", "토큰이 유효하지 않거나, 검증에 실패했습니다."),
	JWT_TOKEN_VALIDATION_FAILURE(401, "R40104", "토큰 검증 실패"),
	JWT_TOKEN_NOT_FOUND(401, "R40105", "토큰 없음"),
	LOGIN_FAILURE(401, "R40106", "로그인을 실패했습니다. 아이디/비밀번호를 확인해 주세요."),
	LOGOUT(401, "R40107", "로그아웃 되었습니다."),

	ACCESS_DENIED(403, "R40300", "접근이 허용되지 않습니다."),

	NOT_FOUND(404, "R40400", "존재하지 않거나 비활성화된 API 입니다."),
	DATA_NOT_FOUND(404, "R40402", "데이터가 존재하지 않습니다."),

	METHOD_NOT_ALLOWED(405, "R40500", "허용되지 않는 Http Method 입니다."),

	UNSUPPORTED_MEDIA_TYPE(415, "R41500", "지원하지 않거나 처리할 수 없는 요청입니다."),

	INTERNAL_SERVER_ERROR(500, "R50000", "처리 중 오류가 발생했습니다."),

	EXTERNAL_API_UNAVAILABLE(503, "R50301", "서버가 요청을 처리할 준비가 되지 않았습니다."),
	;

	private final String code;
	private final String message;
	private final int status;

	ApiResponseCode(final int status, final String code, final String message) {
		this.status = status;
		this.message = message;
		this.code = code;
	}
}
