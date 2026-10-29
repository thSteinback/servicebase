package io.github.thsteinback.servicebase;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {

	@Bean
	@ServiceConnection
	PostgreSQLContainer postgresContainer() {
		// Mesma versão do compose.yaml, para os testes rodarem no banco real de dev.
		return new PostgreSQLContainer(DockerImageName.parse("postgres:17-alpine"));
	}

}
