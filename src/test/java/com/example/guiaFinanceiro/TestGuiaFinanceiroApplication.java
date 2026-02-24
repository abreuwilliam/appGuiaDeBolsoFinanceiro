package com.example.guiaFinanceiro;

import org.springframework.boot.SpringApplication;

public class TestGuiaFinanceiroApplication {

	public static void main(String[] args) {
		SpringApplication.from(GuiaFinanceiroApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
