package api;

import java.util.List;

public class Vertex {

	private Long vertexId;
	private List<Edge> outgoingEdges;
	
	public Vertex(Long vertexId, List<Edge> outgoingEdges) {
		this.vertexId = vertexId;
		this.outgoingEdges = outgoingEdges;
	}
	
	public Long getId() {
		return vertexId;
	}

	public List<Edge> getOutgoingEdges() {
		return outgoingEdges;
	}
	
	public String toString() {
		return "(" + vertexId + "-" + outgoingEdges + ")";
	}
	
}
