package com.me.ssu.myloadproject.global.error.exception;

import com.me.ssu.myloadproject.global.constant.ApiResponseCode;
import lombok.Getter;

/**
 * @SuppressWarnings("unused")
 *  사용하지 않아도 경고 없애줌 (예: IDE에서 경고 뜨는 것 방지)
 */

/**
 * 용도 : 복구 가능 예외일 때
 *  Checked Exception(검사 예외)
 */
@Getter
@SuppressWarnings("unused")
public class BusinessException extends Exception {
	private final ApiResponseCode errorCode;
	private final String errorMessage;

	public BusinessException(ApiResponseCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
		this.errorMessage = errorCode.getMessage();
	}

	public BusinessException(ApiResponseCode errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
		this.errorMessage = message;
	}

	public BusinessException(String message) {
		this(ApiResponseCode.BAD_REQUEST, message);
	}
}
