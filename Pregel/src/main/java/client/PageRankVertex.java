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

public class PageRankVertex extends Vertex<Double>{

	private static final long serialVersionUID = 3545610632519357452L;
	
	/**
	 * Instantiates a new PageRank vertex.
	 *
	 * @param vertexID the vertex id
	 * @param outgoingEdges the outgoing edges
	 */
	public PageRankVertex(VertexID vertexID, List<Edge> outgoingEdges) {
		super(vertexID, outgoingEdges);		
	}
/*
	@Override
	public Map<VertexID, Message<Double>> compute(
			Iterator<Message<Double>> messageIterator) {
		Map<VertexID, Message<Double>> vertexMessageMap = new HashMap<>();
		int numOutgoingEdges = this.getOutgoingEdges().size();
		if(this.getSuperstepCounter() >= 1)
		{
			double sum = 0;
			double updatedRank = 0;
			while (messageIterator.hasNext()) {
				Message<Double> message = messageIterator.next();
				PageRankData data = (PageRankData) message.getData();
				sum += data.getValue();
				updatedRank = (0.15 / numOutgoingEdges + 0.85 * sum);
				data.setValue(updatedRank);
			}
			
		}
		if(this.getSuperstepCounter() < 30)
		{
			for (Edge edge : this.getOutgoingEdges()) {
				vertexMessageMap.put(edge.getDestID(), 
						new Message<>(this.getID(), new PageRankData(this.getData().getValue() / numOutgoingEdges)));
			}
		}
		else
		{
			// votetohalt
		}
		return vertexMessageMap;
	}
*/
	@Override
	public Map<VertexID, Message<?>> compute(Iterator<Message<?>> iterator) {
		// TODO Auto-generated method stub
		return null;
	}

}
