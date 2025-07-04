package com.me.ssu.myloadproject.global.dto;

import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
public class Pagination<DTO> {
	private final List<DTO> contents;
	private final Integer totalPage;
	private final long totalCount;
	private final int page;
	private final int pageSize;

	public Pagination(Page<DTO> page) {
		this.contents = page.getContent();
		this.totalPage = page.getTotalPages();
		this.totalCount = page.getTotalElements();
		this.page = page.getNumber() + 1;
		this.pageSize = page.getSize();
	}
}

