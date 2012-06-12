package system;

import graphs.VertexID;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

/**
 * Interface for the workers to communicate between them
 * 
 * @author Prakash Chandrasekaran
 * @author Gautham Narayanasamy
 * @author Vijayaraghavan Subbaiah
 */
public interface Worker2Worker extends java.rmi.Remote {
	/**
	 * Method to send message to another worker
	 * 
	 * @param receiverWorkerID
	 *            the receiver worker
	 * @param outgoingMessages
	 *            set of messages to be sent to the worker
	 */
	public void sendMessage(String receiverWorkerID,
			Map<VertexID, List<Message>> outgoingMessages)
			throws RemoteException;
}