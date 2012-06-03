package api;

import graphs.VertexID;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import system.Message;

/**
 * Represents the pair object containing partitionID and vertexIdentifier
 * 
 * @author Prakash Chandrasekaran
 * @author Gautham Narayanasamy
 * @author Vijayaraghavan Subbaiah
 * 
 */
public abstract class Vertex {

	private VertexID vertexID;
	private List<Edge> outgoingEdges;
	private Data data;
	
	/**
	 * 
	 * @param vertexID
	 *            Represents the pair object containing partitionID and
	 *            vertexIdentifier
	 * @param outgoingEdges
	 *            Represents the list of outgoing edges from the source vertex
	 */
	protected Vertex(VertexID vertexID, List<Edge> outgoingEdges) {
		this.vertexID = vertexID;
		this.outgoingEdges = outgoingEdges;
	}

	/**
	 * Gets the vertex identifier
	 * 
	 * @return Returns the vertex identifier
	 */
	public VertexID getID() {
		return vertexID;
	}

	/**
	 * Gets the list of outgoing edges for this source vertex
	 * 
	 * @return Returns the list of outgoing edges for this source vertex
	 */
	public List<Edge> getOutgoingEdges() {
		return outgoingEdges;
	}

	public String toString() {
		return "(" + vertexID + "-" + outgoingEdges + ")";
	}

	public abstract Map<VertexID, Message> compute(Iterator<Message> messageIterator);

	public Data getData(){
		return data;
	}
	
	public void setData(Data data){
		this.data = data;
	}
}
