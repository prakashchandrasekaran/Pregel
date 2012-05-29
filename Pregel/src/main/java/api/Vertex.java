package api;

import java.util.List;

public class Vertex {

	private String vertexId;
	private List<Edge> outgoingEdges;
	
	public Vertex(String vertexId, List<Edge> outgoingEdges) {
		this.vertexId = vertexId;
		this.outgoingEdges = outgoingEdges;
	}
	
	public String getId() {
		return vertexId;
	}

	public List<Edge> getOutgoingEdges() {
		return outgoingEdges;
	}
	
	public String toString() {
		return "(" + vertexId + "-" + outgoingEdges + ")";
	}
	
}
