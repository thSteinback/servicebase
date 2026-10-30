package io.github.thsteinback.servicebase.shared.error;

/**
 * Erro de validação de um campo, devolvido na propriedade {@code errors}
 * do {@link org.springframework.http.ProblemDetail}.
 */
public record FieldError(String field, String message) {
}
