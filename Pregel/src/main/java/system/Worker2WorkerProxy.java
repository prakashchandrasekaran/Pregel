package system;

import java.rmi.RemoteException;
import java.util.*;
import graphs.VertexID;
import java.util.Map;

public class Worker2WorkerProxy implements Worker2Worker {
	private Map<String, Worker> mapWorkerIdToWorker;

	public Worker2WorkerProxy(Map<String, Worker> mapWorkerIdToWorker) {
		this.mapWorkerIdToWorker = mapWorkerIdToWorker;
	}

	public void sendMessage(String receiverWorkerID,
			Map<VertexID, List<Message>> outgoingMessages)
			throws RemoteException {
		mapWorkerIdToWorker.get(receiverWorkerID).receiveMessage(
				outgoingMessages);
	}
}
