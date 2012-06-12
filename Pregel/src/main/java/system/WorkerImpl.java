package system;

import exceptions.PropertyNotFoundException;
import graphs.VertexID;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

import utility.GeneralUtils;
import utility.Props;
import api.Vertex;

/**
 * Represents the computation node.
 *
 * @author Prakash Chandrasekaran
 * @author Gautham Narayanasamy
 * @author Vijayaraghavan Subbaiah
 */

public class WorkerImpl extends UnicastRemoteObject implements Worker {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -8137628519082382850L;
	
	/** The num threads. */
	private int numThreads;

	/** The total partitions assigned. */
	private int totalPartitionsAssigned;
	
	/** boolean variable to determine if a Worker can send messages to other Workers and to Master. 
	 *  It is set to true when a Worker is sending messages to other Workers.
	 */
	private boolean stopSendingMessage;
	
	/** The queue of partitions in the current superstep. */
	private BlockingQueue<Partition> currentPartitionQueue;

	/** The queue of partitions in the next superstep. */
	private BlockingQueue<Partition> nextPartitionQueue;

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
	private long superstep = 0;

	/** The checkpoint file that points to the latest checkpoint */
	private String nextCheckpointFile;
	
	/** The checkpoint file that points to the last successful checkpoint (among all Workers). */
	private String currentCheckpointFile;
	
	/** The CHECKPOINTIN g_ directory. */
	private static String CHECKPOINTING_DIRECTORY;
	
