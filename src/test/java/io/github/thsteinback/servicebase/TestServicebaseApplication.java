package io.github.thsteinback.servicebase;

import org.springframework.boot.SpringApplication;

public class TestServicebaseApplication {

	public static void main(String[] args) {
		SpringApplication.from(ServicebaseApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
