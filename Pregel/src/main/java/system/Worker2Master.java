package system;

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
	 * Registers remote workers with the master
	 * 
	 * @param Worker
	 *            Represents the {@system.Worker Worker} that is available for
	 *            registering itself with the {@link system.Master Master}.
	 * @throws java.rmi.RemoteException
	 *             Throws RemoteException if {@link system.Worker Worker} is
	 *             unable to register itself with the {@link system.Master
	 *             Master}
	 */

	public Worker2Master register(Worker worker, String workerID, int numWorkerThreads)
			throws java.rmi.RemoteException;
}
