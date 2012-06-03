package system;

import java.io.Serializable;

import api.Data;
import graphs.VertexID;

public class Message<T> implements Serializable {

	private static final long serialVersionUID = -2737750866653457002L;
	private VertexID sourceID;
	private Data<T> data;
	
	public Message(VertexID sourceID, Data<T> data) {
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
	public Data<T> getData() {
		return data;
	}

	/**
	 * sets data for this message
	 * @param data
	 */
	public void setData(Data<T> data) {
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
		Message<Double> message = new Message<>(s, null);
		System.out.println(message);
	}

}
