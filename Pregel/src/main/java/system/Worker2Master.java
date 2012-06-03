/*
 * @author sam
 */
package system;

import java.rmi.RemoteException;
import java.util.Set;

/**
 * Defines an interface to register remote ({@link system.Worker Worker}) with
 * the {@link system.Master Master}.
 * 
 * @author Prakash Chandrasekaran
 * @author Gautham Narayanasamy
 * @author Vijayaraghavan Subbaiah
 */

public interface Worker2Master extends java.rmi.Remote {

	/** The Constant SERVICE_NAME. */
	public static final String SERVICE_NAME = "Master";

	/**
	 * Registers remote workers with the master.
	 *
	 * @param worker the worker
	 * @param workerID the worker id
	 * @param numWorkerThreads the num worker threads
	 * @return worker2 master
	 * @throws RemoteException the remote exception
	 */

	public Worker2Master register(Worker worker, String workerID,
			int numWorkerThreads) throws RemoteException;

	
	/**
	 * Send a message to the Master saying that the current superstep has been completed.
	 *
	 * @param workerID the worker id
	 */
	public void superStepCompleted(String workerID, Set<String> activeWorkerIDs);
}
