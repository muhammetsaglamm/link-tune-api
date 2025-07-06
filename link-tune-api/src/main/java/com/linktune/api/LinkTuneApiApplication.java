package com.linktune.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories; // YENİ IMPORT

@EnableCaching
// YENİ ANNOTATION: Spring'e Repository'lerin nerede olduğunu söylüyoruz.
@EnableJpaRepositories(basePackages = "com.linktune.api.Repository")
@SpringBootApplication
public class LinkTuneApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(LinkTuneApiApplication.class, args);
	}

}