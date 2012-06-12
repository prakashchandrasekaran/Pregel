package api;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Defines the interface through which the application programmer communicates
 * with the Master.
 * 
 * @author Prakash Chandrasekaran
 * @author Gautham Narayanasamy
 * @author Vijayaraghavan Subbaiah
 */

public interface Client2Master extends Remote {
	/** The Constant SERVICE_NAME. */
	public static final String SERVICE_NAME = "Master";

	/**
	 * Submits the graph problem to be computed.
	 * 
	 * @param graphFileName
	 *            the graph file name
	 * @param vertexClassName
	 *            the application specific vertex class name
	 */
	public <T> void putTask(String graphFileName, String vertexClassName,
			long sourceVertexID, Data<T> data) throws RemoteException;

	/**
	 * Take the file (specified by its name) that stores the computed result
	 * from the Master.
	 * 
	 * @return string representing the output file name.
	 */
	public String takeResult() throws RemoteException;
}
