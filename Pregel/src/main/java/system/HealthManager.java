package system;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import utility.Props;
import exceptions.PropertyNotFoundException;

/**
 * Represents a thread which checks the health of the workers using heart beat.
 *
 * @author Prakash Chandrasekaran
 * @author Gautham Narayanasamy
 * @author Vijayaraghavan Subbaiah
 */

public class HealthManager implements Runnable {
	
	/** The failed workers. */
	Set<String> failedWorkers;
	
	/** The master. */
	Master master;
	
	/** The ping interval. */
	long pingInterval;
	
	/** The checkpoint dir. */
	String checkpointDir;

	/**
	 * Instantiates a new health manager.
	 *
	 * @param master the master
	 * @throws PropertyNotFoundException the property not found exception
	 */
	public HealthManager(Master master)
			throws PropertyNotFoundException {
		this.master = master;
		Props properties = Props.getInstance();
		pingInterval = properties.getLongProperty("PING_INTERVAL");
		checkpointDir = properties.getStringProperty("CHECKPOINT_DIR");
		failedWorkers = new HashSet<>();
	}
	
	/**
	 * Checks the health of all the workers.
	 *
	 * @return true, if successful
	 */
	public boolean checkHealth() {
		String workerID;
		WorkerProxy workerProxy;
		for (Map.Entry<String, WorkerProxy> entry : master.getWorkerProxyMap().entrySet()) {
			workerProxy = entry.getValue();
			try {
				workerProxy.heartBeat();
			} catch (RemoteException e) {
				System.out.println("Remote Exception received from the Worker");
				workerProxy.exit();
				workerID = entry.getKey();
				failedWorkers.add(workerID);
				master.removeWorker(workerID);
				continue;
			}
		}
		return (failedWorkers.size() == 0);
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		boolean health = true;
		while (true) {
			try {
				Thread.sleep(pingInterval);
				health = checkHealth();
				if (!health) {
					recovery();
				}
			} catch (InterruptedException e) {
				System.out.println("Health Checker Stopped");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Recovery.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ClassNotFoundException the class not found exception
	 */
	private void recovery() throws IOException, ClassNotFoundException {
		startRecovery();
		FileInputStream fis;
		ObjectInputStream ois;
		Iterator<String> iter = failedWorkers.iterator();
		String workerID;
		String workerStateFile;
		WorkerData workerData;
		while(iter.hasNext())
		{
			workerID = iter.next();
			workerStateFile = checkpointDir + File.pathSeparator + workerID;
			fis = new FileInputStream(workerStateFile);
		    ois = new ObjectInputStream(fis);
		    workerData = (WorkerData)ois.readObject();
		    assignRecoveredPartitions(workerID, workerData);
		    ois.close();
		}
		failedWorkers.clear();
		finishRecovery();
		
	}
	
	/**
	 * Start recovery.
	 */
	private void startRecovery()
	{
	    String workerID;
		WorkerProxy workerProxy;
		recoverActiveWorkerSet();
		for (Map.Entry<String, WorkerProxy> entry : master.getWorkerProxyMap().entrySet()) {
			workerProxy = entry.getValue();
			try {
				workerProxy.startRecovery();
			} catch (Exception e) {
				System.out.println("Remote Exception received from the Worker");
				workerProxy.exit();
				workerID = entry.getKey();
				failedWorkers.add(workerID);
				master.removeWorker(workerID);
				continue;
			}
		}
	}
	
	/**
	 * Recover the serialized active worker set.
	 */
	private void recoverActiveWorkerSet(){
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try{
			fis = new FileInputStream(checkpointDir + File.pathSeparator + "activeworkers");
		    ois = new ObjectInputStream(fis);
		    master.setActiveWorkerSet((Set<String>)ois.readObject());
		}
		catch(IOException | ClassNotFoundException e){
			e.printStackTrace();
		}
		finally{
			try {
				fis.close();			
				ois.close();
			} catch (IOException e) {				
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Finish recovery.
	 */
	private void finishRecovery()
	{
		String workerID;
		WorkerProxy workerProxy;
		for (Map.Entry<String, WorkerProxy> entry : master.getWorkerProxyMap().entrySet()) {
			workerProxy = entry.getValue();
			try {
				workerProxy.finishRecovery();
			} catch (Exception e) {
				System.out.println("Remote Exception received from the Worker");
				workerProxy.exit();
				workerID = entry.getKey();
				failedWorkers.add(workerID);
				master.removeWorker(workerID);
				continue;
			}
		}
	}
	
	/**
	 * Assign the recovered partitions from the dead Worker to other random Workers.
	 *
	 * @param workerID the dead worker's id
	 * @param workerData the dead Worker's data
	 */
	private void assignRecoveredPartitions(String workerID, WorkerData workerData){
		Map<Integer, String> partitionWorkerMap = this.master.getPartitionWorkerMap();
		Map<String, WorkerProxy> workerProxyMap = this.master.getWorkerProxyMap();
		Set<String> activeWorkerSet = this.master.getActiveWorkerSet();
		
		Set<Entry<String, WorkerProxy>> set = workerProxyMap.entrySet();
		Entry<String, WorkerProxy>[] entries = (Entry<String, WorkerProxy>[])set.toArray();
		
		// Remove the dead worker from the active worker set if at all it was present during checkpointing.
		boolean wasDeadWorkerActive = activeWorkerSet.contains(workerID);
		if(wasDeadWorkerActive){
			activeWorkerSet.remove(workerID);
		}
		Random rand = new Random();
		for(Iterator<Partition> iter = workerData.getPartitions().iterator(); iter.hasNext();){
			Partition partition = iter.next();
			int index = (rand.nextInt(entries.length));
			// Choose a random worker from the map and assign the partition to it.
			WorkerProxy workerProxy = entries[index].getValue();
			partitionWorkerMap.put(partition.getPartitionID(),
					workerProxy.getWorkerID());
			// If the dead worker was active during checkpointing, add the worker to which the dead worker's partition is assigned.
			if(wasDeadWorkerActive){
				activeWorkerSet.add(workerProxy.getWorkerID());
			}
			try {
				workerProxy.addRecoveredData(workerData);
				// Send the modified maps to all the workers.
				this.master.sendWorkerPartitionInfo();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
	}
}
