package system;

import graphs.VertexID;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
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
	
	
	/** */
	private int numThreads;

	/** */
	private int totalPartitionsAssigned;
	
	/** flag is true when Worker is sending messages to other workers */
	private boolean sendingMessage;
	
	/** */
	private BlockingQueue<Partition> partitionQueue;

	/** */
	private Queue<Partition> completedPartitions;

	/** Hostname of the node with timestamp information */
	private String workerID;

	/** Master Proxy object to interact with Master */
	private Worker2Master masterProxy;

	/** PartitionID to WorkerID Map */
	private Map<Integer, String> mapPartitionIdToWorkerId;

	/** Worker2WorkerProxy Object */
	private Worker2WorkerProxy worker2WorkerProxy;

	/** Worker to Outgoing Messages Map */
	private ConcurrentHashMap<String, Map<VertexID, List<Message>>> outgoingMessages;

	/** partitionId to Previous Incoming messages - Used in current Super Step */
	private ConcurrentHashMap<Integer, Map<VertexID, List<Message>>> previousIncomingMessages;

	/** partitionId to Current Incoming messages - used in next Super Step */
	private ConcurrentHashMap<Integer, Map<VertexID, List<Message>>> currentIncomingMessages;

	/**
	 * boolean variable indicating whether the partitions can be worked upon by
	 * the workers in each superstep.
	 **/
	private boolean startSuperStep = false;

	public Worker() throws RemoteException {
		InetAddress address = null;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				"YYYMMMdd.HHmmss.SSS");
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
		this.currentIncomingMessages = new ConcurrentHashMap<>();
		this.previousIncomingMessages = new ConcurrentHashMap<>();
		this.outgoingMessages = new ConcurrentHashMap<>();
		this.numThreads = Runtime.getRuntime().availableProcessors();
		this.sendingMessage = false;
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
			while(true) {
				while (startSuperStep) {
					try {
						Partition partition = partitionQueue.take();
						Map<VertexID, List<Message>> messageForThisPartition = previousIncomingMessages
								.get(partition.getPartitionID());
						Map<VertexID, Message> messagesFromCompute = null;
						for (Entry<VertexID, List<Message>> entry : messageForThisPartition
								.entrySet()) {
							Vertex vertex = partition.getVertex(entry.getKey());
							messagesFromCompute = vertex.compute(entry.getValue()
									.iterator());
							updateOutgoingMessages(messagesFromCompute);
						}
						completedPartitions.add(partition);
						checkAndSendMessage();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}

		private synchronized void checkAndSendMessage() {
			if( (! sendingMessage) && (completedPartitions.size() == totalPartitionsAssigned)) {
				sendingMessage = true;
				startSuperStep = false;
				partitionQueue.addAll(completedPartitions);
				completedPartitions.clear();
				for(Entry<String, Map<VertexID, List<Message>>> entry : outgoingMessages.entrySet()) {
					try {
						worker2WorkerProxy.sendMessage(entry.getKey(), entry.getValue());
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}
			}
			// superStepCompleted
			
		}
	}
	
	

	/**
	 * Updates the outgoing messages for every superstep
	 * 
	 * @param messagesFromCompute
	 *            Represents the map of destination vertex and its associated
	 *            message to be send
	 */
	private void updateOutgoingMessages(
			Map<VertexID, Message> messagesFromCompute) {
		String workerID = null;
		VertexID vertexID = null;
		Message message = null;
		Map<VertexID, List<Message>> workerMessages = null;
		ArrayList<Message> messageList = null;
		for (Entry<VertexID, Message> entry : messagesFromCompute.entrySet()) {
			vertexID = entry.getKey();
			message = entry.getValue();
			workerID = mapPartitionIdToWorkerId.get(vertexID.getPartitionID());
			if (workerID.equals(this.workerID)) {
				updateIncomingMessages(vertexID, message);
			} else {
				if (outgoingMessages.containsKey(workerID)) {
					workerMessages = outgoingMessages.get(workerID);
					if (workerMessages.containsKey(vertexID)) {
						workerMessages.get(vertexID).add(message);
					} else {
						messageList = new ArrayList<Message>();
						messageList.add(message);
						workerMessages.put(vertexID, messageList);
					}
				} else {
					messageList = new ArrayList<Message>();
					messageList.add(message);
					workerMessages = new HashMap<>();
					workerMessages.put(vertexID, messageList);
					outgoingMessages.put(workerID, workerMessages);
				}
			}
		}
	}

	public void setWorkerPartitionInfo(
			int totalPartitionsAssigned,
			Map<Integer, String> mapPartitionIdToWorkerId,
			Map<String, Worker> mapWorkerIdToWorker) {
		this.totalPartitionsAssigned = totalPartitionsAssigned;
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
	
	private void superStepCompleted() {
		this.masterProxy.superStepCompleted(this.workerID);
	}

	private void setMasterProxy(Worker2Master masterProxy) {
		this.masterProxy = masterProxy;
	}

	public void receiveMessage(Map<VertexID, List<Message>> incomingMessages) {
		Map<VertexID, List<Message>> partitionMessages = null;
		int partitionID = 0;
		VertexID vertexID = null;
		for (Entry<VertexID, List<Message>> entry : incomingMessages.entrySet()) {
			vertexID = entry.getKey();
			partitionID = vertexID.getPartitionID();
			partitionMessages = currentIncomingMessages.get(partitionID);
			if (partitionMessages.containsKey(vertexID)) {
				partitionMessages.get(vertexID).addAll(entry.getValue());
			} else {
				partitionMessages.put(vertexID, entry.getValue());
			}
		}
	}

	/**
	 * Receives the messages send by all the vertices in the same node and
	 * updates the current incoming message queue
	 * 
	 * @param destinationVertex
	 *            Represents the destination vertex to which the message has to
	 *            be sent
	 * @param incomingMessage
	 *            Represents the incoming message for the destination vertex
	 */
	public void updateIncomingMessages(VertexID destinationVertex,
			Message incomingMessage) {
		Map<VertexID, List<Message>> partitionMessages = null;
		ArrayList<Message> newMessageList = null;
		int partitionID = destinationVertex.getPartitionID();
		partitionMessages = currentIncomingMessages.get(partitionID);
		if (partitionMessages.containsKey(destinationVertex)) {
			partitionMessages.get(destinationVertex).add(incomingMessage);
		} else {
			newMessageList = new ArrayList<Message>();
			newMessageList.add(incomingMessage);
			partitionMessages.put(destinationVertex, newMessageList);
		}
	}

	public boolean isStartSuperStep() {
		return startSuperStep;
	}

	public void setStartSuperStep(boolean startSuperStep) {
		this.startSuperStep = startSuperStep;
	}
	
	
}
