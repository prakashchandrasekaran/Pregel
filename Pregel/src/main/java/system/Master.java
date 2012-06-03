package system;

import exceptions.PropertyNotFoundException;
import graphs.GraphPartitioner;

import java.rmi.AccessException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import api.Partition;

/**
 * The Class Master.
 * 
 * @author Prakash Chandrasekaran
 * @author Gautham Narayanasamy
 * @author Vijayaraghavan Subbaiah
 */
public class Master extends UnicastRemoteObject implements Worker2Master {

	/** The master thread. */
	// private Thread masterThread;

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -4329526430075226361L;

	/** The Constant SERVICE_NAME. */
	private static final String SERVICE_NAME = "Master";

	/** The worker id. */
	// private static int workerID = 0;

	/** The total number of worker threads. */
	private static AtomicInteger totalWorkerThreads = new AtomicInteger(0);

	/** The graph. */
	private GraphPartitioner graphPartitioner;

	/** The workerID to WorkerProxy map. */
	Map<String, WorkerProxy> workerProxyMap = new HashMap<>();

	/** The workerID to Worker map. **/
	Map<String, Worker> workerMap = new HashMap<>();

	/** The partitionID to workerID map. **/
	Map<Integer, String> partitionWorkerMap;

	/**
	 * Instantiates a new master.
	 * 
	 * @throws RemoteException
	 *             the remote exception
	 */
	public Master() throws RemoteException, PropertyNotFoundException {
		super();
	}

	/**
	 * Registers the worker computation nodes with the master
	 * 
	 * @param worker
	 *            Represents the {@link system.Worker Worker}
	 * @param numWorkerThreads
	 *            Represents the number of worker threads available in the
	 *            worker computation node
	 * @throws RemoteException
	 * @throws AccessException
	 */
	public Worker2Master register(Worker worker, String workerID,
			int numWorkerThreads) throws AccessException, RemoteException {
		totalWorkerThreads.getAndAdd(numWorkerThreads);
		WorkerProxy workerProxy = new WorkerProxy(worker, workerID,
				numWorkerThreads, this);
		workerProxyMap.put(workerID, workerProxy);
		workerMap.put(workerID, worker);
		return (Worker2Master) UnicastRemoteObject.exportObject(workerProxy, 0);
	}

	/**
	 * The application programmer calls this method to give submit the task (in
	 * the form of a GraphPartitioner object) to the master.
	 * 
	 * @param graphPartitioner
	 *            the graph
	 */
	public void putTask(GraphPartitioner graphPartitioner) {
		this.graphPartitioner = graphPartitioner;
		assignPartitions();
		sendWorkerPartitionInfo();
	}

	/**
	 * 
	 */
	private void sendWorkerPartitionInfo() {
		for (Map.Entry<String, WorkerProxy> entry : workerProxyMap.entrySet()) {
			WorkerProxy workerProxy = entry.getValue();
			workerProxy.setWorkerPartitionInfo(partitionWorkerMap, workerMap);
		}
	}

	/**
	 * Assign partitions to workers based on the number of processors (threads)
	 * that each worker has.
	 */
	private void assignPartitions() {
		int totalPartitions = this.graphPartitioner.getNumPartitions();
		Iterator<Partition> iter = this.graphPartitioner.iterator();
		Partition partition = null;
		partitionWorkerMap = new HashMap<>();
		// Assign partitions to workers in the ratio of the number of worker
		// threads that each worker has.
		for (Map.Entry<String, WorkerProxy> entry : workerProxyMap.entrySet()) {
			WorkerProxy workerProxy = entry.getValue();
			int numThreads = workerProxy.getNumThreads();
			double ratio = (double) numThreads / totalWorkerThreads.get();
			int numPartitionsToAssign = (int) ratio * totalPartitions;
			List<Partition> workerPartitions = new ArrayList<>();
			for (int i = 0; i < numPartitionsToAssign; i++) {
				partition = iter.next();
				workerPartitions.add(partition);
				partitionWorkerMap.put(partition.getPartitionID(),
						workerProxy.getWorkerID());
			}
			workerProxy.addPartitionList(workerPartitions);
		}

		// Add the remaining partitions (if any) in a round-robin fashion.
		Iterator<Map.Entry<String, WorkerProxy>> workerMapIter = workerProxyMap
				.entrySet().iterator();
		while (iter.hasNext()) {
			// If the remaining partitions is greater than the number of the
			// workers, start iterating from the beginning again.
			if (!workerMapIter.hasNext()) {
				workerMapIter = workerProxyMap.entrySet().iterator();
			}
			partition = iter.next();
			WorkerProxy workerProxy = workerMapIter.next().getValue();
			workerProxy.addPartition(partition);
			partitionWorkerMap.put(partition.getPartitionID(),
					workerProxy.getWorkerID());
		}
	}

	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) throws Exception {
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
		Master master;
		try {
			master = new Master();
			Registry registry = LocateRegistry.createRegistry(1099);
			registry.rebind(Master.SERVICE_NAME, master);
			System.out.println("Master Instance is bound and ready");
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */

	public void removeWorker(String serviceName) throws RemoteException {
		workerProxyMap.remove(serviceName);
	}

}
