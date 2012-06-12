package system;

import java.io.Serializable;

import api.Data;
import graphs.VertexID;

/**
 * Represents the message to be sent between vertices
 * 
 * @author Prakash Chandrasekaran
 * @author Gautham Narayanasamy
 * @author Vijayaraghavan Subbaiah
 * 
 */
public class Message implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -2737750866653457002L;
	/** The source vertex ID */
	private VertexID sourceID;
	/** Data to be sent */
	private Data<?> data;

	/**
	 * Constructs a Message
	 * 
	 * @param sourceID
	 *            The source vertex ID
	 * @param data
	 *            Data to be sent
	 */
	public Message(VertexID sourceID, Data<?> data) {
		this.sourceID = sourceID;
		this.data = data;
	}

	/**
	 * gets the source Vertex Identifier of the message
	 * 
	 * @return returns the source Vertex Identifier of the message
	 */
	public VertexID getSourceID() {
		return this.sourceID;
	}

	/**
	 * gets the data associated with this message
	 * 
	 * @return Returns the data associated with this message
	 */
	public Data<?> getData() {
		return data;
	}

	/**
	 * sets data for this message
	 * 
	 * @param data
	 *            the data associated with this message
	 */
	public void setData(Data<?> data) {
		this.data = data;
	}

	/**
	 * String representation of Message
	 */
	public String toString() {
		return "[" + sourceID + " -- " + "{" + data + "}" + "]";
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		VertexID s = new VertexID(0, 1);
		Message message = new Message(s, null);
		System.out.println(message);
	}

}
