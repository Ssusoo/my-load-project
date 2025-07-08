package com.me.ssu.myloadproject.global.config;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @EnableEncryptableProperties
 *  application.yml에서 ENC(...)로 감싼 암호화된 값을 자동 복호화 가능
 */
@Configuration
@EnableEncryptableProperties
public class JasyptConfig {
	@Value("${jasypt.encryptor.password}")
	private String jasyptEncryptorPassword;

	@Bean("jasyptStringEncryptor")
	public StringEncryptor stringEncryptor() {
		PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
		SimpleStringPBEConfig config = new SimpleStringPBEConfig();
		config.setPassword(jasyptEncryptorPassword); // 암복호화 비밀번호(비밀키)
		config.setAlgorithm("PBEWITHHMACSHA512ANDAES_256"); // 256 알고리즘
		config.setKeyObtentionIterations("1000");
		config.setPoolSize("1");
		config.setProviderName("SunJCE");
		config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator"); // 매 암호화마다 다른 salt 사용(랜덤화)
		config.setIvGeneratorClassName("org.jasypt.iv.RandomIvGenerator"); // AES를 위한 랜덤 설정
		config.setStringOutputType("base64"); // 출력
		encryptor.setConfig(config);
		return encryptor;
	}
}
