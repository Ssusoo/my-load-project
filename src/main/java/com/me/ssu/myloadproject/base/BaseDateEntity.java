package com.me.ssu.myloadproject.base;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
public abstract class BaseDateEntity {
	@Column(name = "REG_DTM", nullable = false, updatable = false)
	private LocalDateTime registerAt;

	@Column(name = "UPD_DTM", nullable = false)
	private LocalDateTime updateAt;

	@PrePersist
	public void onPrePersist() {
		var now = LocalDateTime.now();
		this.registerAt = now;
		this.updateAt = now;
	}

	@PreUpdate
	public void onPreUpdate() {
		this.updateAt = LocalDateTime.now();
	}
}
