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
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;

import api.Vertex;

/**
 * Represents the computation node.
 *
 * @author Prakash Chandrasekaran
 * @author Gautham Narayanasamy
 * @author Vijayaraghavan Subbaiah
 */

public class Worker extends UnicastRemoteObject {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -8137628519082382850L;
	
	/** The num threads. */
	private int numThreads;

	/** The total partitions assigned. */
	private int totalPartitionsAssigned;
	
	/** flag is true when Worker is sending messages to other workers. */
	private boolean sendingMessage;
	
	/** The partition queue. */
	private BlockingQueue<Partition> partitionQueue;

	/** The completed partitions. */
	private Queue<Partition> completedPartitions;

	/** Hostname of the node with timestamp information. */
	private String workerID;

	/** Master Proxy object to interact with Master. */
	private Worker2Master masterProxy;

	/** PartitionID to WorkerID Map. */
	private Map<Integer, String> mapPartitionIdToWorkerId;

	/** Worker2WorkerProxy Object. */
	private Worker2WorkerProxy worker2WorkerProxy;

	/** Worker to Outgoing Messages Map. */
	private ConcurrentHashMap<String, Map<VertexID, List<Message>>> outgoingMessages;

	/** partitionId to Previous Incoming messages - Used in current Super Step. */
	private ConcurrentHashMap<Integer, Map<VertexID, List<Message>>> previousIncomingMessages;

	/** partitionId to Current Incoming messages - used in next Super Step. */
	private ConcurrentHashMap<Integer, Map<VertexID, List<Message>>> currentIncomingMessages;

	/**
	 * boolean variable indicating whether the partitions can be worked upon by
	 * the workers in each superstep.
	 **/
	private boolean startSuperStep = false;
	
	/** The super step counter. */
	private long superStepCounter = 0;

	/**
	 * Instantiates a new worker.
	 *
	 * @throws RemoteException the remote exception
	 */
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
	 * @param partition the partition to be assigned
	 * @throws RemoteException the remote exception
	 */
	public void addPartition(Partition partition) throws RemoteException {
		this.partitionQueue.add(partition);
	}

	/**
	 * Adds the partition list.
	 *
	 * @param workerPartitions the worker partitions
	 * @throws RemoteException the remote exception
	 */
	public void addPartitionList(List<Partition> workerPartitions)
			throws RemoteException {
		this.partitionQueue.addAll(workerPartitions);
	}

	/**
	 * Gets the num threads.
	 *
	 * @return the num threads
	 */
	public int getNumThreads() {
		return numThreads;
	}

	/**
	 * Gets the worker id.
	 *
	 * @return the worker id
	 */
	public String getWorkerID() {
		return workerID;
	}

	/**
	 * The Class WorkerThread.
	 */
	private class WorkerThread extends Thread {
		
		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
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
							vertex.setSuperstepCounter(superStepCounter);
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

		/**
		 * Check and send message.
		 */
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
				
				// This worker will be active only if it has some messages queued up in the next superstep.
				// activeWorkerSet will have all the workers who will be active in the next superstep.
				Set<String> activeWorkerSet = outgoingMessages.keySet();
				if(currentIncomingMessages.size() > 0){
					activeWorkerSet.add(workerID);
				}
				// Send a message to the Master saying that this superstep has been completed.
				masterProxy.superStepCompleted(workerID, activeWorkerSet);
			}
			
		}
	}
	
	/**
	 * Updates the outgoing messages for every superstep.
	 *
	 * @param messagesFromCompute Represents the map of destination vertex and its associated
	 * message to be send
	 */
	private void updateOutgoingMessages(
			Map<VertexID, Message> messagesFromCompute) {
		String workerID = null;
		VertexID vertexID = null;
		Message message = null;
		Map<VertexID, List<Message>> workerMessages = null;
		List<Message> messageList = null;
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

	/**
	 * Sets the worker partition info.
	 *
	 * @param totalPartitionsAssigned the total partitions assigned
	 * @param mapPartitionIdToWorkerId the map partition id to worker id
	 * @param mapWorkerIdToWorker the map worker id to worker
	 */
	public void setWorkerPartitionInfo(
			int totalPartitionsAssigned,
			Map<Integer, String> mapPartitionIdToWorkerId,
			Map<String, Worker> mapWorkerIdToWorker) {
		this.totalPartitionsAssigned = totalPartitionsAssigned;
		this.mapPartitionIdToWorkerId = mapPartitionIdToWorkerId;
		this.worker2WorkerProxy = new Worker2WorkerProxy(mapWorkerIdToWorker);
		startSuperStep = true;
	}

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws Exception the exception
	 */
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
	

	/**
	 * Sets the master proxy.
	 *
	 * @param masterProxy the new master proxy
	 */
	private void setMasterProxy(Worker2Master masterProxy) {
		this.masterProxy = masterProxy;
	}

	/**
	 * Receive message.
	 *
	 * @param incomingMessages the incoming messages
	 */
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
	 * updates the current incoming message queue.
	 *
	 * @param destinationVertex Represents the destination vertex to which the message has to
	 * be sent
	 * @param incomingMessage Represents the incoming message for the destination vertex
	 */
	public void updateIncomingMessages(VertexID destinationVertex,
			Message incomingMessage) {
		Map<VertexID, List<Message>> partitionMessages = null;
		List<Message> newMessageList = null;
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
	
	
	/**
	 * The worker receives the command to start the next superstep from the master.
	 * Set startSuperStep to true; assign previousIncomingMessages to currentIncomingMessages; reset currentIncomingMessages;
	 *
	 * @param superStepCounter the super step counter
	 */
	public void startSuperStep(long superStepCounter){		
		this.previousIncomingMessages.clear();
		ConcurrentHashMap<Integer, Map<VertexID, List<Message>>> temp = this.previousIncomingMessages;
		this.previousIncomingMessages = this.currentIncomingMessages;
		this.currentIncomingMessages = temp;
		this.superStepCounter = superStepCounter;
		this.startSuperStep = true;
	}
	
	
	/**
	 *
	 * Sets the initial message for the Worker that has the source vertex.
	 *
	 * @param initialMessage the initial message
	 */
	public void setInitialMessage(ConcurrentHashMap<Integer, Map<VertexID, List<Message>>> initialMessage){
		previousIncomingMessages = initialMessage;
	}
}
