package system;

import java.util.Map;

public class Worker2WorkerProxy {
	private Map<String, Worker> mapWorkerIdToWorker;
	
	public Worker2WorkerProxy(Map<String, Worker> mapWorkerIdToWorker) {
		this.mapWorkerIdToWorker = mapWorkerIdToWorker;
	}
	
	public void send(String receiverWorkerID, Message m) {
		// send message to receiver worker
	}

	
}
