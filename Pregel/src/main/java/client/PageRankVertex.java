package client;

import graphs.VertexID;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import system.Message;
import api.Edge;
import api.Vertex;

/**
 * Defines the Vertex implementation for the PageRank graph problem.
 */

public class PageRankVertex extends Vertex {

	private static final long serialVersionUID = 3545610632519357452L;

	/**
	 * Instantiates a new PageRank vertex.
	 * 
	 * @param vertexID
	 *            the vertex id
	 * @param outgoingEdges
	 *            the outgoing edges
	 */
	public PageRankVertex(VertexID vertexID, List<Edge> outgoingEdges) {
		super(vertexID, outgoingEdges);
	}

	@Override
	public Map<VertexID, Message> compute(Iterator<Message> messageIterator) {
		Map<VertexID, Message> vertexMessageMap = new HashMap<>();
		int numOutgoingEdges = this.getOutgoingEdges().size();
		PageRankData data = null;
		if (this.getSuperstepCounter() < 30) {
			double sum = 0;
			double updatedRank = 0;
			while (messageIterator.hasNext()) {
				Message message = messageIterator.next();
				data = (PageRankData) message.getData();
				sum += data.getValue();
			}
			updatedRank = (0.15 / numOutgoingEdges + 0.85 * sum);
			((PageRankData)this.getData()).setValue(updatedRank);
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
