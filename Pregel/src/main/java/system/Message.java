package system;

import java.io.Serializable;

import api.Data;
import graphs.VertexID;

public class Message<T> implements Serializable {

	private VertexID sourceID;
	private VertexID destID;
	private Data<T> data;
	
	public Message(VertexID sourceID, VertexID destID, Data<T> data) {
		this.sourceID = sourceID;
		this.destID = destID;
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
	 * returns destination Vertex Identifier of the message
	 * @return
	 */
	public VertexID getDestID() {
		return this.destID;
	}
	
	
	/**
	 * returns data associated with this message
	 * @return
	 */
	public Data getData() {
		return data;
	}

	/**
	 * sets data for this message
	 * @param data
	 */
	public void setData(Data data) {
		this.data = data;
	}
	
	/**
	 * String representation of Message
	 */
	public String toString() {
		return "[" + sourceID + " --" + "{" + data + "}" +  "-> " + destID + "]"; 
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		VertexID s = new VertexID(0, 1);
		VertexID d = new VertexID(1, 2);
		Message<Double> message = new Message<>(s,d, null);
		System.out.println(message);
	}

}
