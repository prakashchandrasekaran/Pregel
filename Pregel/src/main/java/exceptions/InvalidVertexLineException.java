package exceptions;

@SuppressWarnings("serial")
public class InvalidVertexLineException extends Exception {
	public InvalidVertexLineException(String vertexLine, String message) {
		super(vertexLine + " - " + message);
	}
}
