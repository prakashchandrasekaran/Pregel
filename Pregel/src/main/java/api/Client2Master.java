package api;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Defines the interface through which the application programmer communicates with the Master.
 * 
 * @author Prakash Chandrasekaran
 * @author Gautham Narayanasamy
 * @author Vijayaraghavan Subbaiah
 */
 
public interface Client2Master extends Remote{
	
	/**
	 * Submits the graph problem to be computed.
	 *
	 * @param graphFileName the graph file name
	 * @param vertexClassName the application specific vertex class name
	 */
	public void putTask(String graphFileName, String vertexClassName) throws RemoteException;
	
	/**
	 * Take the computed result from the Master.
	 *
	 * @return string representing the output file name.
	 */
	public String takeResult() throws RemoteException;
}
