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

public class Partition implements Serializable {
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -7212204736364464061L;
	private Map<VertexID, Vertex> vertexMap;
	private int partitionID;

	public Partition() throws RemoteException {

	}

	/**
	 * Constructs a partition
	 * 
	 * @param partitionID
	 *            Represents the unique id of the partition
	 * @param vertexMap
	 *            Represents mapping between id and its respective vertices
	 * @throws RemoteException
	 */
	public Partition(int partitionID, Map<VertexID, Vertex> vertexMap)
			throws RemoteException {
		this.partitionID = partitionID;
		this.vertexMap = vertexMap;
	}

	/**
	 * Gets the id of the partition
	 * 
	 * @return returns the id of the partition
	 */
	public int getPartitionID() {
		return partitionID;
	}

	/**
	 * Sets the id of the partition
	 * 
	 * @param partitionID
	 *            Represents the unique id of the partition
	 * @throws RemoteException
	 */
	public void setPartitionID(int partitionID) throws RemoteException {
		this.partitionID = partitionID;
	}

	/**
	 * Gets the vertex
	 * 
	 * @param vertexID
	 *            Represents the unique id of the vertex
	 * @return Returns the vertex
	 * @throws RemoteException
	 */
	public Vertex getVertex(VertexID vertexID) throws RemoteException {
		return vertexMap.get(vertexID);
	}

	/**
	 * String Representation of the object
	 */
	public String toString() {
		String result = "Partition ID :" + this.partitionID + "\n";
		for (Entry<VertexID, Vertex> entry : vertexMap.entrySet())
			result += entry.getValue().toString() + "\n";
		return result;
	}
}
