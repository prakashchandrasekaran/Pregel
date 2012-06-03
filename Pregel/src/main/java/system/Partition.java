package system;

import graphs.VertexID;
import java.util.*;

import api.Vertex;

/**
 * Represents the partition of a graph consisting of set of vertices and their
 * outgoing edges
 * 
 * @author Prakash Chandrasekaran
 * @author Gautham Narayanasamy
 * @author Vijayaraghavan Subbaiah
 * 
 */

public class Partition {
	private Map<VertexID, Vertex> vertexMap;
	int partitionID;

	/**
	 * @param vertexList
	 *            Represents the list of vertices in a partition
	 */
	public Partition(int partitionID, Map<VertexID, Vertex> vertexMap) {
		this.partitionID = partitionID;
		this.vertexMap = vertexMap;
	}

	public int getPartitionID() {
		return partitionID;
	}

	public void setPartitionID(int partitionID) {
		this.partitionID = partitionID;
	}

	public Vertex getVertex(VertexID vertexID) {
		return vertexMap.get(vertexID);
	}

}
