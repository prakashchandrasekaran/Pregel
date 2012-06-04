package system;

import graphs.VertexID;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public interface Worker extends Remote{

	public String getWorkerID() throws RemoteException;
	
	public int getNumThreads() throws RemoteException;
	
	public void setMasterProxy(Worker2Master masterProxy) throws RemoteException;
	
	public void addPartition(Partition partition) throws RemoteException;
	
	public void addPartitionList(List<Partition> workerPartitions)
			throws RemoteException;
	
	public void setWorkerPartitionInfo(
			int totalPartitionsAssigned,
			Map<Integer, String> mapPartitionIdToWorkerId,
			Map<String, Worker> mapWorkerIdToWorker) throws RemoteException;
	
	public void halt() throws RemoteException;
	
	public void startSuperStep(long superStepCounter) throws RemoteException;
	
	public void setInitialMessage(ConcurrentHashMap<Integer, Map<VertexID, List<Message>>> initialMessage) throws RemoteException;
	
	public void receiveMessage(Map<VertexID, List<Message>> incomingMessages) throws RemoteException;
	
}
