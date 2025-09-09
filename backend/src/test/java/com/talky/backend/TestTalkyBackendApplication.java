package com.talky.backend;

import org.springframework.boot.SpringApplication;

public class TestTalkyBackendApplication {

	public static void main(String[] args) {
		SpringApplication.from(TalkyBackendApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
