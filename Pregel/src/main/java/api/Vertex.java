package api;

import java.util.List;

public class Vertex {

	private String vertexId;
	private List<Edge> outGoingEdges;
	
	public Vertex(String vertexId, List<Edge> outGoingEdges) {
		this.vertexId = vertexId;
		this.outGoingEdges = outGoingEdges;
	}
	
	public String getId() {
		return vertexId;
	}

	public List<Edge> getOutgoingEdges() {
		return outGoingEdges;
	}
	
	public String toString() {
		return "(" + vertexId + "-" + outGoingEdges + ")";
	}
	
}
