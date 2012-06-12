package system;

import graphs.VertexID;

import java.io.Serializable;

/**
 * Represents the edge containing source and destination vertex along with their
 * edge weight
 * 
 * @author Prakash Chandrasekaran
 * @author Gautham Narayanasamy
 * @author Vijayaraghavan Subbaiah
 * 
 */

public class Edge implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 349936200862773459L;
	/** source vertex ID **/
	private VertexID sourceID;
	/** Destination vertex ID **/
	private VertexID destID;
	/** Edge Weight **/
	private double weight;

	/**
	 * @param sourceID
	 *            Represents the source vertex
	 * @param destID
	 *            Represents the destination vertex
	 * @param weight
	 *            Represents the edge weight
	 */
	public Edge(VertexID sourceID, VertexID destID, double weight) {
		this.sourceID = sourceID;
		this.destID = destID;
		this.weight = weight;
	}

	/**
	 * Overrides the default java toString method
	 */
	@Override
	public String toString() {
		return "(" + sourceID + "," + destID + "-" + weight + ")";
	}

	/**
	 * Gets the source vertex identifier
	 * 
	 * @return Returns the source vertex identifier
	 */
	public VertexID getSourceID() {
		return sourceID;
	}

	/**
	 * Gets the destination vertex identifier
	 * 
	 * @return Returns the destination vertex identifier
	 */
	public VertexID getDestID() {
		return destID;
	}

	/**
	 * Gets the edge weight
	 * 
	 * @return Returns the edge weight
	 */
	public double getEdgeWeight() {
		return this.weight;
	}
}
