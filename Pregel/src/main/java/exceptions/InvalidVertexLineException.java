package exceptions;

/**
 * Represents the exception when vertex representation in file is invalid
 * 
 * @author Prakash Chandrasekaran
 * @author Gautham Narayanasamy
 * @author Vijayaraghavan Subbaiah
 */
@SuppressWarnings("serial")
public class InvalidVertexLineException extends Exception {
	/**
	 * Constructs the invalid vertex line exception
	 * 
	 * @param vertexLine
	 *            Represents the vertex line from the file
	 * @param message
	 *            Represents the message
	 */
	public InvalidVertexLineException(String vertexLine, String message) {
		super(vertexLine + " - " + message);
	}
}
