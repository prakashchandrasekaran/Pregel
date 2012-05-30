package api;

import graphs.VertexID;

/**
 * Represents the edge containing source and destination vertex along with their
 * edge weight
 * 
 * @author Prakash Chandrasekaran
 * @author Gautham Narayanasamy
 * @author Vijayaraghavan Subbaiah
 * 
 */

public class Edge {

	private VertexID sourceID;
	private VertexID destID;
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
}
