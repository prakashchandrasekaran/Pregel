package system;

import graphs.VertexID;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import api.Vertex;
import api.Partition;

/**
 * Represents the computation node
 * 
 * @author Prakash Chandrasekaran
 * @author Gautham Narayanasamy
 * @author Vijayaraghavan Subbaiah
 **/

public class Worker extends UnicastRemoteObject {

	private static final long serialVersionUID = -8137628519082382850L;
	private int numThreads;
	
	
	/** */
	private BlockingQueue<Partition> partitionQueue;
	
	/** */
	private Queue<Partition> completedPartitions;
	
	/** Hostname of the node with timestamp information*/
	private String workerID;
	
	/** Master Proxy object to interact with Master*/
	private Worker2Master masterProxy;
	
	/** PartitionID to WorkerID Map*/
	private Map<Integer, String> mapPartitionIdToWorkerId;
	
	/** Worker2WorkerProxy Object*/
	private Worker2WorkerProxy worker2WorkerProxy;
	
	/** Worker to Outgoing Messages Map*/
	private Map<Worker, Map<VertexID, List<Message>>> outgoingMessages;
	
	/** partitionId to Previous Incoming messages - Used in current Super Step*/
	private Map<Integer, Map<VertexID, List<Message>>> previousIncomingMessages;
	
	/** partitionId to Current Incoming messages - used in next Super Step*/
	private Map<Integer, Map<VertexID, List<Message>>> currentIncomingMessages;

	/** boolean variable indicating whether the partitions can be worked upon by the workers in each superstep. **/
	boolean startSuperStep = false;
	public Worker() throws RemoteException {
		InetAddress address = null;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYMMMdd.HHmmss.SSS");
		String timestamp = simpleDateFormat.format(new Date());

		String hostName = new String();
		try {
			address = InetAddress.getLocalHost();
			hostName = address.getHostName();
		} catch (UnknownHostException e) {
			hostName = "UnKnownHost";
			e.printStackTrace();
		} 

		this.workerID = hostName + "_" + timestamp;
		this.partitionQueue = new LinkedBlockingDeque<>();
		this.completedPartitions = new LinkedList<>();
		this.numThreads = Runtime.getRuntime().availableProcessors();
		for (int i = 0; i < numThreads; i++) {
			WorkerThread workerThread = new WorkerThread();
			workerThread.start();
		}
	}

	/**
	 * Adds the partition to be assigned to the worker.
	 * 
	 * @param partition
	 *            the partition to be assigned
	 */
	public void addPartition(Partition partition) throws RemoteException {
		this.partitionQueue.add(partition);
	}

	public void addPartitionList(List<Partition> workerPartitions)
			throws RemoteException {
		this.partitionQueue.addAll(workerPartitions);
	}

	public int getNumThreads() {
		return numThreads;
	}

	public String getWorkerID() {
		return workerID;
	}

	private class WorkerThread extends Thread {
		@Override
		public void run() {
			while (startSuperStep) {
				try {
					Partition partition = partitionQueue.take();
					Map<VertexID, List<Message>> messageForThisPartition = previousIncomingMessages.get(partition.getPartitionID());
					for(Entry<VertexID, List<Message>> entry : messageForThisPartition.entrySet()) {
						Vertex vertex = partition.getVertex(entry.getKey());
						vertex.compute(entry.getValue().iterator());
					}
					completedPartitions.add(partition);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void setWorkerPartitionInfo(Map<Integer, String> mapPartitionIdToWorkerId,
			Map<String, Worker> mapWorkerIdToWorker) {
		this.mapPartitionIdToWorkerId = mapPartitionIdToWorkerId;
		this.worker2WorkerProxy = new Worker2WorkerProxy(mapWorkerIdToWorker);
		startSuperStep = true;
	}

	public static void main(String[] args) throws Exception {
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
		try {
			String masterMachineName = args[0];
			Registry registry = LocateRegistry.getRegistry(masterMachineName);
			Worker2Master worker2Master = (Worker2Master) registry
					.lookup(Worker2Master.SERVICE_NAME);
			Worker worker = new Worker();
			Worker2Master masterProxy = worker2Master.register(worker,
					worker.getWorkerID(), worker.getNumThreads());			
			worker.setMasterProxy(masterProxy);
			System.out.println("Worker is bound and ready for computations ");
		} catch (Exception e) {
			System.err.println("ComputeEngine exception:");
			e.printStackTrace();
		}
	}

	private void setMasterProxy(Worker2Master masterProxy) {
		this.masterProxy = masterProxy;
	}

	public void receiveMessage(Map<VertexID, List<Message>> incomingMessages) {
		Map<VertexID, List<Message>> partitionMessages = null;
		int partitionID = 0;
		VertexID vertexID = null;
		for(Entry<VertexID, List<Message>> entry : incomingMessages.entrySet()) {
			vertexID = entry.getKey();
			partitionID = vertexID.getPartitionID(); 
			partitionMessages = currentIncomingMessages.get(partitionID);
			if(partitionMessages.containsKey(vertexID))
			{
				partitionMessages.get(vertexID).addAll(entry.getValue());
			}
			else
			{
				partitionMessages.put(vertexID, entry.getValue());
			}
		}
	}
}
