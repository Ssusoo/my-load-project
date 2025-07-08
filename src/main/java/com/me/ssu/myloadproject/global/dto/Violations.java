package com.me.ssu.myloadproject.global.dto;

import com.me.ssu.myloadproject.global.constant.ViolationType;

import java.util.Map;

public record Violations(
		String type,
		String description,
		Map<String, Object> details
) {
	public Violations(ViolationType type, Map<String, Object> details) {
		this(type.name(), type.getDescription(), details);
	}
}
