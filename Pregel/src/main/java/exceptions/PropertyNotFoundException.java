package exceptions;

/**
 * Represents the exception when invalid property is requested
 * 
 * @author Prakash Chandrasekaran
 * @author Gautham Narayanasamy
 * @author Vijayaraghavan Subbaiah
 * 
 */

public class PropertyNotFoundException extends Exception {
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs the property not found exception
	 * 
	 * @param propertyKey
	 *            Represents the invalid property key
	 */
	public PropertyNotFoundException(String propertyKey) {
		super(propertyKey + " is not found in props file");
	}
}
