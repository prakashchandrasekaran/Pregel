package system;

import api.Data;
import graphs.VertexID;

public class Message {

	private VertexID sourceID;
	private VertexID destID;
	private Data data;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public Data getData() {
		return data;
	}

	public void setData(Data data) {
		this.data = data;
	}

}
