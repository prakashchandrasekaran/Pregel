package system;

import graphs.VertexID;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

public interface Worker2Worker extends java.rmi.Remote {
	public void sendMessage(String receiverWorkerID,
			Map<VertexID, List<Message<?>>> outgoingMessages)
			throws RemoteException;
}