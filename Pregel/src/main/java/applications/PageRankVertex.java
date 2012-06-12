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
 * Defines the Vertex implementation for the PageRank graph problem.
 * 
 * @author Prakash Chandrasekaran
 * @author Gautham Narayanasamy
 * @author Vijayaraghavan Subbaiah
 */

public class PageRankVertex extends Vertex {
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 3545610632519357452L;

	/**
	 * Instantiates a new PageRank vertex.
	 * 
	 * @param vertexID
	 *            the vertex id
	 * @param outgoingEdges
	 *            the outgoing edges
	 * @throws RemoteException
	 */
	public PageRankVertex(VertexID vertexID, List<Edge> outgoingEdges)
			throws RemoteException {
		super(vertexID, outgoingEdges);
		this.setData(new PageRankData(new Double(0)));
	}

	/**
	 * Represents the overrided compute method
	 * 
	 * @param messageIterator
	 *            Represents the iterator for the incoming messages for this
	 *            vertex
	 * @return Returns the map of outgoing messages from this vertex
	 */
	@Override
	public Map<VertexID, Message> compute(Iterator<Message> messageIterator) {
		Map<VertexID, Message> vertexMessageMap = new HashMap<>();
		int numOutgoingEdges = this.getOutgoingEdges().size();
		PageRankData data = null;
		if (this.getSuperstep() < 30) {
			double sum = 0;
			double updatedRank = 0;
			while (messageIterator.hasNext()) {
				Message message = messageIterator.next();
				data = (PageRankData) message.getData();
				sum += data.getValue();
			}
			updatedRank = (0.15 / numOutgoingEdges + 0.85 * sum);
			((PageRankData) this.getData()).setValue(updatedRank);
			for (Edge edge : this.getOutgoingEdges()) {
				vertexMessageMap.put(edge.getDestID(),
						new Message(this.getID(), new PageRankData(
								((PageRankData) this.getData()).getValue()
										/ numOutgoingEdges)));
			}
		} else {
			// votetohalt
		}
		return vertexMessageMap;
	}
}
