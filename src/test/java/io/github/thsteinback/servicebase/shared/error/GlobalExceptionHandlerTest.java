package io.github.thsteinback.servicebase.shared.error;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@WebMvcTest(controllers = GlobalExceptionHandlerTest.FakeController.class)
@Import(GlobalExceptionHandlerTest.FakeController.class)
class GlobalExceptionHandlerTest {

	private static final MediaType PROBLEM_JSON = MediaType.APPLICATION_PROBLEM_JSON;

	@Autowired
	MockMvc mvc;

	@Test
	void corpoInvalidoRetorna400ComOsCamposComErro() throws Exception {
		mvc.perform(post("/fake").contentType(MediaType.APPLICATION_JSON).content("""
				{"name": "", "price": -10}
				"""))
			.andExpect(status().isBadRequest())
			.andExpect(content().contentTypeCompatibleWith(PROBLEM_JSON))
			.andExpect(jsonPath("$.title").value("Dados inválidos"))
			.andExpect(jsonPath("$.type").value("/problems/validation"))
			.andExpect(jsonPath("$.errors", hasSize(2)))
			.andExpect(jsonPath("$.errors[0].field").value("name"))
			.andExpect(jsonPath("$.errors[1].field").value("price"));
	}

	@Test
	void jsonMalformadoRetorna400() throws Exception {
		mvc.perform(post("/fake").contentType(MediaType.APPLICATION_JSON).content("{ isso não é json"))
			.andExpect(status().isBadRequest())
			.andExpect(content().contentTypeCompatibleWith(PROBLEM_JSON))
			.andExpect(jsonPath("$.title").value("Corpo da requisição inválido"));
	}

	@Test
	void parametroDeConsultaInvalidoRetorna400() throws Exception {
		mvc.perform(get("/fake").param("size", "500"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.title").value("Dados inválidos"))
			.andExpect(jsonPath("$.errors[0].field").value("size"));
	}

	@Test
	void recursoInexistenteRetorna404() throws Exception {
		UUID id = UUID.randomUUID();
		mvc.perform(get("/fake/{id}", id))
			.andExpect(status().isNotFound())
			.andExpect(content().contentTypeCompatibleWith(PROBLEM_JSON))
			.andExpect(jsonPath("$.title").value("Recurso não encontrado"))
			.andExpect(jsonPath("$.detail").value("Serviço não encontrado(a) com id " + id))
			.andExpect(jsonPath("$.instance").value("/fake/" + id));
	}

	@Test
	void rotaInexistenteRetorna404NoMesmoFormato() throws Exception {
		mvc.perform(get("/nao-existe"))
			.andExpect(status().isNotFound())
			.andExpect(content().contentTypeCompatibleWith(PROBLEM_JSON))
			.andExpect(jsonPath("$.status").value(404));
	}

	@Test
	void erroInesperadoRetorna500SemExporDetalhes() throws Exception {
		mvc.perform(get("/fake/boom"))
			.andExpect(status().isInternalServerError())
			.andExpect(jsonPath("$.title").value("Erro interno"))
			.andExpect(jsonPath("$.detail").value("Ocorreu um erro inesperado. Tente novamente mais tarde."));
	}

	record FakeRequest(@NotBlank String name, @Positive int price) {
	}

	/** Controller só de teste, para exercitar cada tipo de erro. */
	@RestController
	@RequestMapping("/fake")
	@Validated
	static class FakeController {

		@PostMapping
		void create(@Valid @RequestBody FakeRequest request) {
		}

		@GetMapping
		void list(@RequestParam @Max(100) int size) {
		}

		@GetMapping("/boom")
		void boom() {
			throw new IllegalStateException("senha do banco: 123");
		}

		@GetMapping("/{id}")
		void find(@PathVariable UUID id) {
			throw new ResourceNotFoundException("Serviço", id);
		}

	}

}
