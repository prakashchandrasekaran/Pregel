package system;

import java.io.Serializable;

import api.Data;
import graphs.VertexID;

public class Message implements Serializable {

	private static final long serialVersionUID = -2737750866653457002L;
	private VertexID sourceID;
	private Data<?> data;
	
	public Message(VertexID sourceID, Data<?> data) {
		this.sourceID = sourceID;		
		this.data = data;
	}
	
	/**
	 * returns source Vertex Identifier of the message
	 * @return
	 */
	public VertexID getSourceID() {
		return this.sourceID;
	}
	
	
	/**
	 * returns data associated with this message
	 * @return
	 */
	public Data<?> getData() {
		return data;
	}

	/**
	 * sets data for this message
	 * @param data
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
