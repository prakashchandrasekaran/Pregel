package system;

import graphs.VertexID;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The Interface Worker.
 * 
 * @author Prakash Chandrasekaran
 * @author Gautham Narayanasamy
 * @author Vijayaraghavan Subbaiah
 */
public interface Worker extends Remote {

	/**
	 * Gets the worker id.
	 * 
	 * @return the worker id
	 * @throws RemoteException
	 *             the remote exception
	 */
	public String getWorkerID() throws RemoteException;

	/**
	 * Gets the num threads.
	 * 
	 * @return the num threads
	 * @throws RemoteException
	 *             the remote exception
	 */
	public int getNumThreads() throws RemoteException;

	/**
	 * Sets the master proxy.
	 * 
	 * @param masterProxy
	 *            the new master proxy
	 * @throws RemoteException
	 *             the remote exception
	 */
	public void setMasterProxy(Worker2Master masterProxy)
			throws RemoteException;

	/**
	 * Adds the partition.
	 * 
	 * @param partition
	 *            the partition
	 * @throws RemoteException
	 *             the remote exception
	 */
	public void addPartition(Partition partition) throws RemoteException;

	/**
	 * Adds the partition list.
	 * 
	 * @param workerPartitions
	 *            the worker partitions
	 * @throws RemoteException
	 *             the remote exception
	 */
	public void addPartitionList(List<Partition> workerPartitions)
			throws RemoteException;

	/**
	 * Sets the worker partition info.
	 * 
	 * @param totalPartitionsAssigned
	 *            the total partitions assigned
	 * @param mapPartitionIdToWorkerId
	 *            the map partition id to worker id
	 * @param mapWorkerIdToWorker
	 *            the map worker id to worker
	 * @throws RemoteException
	 *             the remote exception
	 */
	public void setWorkerPartitionInfo(int totalPartitionsAssigned,
			Map<Integer, String> mapPartitionIdToWorkerId,
			Map<String, Worker> mapWorkerIdToWorker) throws RemoteException;

	/**
	 * Halt.
	 * 
	 * @throws RemoteException
	 *             the remote exception
	 */
	public void halt() throws RemoteException;

	/**
	 * Start super step.
	 * 
	 * @param superStepCounter
	 *            the super step counter
	 * @throws RemoteException
	 *             the remote exception
	 */
	public void startSuperStep(long superStepCounter) throws RemoteException;

	/**
	 * Sets the initial message.
	 * 
	 * @param initialMessage
	 *            the initial message
	 * @throws RemoteException
	 *             the remote exception
	 */
	public void setInitialMessage(
			ConcurrentHashMap<Integer, Map<VertexID, List<Message>>> initialMessage)
			throws RemoteException;

	/**
	 * Receive message.
	 * 
	 * @param incomingMessages
	 *            the incoming messages
	 * @throws RemoteException
	 *             the remote exception
	 */
	public void receiveMessage(Map<VertexID, List<Message>> incomingMessages)
			throws RemoteException;

	/**
	 * Send heartbeat to Worker to check its health.
	 * 
	 * @throws RemoteException
	 *             the remote exception
	 */
	public void sendHeartBeat() throws RemoteException;

	/**
	 * Check point.
	 * 
	 * @param superstep the superstep
	 * @throws Exception the exception
	 */
	public void checkPoint(long superstep) throws Exception;

	/**
	 * Start recovery.
	 * 
	 * @throws RemoteException
	 *             the remote exception
	 */
	public void startRecovery() throws RemoteException;

	/**
	 * Finish recovery.
	 * 
	 * @throws RemoteException
	 *             the remote exception
	 */
	public void finishRecovery() throws RemoteException;

	/**
	 * Adds the recovered data.
	 * 
	 * @param partition
	 *            the partition
	 * @param messages
	 *            the messages
	 * @throws RemoteException
	 *             the remote exception
	 */
	public void addRecoveredData(Partition partition,
			Map<VertexID, List<Message>> messages) throws RemoteException;

	/**
	 * Method to shutdown the worker machine
	 * 
	 * @throws RemoteException
	 */
	public void shutdown() throws RemoteException;

	/**
	 * Write output.
	 * 
	 * @param outputFilePath
	 *            the output file path
	 * @throws RemoteException
	 *             the remote exception
	 */
	public void writeOutput(String outputFilePath) throws RemoteException;

	/**
	 * Update checkpoint file after the Master knows that the checkpointing process had succeeded in all the Workers.
	 *
	 * @throws RemoteException the remote exception
	 */
	public void updateCheckpointFile() throws RemoteException;

}
