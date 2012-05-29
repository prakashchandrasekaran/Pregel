package api;

import java.util.List;

public class Vertex {

	public String vertexId;
	public List<Edge> outGoingEdges;
	public void setId(String vertexId) {
		this.vertexId = vertexId;
	}

	public void setOutgoingEdges(List<Edge> outGoingEdges) {
		this.outGoingEdges = outGoingEdges;
	}
	
	public String toString() {
		return vertexId + "<->" + outGoingEdges;
	}
	
}
