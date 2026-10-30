package io.github.thsteinback.servicebase.shared.error;

import java.net.URI;
import java.util.Comparator;
import java.util.List;

import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Padroniza todas as respostas de erro da API no formato Problem Details (RFC 9457).
 *
 * <p>Os erros do próprio Spring MVC (404 de rota inexistente, 405, 415 etc.) já são
 * tratados pela classe base. Aqui ficam as regras específicas da aplicação.
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	static final URI VALIDATION_TYPE = URI.create("/problems/validation");
	static final URI NOT_FOUND_TYPE = URI.create("/problems/not-found");
	static final URI MALFORMED_BODY_TYPE = URI.create("/problems/malformed-body");
	static final URI INTERNAL_TYPE = URI.create("/problems/internal");

	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	/** Falha de Bean Validation no corpo da requisição ({@code @Valid @RequestBody}). */
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		List<FieldError> errors = ex.getBindingResult().getFieldErrors().stream()
			.map(e -> new FieldError(e.getField(), e.getDefaultMessage()))
			.sorted(Comparator.comparing(FieldError::field))
			.toList();
		return ResponseEntity.badRequest().body(validationProblem(errors));
	}

	/** Corpo ausente, JSON malformado ou tipo incompatível (ex.: texto em campo numérico). */
	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
			HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
				"O corpo da requisição está ausente ou malformado.");
		problem.setType(MALFORMED_BODY_TYPE);
		problem.setTitle("Corpo da requisição inválido");
		return ResponseEntity.badRequest().body(problem);
	}

	/** Falha de validação em parâmetros de rota ou de consulta ({@code @Validated} no controller). */
	@ExceptionHandler(ConstraintViolationException.class)
	ProblemDetail handleConstraintViolation(ConstraintViolationException ex) {
		List<FieldError> errors = ex.getConstraintViolations().stream()
			.map(v -> new FieldError(lastNode(v.getPropertyPath().toString()), v.getMessage()))
			.sorted(Comparator.comparing(FieldError::field))
			.toList();
		return validationProblem(errors);
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	ProblemDetail handleNotFound(ResourceNotFoundException ex) {
		ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
		problem.setType(NOT_FOUND_TYPE);
		problem.setTitle("Recurso não encontrado");
		return problem;
	}

	/** Qualquer erro inesperado: registra no log e não expõe detalhes internos ao cliente. */
	@ExceptionHandler(Exception.class)
	ProblemDetail handleUnexpected(Exception ex) {
		log.error("Erro inesperado ao processar a requisição", ex);
		ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
				"Ocorreu um erro inesperado. Tente novamente mais tarde.");
		problem.setType(INTERNAL_TYPE);
		problem.setTitle("Erro interno");
		return problem;
	}

	private static ProblemDetail validationProblem(List<FieldError> errors) {
		ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
				"Um ou mais campos são inválidos.");
		problem.setType(VALIDATION_TYPE);
		problem.setTitle("Dados inválidos");
		problem.setProperty("errors", errors);
		return problem;
	}

	/** Converte "metodo.arg0.campo" em "campo". */
	private static String lastNode(String path) {
		int dot = path.lastIndexOf('.');
		return dot < 0 ? path : path.substring(dot + 1);
	}

}
