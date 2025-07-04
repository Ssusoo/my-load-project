package com.me.ssu.myloadproject.global.config.database;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * slave 읽기 전용
 * master 쓰기 전용
 */
@Slf4j
public class ReplicationRoutingDataSource extends AbstractRoutingDataSource {
	@Override
	protected Object determineCurrentLookupKey() {
		return TransactionSynchronizationManager.isCurrentTransactionReadOnly()
				? "slave"
				: "master";
	}
}
