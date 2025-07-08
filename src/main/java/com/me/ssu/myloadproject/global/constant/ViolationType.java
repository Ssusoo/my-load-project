package com.me.ssu.myloadproject.global.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ViolationType {
	RECEIPT_CLOSED("신청 마감")
	;

	private final String description;
}
