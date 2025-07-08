package com.me.ssu.myloadproject.global.error.exception;

import com.me.ssu.myloadproject.global.constant.ApiResponseCode;

/**
 * 비즈니스 로직 오류 요청 추가 : 데이터
 */
public class DataNotFoundException extends BusinessRuntimeException {
	public DataNotFoundException() {
		super(ApiResponseCode.DATA_NOT_FOUND);
	}

	public DataNotFoundException(String message) {
		super(ApiResponseCode.DATA_NOT_FOUND, message);
	}
}
