package system;

import java.rmi.AccessException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import api.Partition;

/**
 * Represents a thread which is used by the master to talk to workers and
 * vice-versa
 * 
 * @author Prakash Chandrasekaran
 * @author Gautham Narayanasamy
 * @author Vijayaraghavan Subbaiah
 */

public class WorkerProxy extends UnicastRemoteObject implements Runnable,
		Worker2Master {
	private static final long serialVersionUID = -2001231189089230248L;
	private Worker worker;
	private Master master;
	private Thread t;
	private int numWorkerThreads;
	String serviceName; 
	BlockingQueue<Partition> partitionList;

	/**
	 * 
	 * @param worker
	 *            Represents the remote {@link system.Worker Worker}
	 * @param master
	 *            Represents the {@link system.Master Master}
	 * @param serviceName
	 *            Represents the unique serviceName to identify the worker
	 * @throws RemoteException
	 * @throws AccessException
	 */

	public WorkerProxy(Worker worker, String serviceName, int numWorkerThreads,
			Master master) throws AccessException, RemoteException {
		this.worker = worker;
		this.serviceName = serviceName;
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
				worker.addPartition(partition);
			} catch (RemoteException e) {
				System.out.println("Remote Exception received from the Worker");
				System.out.println("Giving back the partition to the Master ");
				try {
					master.removeWorker(serviceName);
					// give the partition back to Master
					return;
				} catch (RemoteException e1) {
					System.out.println("Master is Down");
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void addPartition(Partition partition) {
		partitionList.add(partition);
	}

	public int getNumThreads() {
		return numWorkerThreads;
	}

	public void addPartitionList(List<Partition> workerPartitions) {
		try {
			worker.addPartitionList(workerPartitions);
		} catch (RemoteException e) {
			System.out.println("Remote Exception received from the Worker");
			System.out.println("Giving back the partition to the Master ");
			try {
				master.removeWorker(serviceName);
				// give the partition back to Master
				return;
			} catch (RemoteException e1) {
				System.out.println("Master is Down");
			}
		}
	}

	@Override
	public Worker2Master register(Worker worker, String workerID, int numWorkerThreads)
			throws RemoteException {
		return null;
	}
}
