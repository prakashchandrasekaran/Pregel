package applications;

import graphs.VertexID;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import system.Edge;
import system.Message;
import api.Vertex;

/**
 * Defines the Vertex implementation for the Shortest-Path graph problem.
 * 
 * @author Prakash Chandrasekaran
 * @author Gautham Narayanasamy
 * @author Vijayaraghavan Subbaiah
 */
public class ShortestPathVertex extends Vertex {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -4522743163505176658L;

	/**
	 * Instantiates a new shortest path vertex.
	 * 
	 * @param vertexID
	 *            the vertex id
	 * @param outgoingEdges
	 *            the outgoing edges
	 * @throws RemoteException
	 */
	public ShortestPathVertex(VertexID vertexID, List<Edge> outgoingEdges)
			throws RemoteException {
		super(vertexID, outgoingEdges);
		this.setData(new ShortestPathData(Double.MAX_VALUE));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see api.Vertex#compute(java.util.Iterator)
	 */

	@Override
	public Map<VertexID, Message> compute(Iterator<Message> messageIterator)
			throws RemoteException {
		// System.out.println("ShortestPathVertex: compute");
		Map<VertexID, Message> vertexMessageMap = new HashMap<>();
		ShortestPathData minData = (ShortestPathData) this.getData();

		while (messageIterator.hasNext()) {
			Message message = messageIterator.next();
			ShortestPathData currData = (ShortestPathData) message.getData();
			if (minData.compareTo(currData) > 0) {
				minData = currData;
			}
		}
		// sets the current vertex data to minimum data computed
		this.setData(minData);
		// Iterate the outgoing edges and assign the resultant message to be
		// sent to each of the destination vertices.

		// create message to all outgoing edges

		for (Edge edge : this.getOutgoingEdges()) {
			vertexMessageMap.put(
					edge.getDestID(),
					new Message(this.getID(), new ShortestPathData(minData
							.getValue() + edge.getEdgeWeight())));
		}
		return vertexMessageMap;
	}
}
