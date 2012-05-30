package api;

import java.util.List;

public class Vertex {

	private long vertexId;
	private List<Edge> outgoingEdges;
	
	public Vertex(long vertexId, List<Edge> outgoingEdges) {
		this.vertexId = vertexId;
		this.outgoingEdges = outgoingEdges;
	}
	
	public long getId() {
		return vertexId;
	}

	public List<Edge> getOutgoingEdges() {
		return outgoingEdges;
	}
	
	public String toString() {
		return "(" + vertexId + "-" + outgoingEdges + ")";
	}
	
}
