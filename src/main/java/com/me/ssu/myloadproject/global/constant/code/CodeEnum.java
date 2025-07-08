package com.me.ssu.myloadproject.global.constant.code;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * String -> Enum 맵핑
 *  프론트에서 "A" 값이 넘어올 경우
 *  문자열을 내부 Enum 타입으로 바꿔야 하는데 해당 클래스 자동으로 변환 처리
 *  CodeEnum은 Eunm마다 공통으로 사용할 수 있는 getCode() 강제
 */
public interface CodeEnum {
	@JsonValue
	String getCode();
	String getCodeName();

	static String getCode(CodeEnum codeEnum) {
		return codeEnum != null ? codeEnum.getCode() : null;
	}

	static String getCodeName(CodeEnum codeEnum) {
		return codeEnum != null ? codeEnum.getCodeName() : null;
	}
}