	static {
		try {
			CHECKPOINTING_DIRECTORY = Props.getInstance().getStringProperty("CHECKPOINT_DIR");
		} catch (PropertyNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Instantiates a new worker.
	 *
	 * @throws RemoteException the remote exception
	 */
	public WorkerImpl() throws RemoteException {
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
		this.currentPartitionQueue = new LinkedBlockingDeque<>();
		this.nextPartitionQueue = new LinkedBlockingQueue<>();
		this.currentIncomingMessages = new ConcurrentHashMap<>();
		this.previousIncomingMessages = new ConcurrentHashMap<>();
		this.outgoingMessages = new ConcurrentHashMap<>();
		this.numThreads = Runtime.getRuntime().availableProcessors();
		this.stopSendingMessage = false;
		for (int i = 0; i < numThreads; i++) {
			System.out.println("Starting thread " + (i + 1));
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
		this.nextPartitionQueue.add(partition);
	}

	/**
	 * Adds the partition list.
	 *
	 * @param workerPartitions the worker partitions
	 * @throws RemoteException the remote exception
	 */
	public void addPartitionList(List<Partition> workerPartitions)
			throws RemoteException {
		this.nextPartitionQueue.addAll(workerPartitions);
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
				try {
					Thread.sleep(500);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				// System.out.println(this + "startSuperStep: " + startSuperStep );
				while (startSuperStep) {
					//System.out.println(this + "Superstep loop started for superstep " + superstep);
					try {
						Partition partition = currentPartitionQueue.take();
						//System.out.println(this + " - Partition taken from queue. superstep:" + superstep);
						// System.out.println(this + "previousIncomingMessages size: " + previousIncomingMessages.size());
						Map<VertexID, List<Message>> messageForThisPartition = previousIncomingMessages
								.get(partition.getPartitionID());
						if(messageForThisPartition != null){
							Map<VertexID, Message> messagesFromCompute = null;
							for (Entry<VertexID, List<Message>> entry : messageForThisPartition
									.entrySet()) {
								Vertex vertex = partition.getVertex(entry.getKey());
								
								vertex.setSuperstep(superstep);
								messagesFromCompute = vertex.compute(entry.getValue()
										.iterator());
								updateOutgoingMessages(messagesFromCompute);
							}
						}
						nextPartitionQueue.add(partition);						
						checkAndSendMessage();
						
					} catch (InterruptedException | RemoteException e) {
						e.printStackTrace();						
					}
					catch(Exception e){
						e.printStackTrace();
					}
				}
			}
			// System.out.println(this + " stopped.");
		}

		/**
		 * Check and send message.
		 *
		 */
		private synchronized void checkAndSendMessage() {
			//System.out.println(this + "sendingMessage: " + sendingMessage + " - completedPartitions: " + completedPartitions.size() + " - totalPartitionsAssigned: " + totalPartitionsAssigned);
			if(!stopSendingMessage && (nextPartitionQueue.size() == totalPartitionsAssigned)) {
				stopSendingMessage = true;
				System.out.println(this + " WorkerImpl: Superstep " + superstep + " completed.");
				startSuperStep = false;
				for(Entry<String, Map<VertexID, List<Message>>> entry : outgoingMessages.entrySet()) {
					try {
						worker2WorkerProxy.sendMessage(entry.getKey(), entry.getValue());
					} catch (RemoteException e) {
						e.printStackTrace();
						
					}
				}
				
				// This worker will be active only if it has some messages queued up in the next superstep.
				// activeWorkerSet will have all the workers who will be active in the next superstep.
				Set<String> activeWorkerSet = new HashSet<String>();
				activeWorkerSet.addAll(outgoingMessages.keySet());
				if(currentIncomingMessages.size() > 0){
					activeWorkerSet.add(workerID);
				}
				// Send a message to the Master saying that this superstep has been completed.
				try {
					masterProxy.superStepCompleted(workerID, activeWorkerSet);					
				} catch (RemoteException e) {
					e.printStackTrace();					
				}
								
			}
			// System.out.println(this + " after sendMessage check " + sendingMessage);
			
		}
	}
	
	/**
	 * Halts the run for this application and prints the output in a file.
	 *
	 * @throws RemoteException the remote exception
	 */
	public void halt() throws RemoteException
	{
		System.out.println("Worker Machine " + workerID + " halts");
		this.restoreInitialState();		
	}
	
	/**
	 * Restore initial state.
	 */
	private void restoreInitialState(){
		this.nextPartitionQueue.clear();
		this.currentIncomingMessages.clear();
		this.outgoingMessages.clear();
		this.mapPartitionIdToWorkerId.clear();
		this.currentPartitionQueue.clear();
		this.previousIncomingMessages.clear();
		this.stopSendingMessage = false;
		this.startSuperStep = false;
		this.totalPartitionsAssigned = 0;		
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
	 * @throws RemoteException the remote exception
	 */
	public void setWorkerPartitionInfo(
			int totalPartitionsAssigned,
			Map<Integer, String> mapPartitionIdToWorkerId,
			Map<String, Worker> mapWorkerIdToWorker) throws RemoteException {
		System.out.println("WorkerImpl: setWorkerPartitionInfo");
		System.out.println("totalPartitionsAssigned " + totalPartitionsAssigned + " mapPartitionIdToWorkerId: " + mapPartitionIdToWorkerId);
		this.totalPartitionsAssigned = totalPartitionsAssigned;
		this.mapPartitionIdToWorkerId = mapPartitionIdToWorkerId;
		this.worker2WorkerProxy = new Worker2WorkerProxy(mapWorkerIdToWorker);
		/*
		 * 
		 */
		// startSuperStep = true;
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
			System.out.println("masterMachineName " + masterMachineName);
			
			String masterURL = "//" + masterMachineName + "/" + Worker2Master.SERVICE_NAME;
//			Registry registry = LocateRegistry.getRegistry(masterMachineName);
//			Worker2Master worker2Master = (Worker2Master) registry
//					.lookup(Worker2Master.SERVICE_NAME);
			Worker2Master worker2Master = (Worker2Master) Naming.lookup(masterURL);
			Worker worker = new WorkerImpl();
			//System.out.println("here " + worker2Master.getClass());
			Worker2Master masterProxy = worker2Master.register(worker,
					worker.getWorkerID(), worker.getNumThreads());
			// Worker2Master masterProxy = (Worker2Master) worker2Master.register(null, null, 0);
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
	public void setMasterProxy(Worker2Master masterProxy) {
		this.masterProxy = masterProxy;
	}

	/**
	 * Receive message.
	 *
	 * @param incomingMessages the incoming messages
	 * @throws RemoteException the remote exception
	 */
	public void receiveMessage(Map<VertexID, List<Message>> incomingMessages) throws RemoteException {
		Map<VertexID, List<Message>> partitionMessages = null;
		int partitionID = 0;
		VertexID vertexID = null;
		List<Message> messageList = null;
		Map<VertexID, List<Message>> vertexMessageMap = null;
		for (Entry<VertexID, List<Message>> entry : incomingMessages.entrySet()) {
			vertexID = entry.getKey();
			messageList = entry.getValue();
			partitionID = vertexID.getPartitionID();
			if(currentIncomingMessages.containsKey(partitionID))
			{
				partitionMessages = currentIncomingMessages.get(partitionID);
				if (partitionMessages.containsKey(vertexID)) {
					partitionMessages.get(vertexID).addAll(messageList);
				} else {
					partitionMessages.put(vertexID, messageList);
				}
			}
			else
			{
				vertexMessageMap = new HashMap<>();
				vertexMessageMap.put(vertexID, messageList);
				currentIncomingMessages.put(partitionID, vertexMessageMap);
			}
			
		}
	}

	/**
	 * Receives the messages sent by all the vertices in the same node and
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
		//partitionMessages = currentIncomingMessages.get(partitionID);
		if(currentIncomingMessages.containsKey(partitionID))
		{
			partitionMessages = currentIncomingMessages.get(partitionID);
			if (partitionMessages.containsKey(destinationVertex)) {
				partitionMessages.get(destinationVertex).add(incomingMessage);
			} else {
				newMessageList = new ArrayList<Message>();
				newMessageList.add(incomingMessage);
				partitionMessages.put(destinationVertex, newMessageList);
			}
		}
		else
		{
			partitionMessages = new HashMap<>();
			newMessageList = new ArrayList<>();
			newMessageList.add(incomingMessage);
			partitionMessages.put(destinationVertex, newMessageList);
			currentIncomingMessages.put(partitionID, partitionMessages);
		}
	}
	
	
	/**
	 * The worker receives the command to start the next superstep from the master.
	 * Set startSuperStep to true; assign previousIncomingMessages to currentIncomingMessages; reset currentIncomingMessages;
	 *
	 * @param superStepCounter the super step counter
	 */
	public void startSuperStep(long superStepCounter){
		// System.out.println("WorkerImpl: startSuperStep - superStepCounter: " + superStepCounter);
		this.superstep = superStepCounter;
		// Put all elements in current incoming queue to previous incoming queue and clear the current incoming queue.
		this.previousIncomingMessages.clear();
		this.previousIncomingMessages.putAll(this.currentIncomingMessages);
		this.currentIncomingMessages.clear();
		
		this.stopSendingMessage = false;
		this.startSuperStep = true;
		
		this.outgoingMessages.clear();
		// Put all elements in completed partitions back to partition queue and clear the completed partitions.
		// Note: To avoid concurrency issues, it is very important that completed partitions is cleared before the Worker threads start to operate on the partition queue in the next superstep 
		BlockingQueue<Partition> temp = new LinkedBlockingDeque<>(nextPartitionQueue);
		this.nextPartitionQueue.clear();
		this.currentPartitionQueue.addAll(temp);
		
		// System.out.println("Partition queue: " + partitionQueue.size());
	}
	
	/**
	 * Sets the initial message for the Worker that has the source vertex.
	 *
	 * @param initialMessage the initial message
	 * @throws RemoteException the remote exception
	 */
	public void setInitialMessage(ConcurrentHashMap<Integer, Map<VertexID, List<Message>>> initialMessage) throws RemoteException{
		this.currentIncomingMessages = initialMessage;
	}

	/* (non-Javadoc)
	 * @see system.Worker#checkPoint(long)
	 */
	@Override
	public void checkPoint(long superstep) throws Exception{
		System.out.println("WorkerImpl: checkPoint " + superstep);
		this.superstep = superstep;
		WorkerData wd = new WorkerData(
				this.nextPartitionQueue, 
				this.currentIncomingMessages
				);
		// Serialization
		
		// Don't update the currentCheckpointFile until the Master confirms that the checkpointing had succeeded in all the Workers.
		this.nextCheckpointFile = CHECKPOINTING_DIRECTORY + File.separator + workerID + "_" + superstep;
		// String newFilePath = CHECKPOINTING_DIRECTORY + File.separator + workerID;
		GeneralUtils.serialize(this.nextCheckpointFile, wd);
		//nextCheckpointFile = tmpFilePath;
		// GeneralUtils.renameFile(tmpFilePath, newFilePath);
	}

	
	/**
	 * Master checks the heart beat of the worker by calling this method.
	 *
	 * @throws RemoteException the remote exception
	 */
	@Override
	public void sendHeartBeat() throws RemoteException{
	}

	/**
	 * Method to prepare the worker.
	 *
	 * @throws RemoteException the remote exception
	 */
	@Override
	public void startRecovery() throws RemoteException{
		System.out.println("WorkerImpl: startRecovery");
		this.stopSendingMessage = false;
		this.startSuperStep = false;
		this.currentPartitionQueue.clear();
		this.previousIncomingMessages.clear();
		this.outgoingMessages.clear();
		
		WorkerData workerData;
		// String checkpointDir;
		// checkpointDir = Props.getInstance().getStringProperty("CHECKPOINT_DIR");
        // String workerStateFile = checkpointDir + File.separator + workerID;
		workerData = (WorkerData)GeneralUtils.deserialize(this.currentCheckpointFile);
		this.currentIncomingMessages = (ConcurrentHashMap<Integer, Map<VertexID, List<Message>>>)workerData.getMessages();
		this.nextPartitionQueue = (BlockingQueue<Partition>)workerData.getPartitions();
		// System.out.println("Restoring checkpointed data " + this.nextPartitionQueue);			
	
				
	}

	/* (non-Javadoc)
	 * @see system.Worker#finishRecovery()
	 */
	@Override
	public void finishRecovery() throws RemoteException {
		System.out.println("WorkerImpl: finishRecovery");
		try {
			 checkPoint(this.masterProxy.getCheckpointedSuperstep());
		} catch (Exception e) {
			System.out.println("checkpoint failure");
			throw new RemoteException();
		}
	}

	/* (non-Javadoc)
	 * @see system.Worker#addRecoveredData(system.Partition, java.util.Map)
	 */
	public void addRecoveredData(Partition partition, Map<VertexID, List<Message>> messages) throws RemoteException {
		System.out.println("WorkerImpl: addRecoveredData");
//		System.out.println("Partition " + partition.getPartitionID());
//		System.out.println("Messages: " + messages);
		if(messages != null){
			this.currentIncomingMessages.put(partition.getPartitionID(), messages);
		}
		this.nextPartitionQueue.add(partition);		
	}


	@Override
	public void shutdown() throws RemoteException {
			java.util.Date date = new java.util.Date();
			System.out.println("Worker" + workerID + " goes down now at :"
					+ new Timestamp(date.getTime()));
			System.exit(0);
	}
	
	public void writeOutput(String outputFilePath) throws RemoteException{
		System.out.println("Printing the final state of the partitions");		
		Iterator<Partition> iter = nextPartitionQueue.iterator();
		// Append the appropriate content to the output file.
		StringBuilder contents = new StringBuilder();
		while(iter.hasNext()){
			contents.append(iter.next());			
		}
		GeneralUtils.writeToFile(outputFilePath, contents.toString(), true);
	}

	/* (non-Javadoc)
	 * @see system.Worker#updateCheckpointFile()
	 */
	@Override
	public void updateCheckpointFile() throws RemoteException {		
		this.currentCheckpointFile = this.nextCheckpointFile;
		System.out.println("WorkerImpl: current checkpoint file: " + this.currentCheckpointFile);
	}
}