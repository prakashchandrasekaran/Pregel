package api;

import graphs.VertexID;

import java.util.HashMap;
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
	 * @return Returns the list of outgoing edges for this source vertex
	 */
	public List<Edge> getOutgoingEdges() {
		return outgoingEdges;
	}

	public String toString() {
		return "(" + vertexID + "-" + outgoingEdges + ")";
	}

	public Map<VertexID, Message> compute(Iterator<Message> messageIterator) {
		Map<VertexID, Message> vertexMessageMap = new HashMap<>();
		Data resultData = null;
		while (messageIterator.hasNext()) {
			Message message = messageIterator.next();
			resultData = message.getData();
			// do some operation on the data and get the aggregate value.
		}

		Message resultMsg = new Message();
		resultMsg.setData(resultData);
		// Iterate the outgoing edges and assign the resultant message to be
		// sent to each of the destination vertices.
		for (Edge edge : outgoingEdges) {
			vertexMessageMap.put(edge.getDestID(), resultMsg);
		}
		return vertexMessageMap;
	}

}
