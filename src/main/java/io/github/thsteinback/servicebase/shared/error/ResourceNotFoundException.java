package io.github.thsteinback.servicebase.shared.error;

/**
 * Lançada quando um recurso buscado pelo identificador não existe.
 * O {@link GlobalExceptionHandler} converte em uma resposta 404.
 */
public class ResourceNotFoundException extends RuntimeException {

	private final String resource;
	private final Object id;

	public ResourceNotFoundException(String resource, Object id) {
		super("%s não encontrado(a) com id %s".formatted(resource, id));
		this.resource = resource;
		this.id = id;
	}

	public String getResource() {
		return resource;
	}

	public Object getId() {
		return id;
	}

}
