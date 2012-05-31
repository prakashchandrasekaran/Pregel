/*
 * @author gautham
 */
package system;

import exceptions.PropertyNotFoundException;
import graphs.GraphPartitioner;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import utility.Props;
import api.Partition;

/**
 * The Class Master.
 */
public class Master extends UnicastRemoteObject implements Runnable {
	
	/** The master thread. */
	private Thread masterThread;
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -4329526430075226361L;
	
	/** The Constant SERVICE_NAME. */
	private static final String SERVICE_NAME = "Master";
	
	/** The worker id. */
	private static int workerID = 0;
	
	/** The total number of worker threads. */
	private static int totalWorkerThreads = 0;
	
	/** The Constant totalWorkers. */
	private static int totalWorkers;
	
	/** The graph. */
	private GraphPartitioner graph;
	
	/** The worker map. */
	Map<Integer, Worker> workerMap = new HashMap<Integer, Worker>();
		
	
	/**
	 * Instantiates a new master.
	 *
	 * @throws RemoteException the remote exception
	 */
	public Master() throws RemoteException, PropertyNotFoundException {
		super();
		totalWorkers = Props.getInstance().getIntProperty("TOTAL_WORKERS");		
//		masterThread = new Thread(this);
//		masterThread.start();
	}
	
	/**
	 * Register.
	 *
	 * @param worker the worker
	 */
	public void register(Worker worker) {
		workerID += 1;
		totalWorkerThreads += worker.getNumThreads();
		workerMap.put(workerID, worker);
	}

	/**
	 * The application programmer calls this method to give the input graph to the master.
	 *
	 * @param graph the graph
	 */
	public void putGraph(GraphPartitioner graph){
		this.graph = graph;
		assignPartitions();
	}
	
	
	/**
	 * Assign partitions to workers based on the number of processors (threads) that each worker has.
	 */
	public void assignPartitions() {
		int totalPartitions = graph.getNumPartitions();
		Iterator<Partition> iter = graph.iterator();
		
		// Assign partitions to workers in the ratio of the number of worker threads that each worker has. 
		for(Map.Entry<Integer, Worker> entry : workerMap.entrySet()){
			Worker worker = entry.getValue();
			int numThreads = worker.getNumThreads();
			int numPartitionsToAssign = numThreads / totalWorkerThreads * totalPartitions;
			for(int i = 0; i < numPartitionsToAssign; i++){
				worker.addPartition(iter.next());
			}
		}
		
		// Add the remaining partitions (if any) in a round-robin fashion.
		int index = 0;
		while(iter.hasNext()){
			int workerID = (index % totalWorkers) + 1;			
			workerMap.get(workerID).addPartition(iter.next());
		}
				
	}

	/**
	 * The main method.
	 *
	 * @param args the arguments
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

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		
	}

}
