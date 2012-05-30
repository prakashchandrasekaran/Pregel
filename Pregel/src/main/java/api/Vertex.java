package api;

import java.util.List;
import graphs.VertexID;

/**
 * Represents the pair object containing partitionID and vertexIdentifier
 * 
 * @author Prakash Chandrasekaran
 * @author Gautham Narayanasamy
 * @author Vijayaraghavan Subbaiah
 * 
 */
public class Vertex {

	private VertexID vertexID;
	private List<Edge> outgoingEdges;

	/**
	 * 
	 * @param vertexID
	 *            Represents the pair object containing partitionID and
	 *            vertexIdentifier
	 * @param outgoingEdges
	 *            Represents the list of outgoing edges from the source vertex
	 */
	public Vertex(VertexID vertexID, List<Edge> outgoingEdges) {
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
	 * @return Returns the list of outoing edges for this source vertex
	 */
	public List<Edge> getOutgoingEdges() {
		return outgoingEdges;
	}

	public String toString() {
		return "(" + vertexID + "-" + outgoingEdges + ")";
	}

}
