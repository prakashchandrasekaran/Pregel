package exceptions;

public class PropertyNotFoundException extends Exception {
	private static final long serialVersionUID = 1L;

	public PropertyNotFoundException(String propertyKey) {
		super(propertyKey + " is not found in props file");
	}
}
