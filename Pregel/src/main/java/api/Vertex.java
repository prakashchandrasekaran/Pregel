package api;

import graphs.VertexID;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import system.Edge;
import system.Message;

/**
 * Represents the pair object containing partitionID and vertexIdentifier
 * 
 * @author Prakash Chandrasekaran
 * @author Gautham Narayanasamy
 * @author Vijayaraghavan Subbaiah
 * 
 */
public abstract class Vertex implements Serializable {
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 2036651815090314092L;
	/** Represents the vertex identifier */
	private VertexID vertexID;
	/** Represents the list of outgoing edges for this vertex */
	private List<Edge> outgoingEdges;
	/** Represents the data of the vertex */
	private Data<?> data;
	/** Represents the current superstep */
	private long superstep;

	/**
	 * Constructs the vertex
	 * 
	 * @param vertexID
	 *            Represents the pair object containing partitionID and
	 *            vertexIdentifier
	 * @param outgoingEdges
	 *            Represents the list of outgoing edges from the source vertex
	 */
	protected Vertex(VertexID vertexID, List<Edge> outgoingEdges)
			throws RemoteException {
		this.vertexID = vertexID;
		this.outgoingEdges = outgoingEdges;
	}

	/**
	 * Gets the vertex identifier
	 * 
	 * @return Returns the vertex identifier
	 */
	public VertexID getID() {
		return vertexID;
	}

	/**
	 * Gets the list of outgoing edges for this source vertex
	 * 
	 * @return Returns the list of outgoing edges for this source vertex
	 */
	public List<Edge> getOutgoingEdges() {
		return outgoingEdges;
	}

	/**
	 * returns String representation of the Vertex
	 */
	public String toString() {
		return "(" + vertexID + "{" + data + "}" + "-" + outgoingEdges + ")";
	}

	/**
	 * abstract compute method, When a vertex is active, it executes it compute
	 * method by taking all input messages and sends message to all its outgoing
	 * edges
	 * 
	 * @param iterator
	 *            , iterator of messages
	 * @return
	 */
	public abstract Map<VertexID, Message> compute(Iterator<Message> iterator)
			throws RemoteException;

	/**
	 * gets Data associated with the vertex
	 * 
	 * @return Returns Data associated with the vertex
	 */
	public Data<?> getData() {
		return data;
	}

	/**
	 * sets Data associated with the Vertex
	 * 
	 * @param data
	 *            Data associated with the Vertex
	 */
	public void setData(Data<?> data) {
		this.data = data;
	}

	/**
	 * Gets SuperStep in which the Vertex is executing
	 * 
	 * @return Returns SuperStep in which the Vertex is executing
	 */
	public long getSuperstep() {
		return superstep;
	}

	/**
	 * sets the SuperStep value for current Vertex
	 * 
	 * @param superstepCounter
	 *            the SuperStep value for current Vertex
	 */
	public void setSuperstep(long superstepCounter) {
		this.superstep = superstepCounter;
	}
}
