package system;
import graphs.VertexID;

import java.rmi.RemoteException;
import java.util.*;
public class Worker2WorkerProxy implements Worker2Worker {
	private Worker receiverWorker; 
	
	public Worker2WorkerProxy(Worker receiverWorker) {
		this.receiverWorker = receiverWorker;
	}
	
	public void sendMessage(Map<VertexID, List<Message>> outgoingMessages) throws RemoteException {
		receiverWorker.receiveMessage(outgoingMessages);
	}

	
}
