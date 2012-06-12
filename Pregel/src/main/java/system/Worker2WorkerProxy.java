package system;

import graphs.VertexID;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

/**
 * Represents a medium through which workers communicate with other workers
 * 
 * @author Prakash Chandrasekaran
 * @author Gautham Narayanasamy
 * @author Vijayaraghavan Subbaiah
 */
public class Worker2WorkerProxy implements Worker2Worker, Remote {
	/** The workerID to Worker map. **/
	private Map<String, Worker> mapWorkerIdToWorker;

	/**
	 * Constructs the worker2worker proxy
	 * 
	 * @param mapWorkerIdToWorker
	 *            Represents the WorkerID to worker map
	 */
	public Worker2WorkerProxy(Map<String, Worker> mapWorkerIdToWorker)
			throws RemoteException {
		this.mapWorkerIdToWorker = mapWorkerIdToWorker;
	}

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
			throws RemoteException {
		mapWorkerIdToWorker.get(receiverWorkerID).receiveMessage(
				outgoingMessages);
	}
}
