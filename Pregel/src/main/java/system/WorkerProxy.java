package system;

import graphs.VertexID;

import java.rmi.AccessException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Represents a thread which is used by the master to talk to workers and
 * vice-versa.
 * 
 * @author Prakash Chandrasekaran
 * @author Gautham Narayanasamy
 * @author Vijayaraghavan Subbaiah
 */

public class WorkerProxy implements Runnable, Worker2Master {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -2001231189089230248L;

	/** The worker. */
	private Worker worker;

	/** The master. */
	private Master master;

	/** The t. */
	private Thread t;

	/** The num worker threads. */
	private int numWorkerThreads;

	/** The worker id. */
	String workerID;

	/** The partition list. */
	BlockingQueue<Partition> partitionList;

	/** The total partitions. */
	private int totalPartitions = 0;

	/**
	 * Instantiates a new worker proxy.
	 * 
	 * @param worker
	 *            Represents the remote {@link system.WorkerImpl Worker}
	 * @param workerID
	 *            Represents the unique serviceName to identify the worker
	 * @param numWorkerThreads
	 *            the num worker threads
	 * @param master
	 *            Represents the {@link system.Master Master}
	 * @throws AccessException
	 *             the access exception
	 * @throws RemoteException
	 *             the remote exception
	 */

	public WorkerProxy(Worker worker, String workerID, int numWorkerThreads,
			Master master) throws AccessException, RemoteException {
		this.worker = worker;
		this.workerID = workerID;
		this.numWorkerThreads = numWorkerThreads;
		this.master = master;
		partitionList = new LinkedBlockingQueue<>();
		t = new Thread(this);
		t.start();
	}

	/**
	 * Represents a thread which removes {@link api.Task tasks} from a queue,
	 * invoking the associated {@link system.Computer Computer's} execute method
	 * with the task as its argument, and putting the returned
	 * {@link api.Result Result} back into the {@link api.Space Space} for
	 * retrieval by the client
	 */

	@Override
	public void run() {
		Partition partition = null;
		while (true) {
			try {
				partition = partitionList.take();
				System.out.println("Partition taken");
				worker.addPartition(partition);
			} catch (RemoteException e) {
				System.out.println("Remote Exception received from the Worker");
				System.out.println("Giving back the partition to the Master ");
				master.removeWorker(workerID);
				return;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Adds the partition.
	 * 
	 * @param partition
	 *            the partition
	 */
	public void addPartition(Partition partition) {

		totalPartitions += 1;
		partitionList.add(partition);
	}

	public void exit() {
		try {
			t.interrupt();
		} catch (Exception e) {
			System.out.println("Worker Stopped");
		}
	}

	/**
	 * Gets the num threads.
	 * 
	 * @return the num threads
	 */
	public int getNumThreads() {
		return numWorkerThreads;
	}

	/**
	 * Halts the worker and prints the final solution
	 */
	public void halt() throws RemoteException {
		this.restoreInitialState();
		worker.halt();
	}

	/**
	 * Adds the partition list.
	 * 
	 * @param workerPartitions
	 *            the worker partitions
	 */
	public void addPartitionList(List<Partition> workerPartitions) {
		System.out.println("WorkerProxy: addPartitionList");
		try {
			totalPartitions += workerPartitions.size();
			worker.addPartitionList(workerPartitions);
		} catch (RemoteException e) {
			e.printStackTrace();
			System.out.println("Remote Exception received from the Worker");
			System.out.println("Giving back the partition to the Master ");
			master.removeWorker(workerID);
			// give the partition back to Master
			return;
		}
	}

	/**
	 * Sets the worker partition info.
	 * 
	 * @param mapPartitionIdToWorkerId
	 *            the map partition id to worker id
	 * @param mapWorkerIdToWorker
	 *            the map worker id to worker
	 * @throws RemoteException
	 */
	public void setWorkerPartitionInfo(
			Map<Integer, String> mapPartitionIdToWorkerId,
			Map<String, Worker> mapWorkerIdToWorker) throws RemoteException {
		worker.setWorkerPartitionInfo(totalPartitions,
				mapPartitionIdToWorkerId, mapWorkerIdToWorker);
	}

	/**
	 * Gets the worker id.
	 * 
	 * @return the worker id
	 */
	public String getWorkerID() {
		return workerID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see system.Worker2Master#register(system.Worker, java.lang.String, int)
	 */
	@Override
	public Worker2Master register(Worker worker, String workerID,
			int numWorkerThreads) throws RemoteException {

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see system.Worker2Master#superStepCompleted(java.lang.String,
	 * java.util.Set)
	 */
	@Override
	public void superStepCompleted(String workerID, Set<String> activeWorkerSet)
			throws RemoteException {
		master.superStepCompleted(workerID, activeWorkerSet);
	}

	/**
	 * Start super step.
	 * 
	 * @param superStepCounter
	 *            the super step counter
	 */
	public void startSuperStep(long superStepCounter) throws RemoteException {
		this.worker.startSuperStep(superStepCounter);
	}

	/**
	 * Sets the initial message for the Worker that has the source vertex.
	 * 
	 * @param initialMessage
	 *            the initial message
	 */
	public void setInitialMessage(
			ConcurrentHashMap<Integer, Map<VertexID, List<Message>>> initialMessage)
			throws RemoteException {
		this.worker.setInitialMessage(initialMessage);
	}

	private void restoreInitialState() {
		this.totalPartitions = 0;
	}

	public void heartBeat() throws RemoteException {
		this.worker.heartBeat();
	}
	
	public void checkPoint() throws Exception {
		this.worker.checkPoint();
	}

	public void startRecovery() throws RemoteException{
		worker.startRecovery();
	}
	
	public void finishRecovery() throws RemoteException{
		worker.finishRecovery();
	}
}
