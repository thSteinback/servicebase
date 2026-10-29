package io.github.thsteinback.servicebase;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class ServicebaseApplicationTests {

	@Autowired
	JdbcTemplate jdbc;

	@Test
	void contextLoads() {
	}

	@Test
	void flywayCreatesBusinessTable() {
		Integer id = jdbc.queryForObject(
				"INSERT INTO business (name, segment) VALUES ('Barbearia Teste', 'barbearia') RETURNING 1",
				Integer.class);
		String settings = jdbc.queryForObject("SELECT settings::text FROM business LIMIT 1", String.class);

		assertThat(id).isEqualTo(1);
		assertThat(settings).isEqualTo("{}");
	}

}
