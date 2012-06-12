package system;

import java.io.File;
import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import utility.GeneralUtils;
import utility.Props;
import exceptions.PropertyNotFoundException;

/**
 * Represents a thread which checks the health of the workers using heart beat
 * and takes care of fault tolerance
 * 
 * @author Prakash Chandrasekaran
 * @author Gautham Narayanasamy
 * @author Vijayaraghavan Subbaiah
 */

public class HealthManager implements Runnable {

	/** The failed workers. */
	private Set<String> failedWorkers;

	/** The master. */
	private Master master;

	/** The ping interval. */
	private long pingInterval;

	/** The checkpoint directory. */
	private String checkpointDir;

	private Thread t;

	/**
	 * Instantiates a new health manager.
	 * 
	 * @param master
	 *            the master
	 * @throws PropertyNotFoundException
	 *             the property not found exception
	 */
	public HealthManager(Master master) throws PropertyNotFoundException {
		this.master = master;
		Props properties = Props.getInstance();
		pingInterval = properties.getLongProperty("PING_INTERVAL");
		checkpointDir = properties.getStringProperty("CHECKPOINT_DIR");
		failedWorkers = new HashSet<>();
		t = new Thread(this);
		t.start();
	}

	/**
	 * Stop the HealthManager
	 */
	public void exit() {
		try {
			t.interrupt();
			t = null;
		} catch (Exception e) {
			System.out.println("HealthManager Stopped");
		}
	}

	/**
	 * Checks the health of all the workers.
	 * 
	 * @return true, if successful
	 */
	private boolean checkHealth() {
		String workerID;
		WorkerProxy workerProxy;
		for (Map.Entry<String, WorkerProxy> entry : master.getWorkerProxyMap()
				.entrySet()) {
			workerProxy = entry.getValue();
			try {
				workerProxy.sendHeartBeat();
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(pingInterval);
				if (!checkHealth()) {
					recover();
				}
			} catch (InterruptedException e) {
				System.out.println("Health Checker Stopped");
			}
		}
	}

	/**
	 * Represents the process of recovery
	 */
	private void recover() {
		System.out.println("HealthManager: recover");
		startRecovery();
		recoverActiveWorkerSet();
		Iterator<String> iter = failedWorkers.iterator();
		String workerID;
		String workerStateFile;
		WorkerData workerData;
		// Reads the persisted partition info of all the failed nodes and
		// assigns it to healthy nodes
		while (iter.hasNext()) {
			workerID = iter.next();
			workerStateFile = checkpointDir + File.separator + workerID + "_" + this.master.getLastCheckpointedSuperstep();	
			workerData = (WorkerData) GeneralUtils.deserialize(workerStateFile);
			assignRecoveredPartitions(workerID, workerData);
		}
		
		// Send the modified maps to all the workers.
		try {
			this.master.sendWorkerPartitionInfo();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		finishRecovery();
		failedWorkers.clear();
	}

	/**
	 * Prepares all the workers for the recovery process
	 */
	private void startRecovery() {
		String workerID;
		WorkerProxy workerProxy;
		for (Map.Entry<String, WorkerProxy> entry : master.getWorkerProxyMap()
				.entrySet()) {
			workerProxy = entry.getValue();
			try {
				workerProxy.startRecovery();
			} catch (Exception e) {
				workerID = entry.getKey();
				System.out.println("Remote Exception received from the Worker "
						+ workerID);
				workerProxy.exit();
				failedWorkers.add(workerID);
				master.removeWorker(workerID);
				continue;
			}
		}
	}

	/**
	 * Recover the serialized active worker set.
	 */
	private void recoverActiveWorkerSet() {
		System.out.println("HealthManager: recoverActiveWorkerSet");
		String filePath = checkpointDir + File.separator + "activeworkers";
		@SuppressWarnings("unchecked")
		Set<String> set = (Set<String>) GeneralUtils.deserialize(filePath);
		System.out.println("Active worker set: " + set);
		master.setActiveWorkerSet(set);
	}

	/**
	 * Finishes the recovery process.
	 */
	private void finishRecovery() {
		System.out.println("HealthManager: finishRecovery");
		String workerID;
		WorkerProxy workerProxy;
		boolean failureDuringRecovery = false;
		for (Map.Entry<String, WorkerProxy> entry : master.getWorkerProxyMap()
				.entrySet()) {
			workerProxy = entry.getValue();
			try {
				workerProxy.finishRecovery();				
			} catch (Exception e) {
				System.out.println("Remote Exception received from the Worker");
				workerProxy.exit();
				workerID = entry.getKey();
				failedWorkers.add(workerID);
				master.removeWorker(workerID);
				failureDuringRecovery = true;
				continue;
			}
		}
		if (failureDuringRecovery){
			recover();
		}
		else {
			try {
				// Update the checkpoint file for all the workers. This will set the current checkpoint file to the one which was done during this recovery.
				this.master.updateCheckpointFile();
				// Serialize the active worker set.
				this.master.serializeActiveWorkerSet();
				// Reset the superstep to the most recent superstep at which checkpointing was done.
				this.master.resetCheckpointSuperstep();
				System.out.println("Calling start superstep");
				this.master.startSuperStep();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Assign the recovered partitions from the dead Worker to other random
	 * Workers.
	 * 
	 * @param workerID
	 *            the dead worker's id
	 * @param workerData
	 *            the dead Worker's data
	 */
	private void assignRecoveredPartitions(String workerID,
			WorkerData workerData) {
		System.out.println("HealthManager: Assigning recovered partitions");
		Map<Integer, String> partitionWorkerMap = this.master
				.getPartitionWorkerMap();
		Map<String, WorkerProxy> workerProxyMap = this.master
				.getWorkerProxyMap();
		Set<String> activeWorkerSet = this.master.getActiveWorkerSet();

		Object[] workerProxyCollection = workerProxyMap.values().toArray();
		// Remove the dead worker from the active worker set if at all it was
		// present during checkpointing.
		boolean wasDeadWorkerActive = activeWorkerSet.contains(workerID);
		if (wasDeadWorkerActive) {
			activeWorkerSet.remove(workerID);
		}
		// System.out.println("WorkerData Partitions " + workerData);
		Random rand = new Random();
		for (Iterator<Partition> iter = workerData.getPartitions().iterator(); iter
				.hasNext();) {

			Partition partition = iter.next();

			int index = (rand.nextInt(workerProxyCollection.length));
			// Choose a random worker from the map and assign the partition to
			// it.
			WorkerProxy workerProxy = (WorkerProxy) workerProxyCollection[index];
			System.out.println("Assigning " + partition.getPartitionID() + " to " + workerProxy.getWorkerID() );
			partitionWorkerMap.put(partition.getPartitionID(),
					workerProxy.getWorkerID());
			// If the dead worker was active during checkpointing, add the
			// worker to which the dead worker's partition is assigned.
			if (wasDeadWorkerActive) {
				activeWorkerSet.add(workerProxy.getWorkerID());
			}
			try {
				// send this partition and the messages that were sent to this
				// partition to the Worker.
				workerProxy.addRecoveredData(partition, workerData
						.getMessages().get(partition.getPartitionID()));

			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}

	}
}
