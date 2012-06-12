package system;

import graphs.VertexID;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 * Represents the serialized worker data during checkpoint
 * 
 * @author Prakash Chandrasekaran
 * @author Gautham Narayanasamy
 * @author Vijayaraghavan Subbaiah
 * 
 */
public class WorkerData implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 4779387255673737945L;
	/** Queue of partitions */
	private Queue<Partition> partitions;
	/**
	 * Map of partitionID and its respective Map of VertexID with its associated
	 * messages
	 */
	private Map<Integer, Map<VertexID, List<Message>>> messages;

	/**
	 * Constructs the worker data
	 * 
	 * @param partitions
	 *            Represents the set of vertices
	 * @param messages
	 *            Represents the message
	 */
	public WorkerData(Queue<Partition> partitions,
			Map<Integer, Map<VertexID, List<Message>>> messages) {
		this.partitions = partitions;
		this.messages = messages;
	}

	/**
	 * Gets the queue of partitions
	 * 
	 * @return Returns the queue of partitions
	 */
	public Queue<Partition> getPartitions() {
		return partitions;
	}

	/**
	 * Sets the partition queue
	 * 
	 * @param partitions
	 *            Represents the set of vertices
	 */
	public void setPartitions(Queue<Partition> partitions) {
		this.partitions = partitions;
	}

	/**
	 * Gets the Map of partitionID and its respective Map of VertexID with its
	 * associated messages
	 * 
	 * @return Returns the Map of partitionID and its respective Map of VertexID
	 *         with its associated messages
	 */
	public Map<Integer, Map<VertexID, List<Message>>> getMessages() {
		return messages;
	}

	/**
	 * Sets the messages for the worker
	 * 
	 * @param messages
	 *            Represents the Map of partitionID and its respective Map of
	 *            VertexID with its associated messages
	 */
	public void setMessages(Map<Integer, Map<VertexID, List<Message>>> messages) {
		this.messages = messages;
	}

	/** String representation of the object */
	@Override
	public String toString() {
		return "Partitions:  " + partitions.toString() + "\n" + "Messages: "
				+ messages;
	}
}
