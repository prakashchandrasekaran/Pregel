package graphs;

import java.io.Serializable;

/**
 * Represents the pair object containing partitionID and vertexIdentifier
 * 
 * @author Prakash Chandrasekaran
 * @author Gautham Narayanasamy
 * @author Vijayaraghavan Subbaiah
 * 
 */
public class VertexID implements Serializable {
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -8636812964597333133L;
	/** Partition identifier */
	private int partitionID;
	/** vertex identifier */
	private long vertexID;

	/**
	 * A Pair Object containing partitionID and vertex identifier
	 * 
	 * @param partitionID
	 *            Represents the partition to which this vertex belongs to
	 * @param vertexID
	 *            Represents the unique vertex Identifier
	 */
	public VertexID(int partitionID, long vertexID) {
		super();
		this.partitionID = partitionID;
		this.vertexID = vertexID;
	}

	/**
	 * Represents the hashCode of this java object
	 */
	public int hashCode() {
		int vertexHash = new Long(vertexID).hashCode();
		return (partitionID + vertexHash) * vertexHash + partitionID;
	}

	/**
	 * Overrides the java object's equal method
	 */
	@Override
	public boolean equals(Object other) {
		if (other instanceof VertexID) {
			VertexID otherPair = (VertexID) other;
			return ((this.partitionID == otherPair.partitionID) && (this.vertexID == otherPair.vertexID));
		}
		return false;
	}

	/**
	 * Overrides the java Object's toString() method
	 */
	@Override
	public String toString() {
		return "(" + partitionID + ", " + vertexID + ")";
	}

	/**
	 * Gets the partition ID of this vertex
	 * 
	 * @return Returns the partition ID of this vertex
	 */
	public int getPartitionID() {
		return partitionID;
	}

	/**
	 * Gets the unique vertexIdentifier
	 * 
	 * @return Returns the unique vertexIdentifier
	 */
	public long getVertexID() {
		return vertexID;
	}

}
