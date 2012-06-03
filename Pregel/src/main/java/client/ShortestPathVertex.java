package client;

import graphs.VertexID;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import system.Message;
import api.Data;
import api.Edge;
import api.Vertex;

public class ShortestPathVertex extends Vertex{

	public ShortestPathVertex(VertexID vertexID, List<Edge> outgoingEdges) {
		super(vertexID, outgoingEdges);		
	}

	@Override
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
