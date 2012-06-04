package system;

import graphs.VertexID;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Map;

public class Worker2WorkerProxy extends UnicastRemoteObject implements Worker2Worker, Remote {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1665144036051268247L;
	private Map<String, Worker> mapWorkerIdToWorker;

	public Worker2WorkerProxy(Map<String, Worker> mapWorkerIdToWorker) throws RemoteException{
		this.mapWorkerIdToWorker = mapWorkerIdToWorker;
	}

	public void sendMessage(String receiverWorkerID,
			Map<VertexID, List<Message>> outgoingMessages)
			throws RemoteException {
		mapWorkerIdToWorker.get(receiverWorkerID).receiveMessage(
				outgoingMessages);
	}
}
