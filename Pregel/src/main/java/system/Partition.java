package system;

import graphs.VertexID;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.Map.Entry;

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

public class Partition implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7212204736364464061L;
	private Map<VertexID, Vertex> vertexMap;
	int partitionID;

	public Partition() throws RemoteException{
		
	}
	/**
	 * @param vertexList
	 *            Represents the list of vertices in a partition
	 */
	public Partition(int partitionID, Map<VertexID, Vertex> vertexMap) throws RemoteException{
		this.partitionID = partitionID;
		this.vertexMap = vertexMap;
	}

	public int getPartitionID(){
		return partitionID;
	}

	public void setPartitionID(int partitionID) throws RemoteException{
		this.partitionID = partitionID;
	}

	public Vertex getVertex(VertexID vertexID) throws RemoteException{
		return vertexMap.get(vertexID);
	}

	public String toString() {
		String result = "Partition ID :" + this.partitionID +"\n";
		for (Entry<VertexID, Vertex> entry : vertexMap.entrySet()) 
				result += entry.getValue().toString() + "\n";
		return result;
	}
}
