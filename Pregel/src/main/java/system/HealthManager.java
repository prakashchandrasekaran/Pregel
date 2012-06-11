package system;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import exceptions.PropertyNotFoundException;

import utility.Props;

/**
 * Represents a thread which checks the health of the workers using heart beat
 * 
 * @author Prakash Chandrasekaran
 * @author Gautham Narayanasamy
 * @author Vijayaraghavan Subbaiah
 */

public class HealthManager implements Runnable {
	Set<String> failedWorkers;
	Master master;
	long pingInterval;
	String checkpointDir;

	public HealthManager(Master master)
			throws PropertyNotFoundException {
		this.master = master;
		Props properties = Props.getInstance();
		pingInterval = properties.getLongProperty("PING_INTERVAL");
		checkpointDir = properties.getStringProperty("CHECKPOINT_DIR");
		failedWorkers = new HashSet<>();
	}
	
	/**
	 * Checks the health of all the workers
	 * 
	 * @return
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
		return (failedWorkers.size() == 0)? true : false;
	}

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
		    master.assignRecoveredPartitions(workerData);
		    ois.close();
		}
		failedWorkers.clear();
		finishRecovery();
		
	}
	
	private void startRecovery()
	{
	    String workerID;
		WorkerProxy workerProxy;
		for (Map.Entry<String, WorkerProxy> entry : master.getWorkerProxyMap().entrySet()) {
			workerProxy = entry.getValue();
			try {
				workerProxy.startRecovery();
			} catch (RemoteException e) {
				System.out.println("Remote Exception received from the Worker");
				workerProxy.exit();
				workerID = entry.getKey();
				failedWorkers.add(workerID);
				master.removeWorker(workerID);
				continue;
			}
		}
	}
	
	private void finishRecovery()
	{
		String workerID;
		WorkerProxy workerProxy;
		for (Map.Entry<String, WorkerProxy> entry : master.getWorkerProxyMap().entrySet()) {
			workerProxy = entry.getValue();
			try {
				workerProxy.finishRecovery();
			} catch (RemoteException e) {
				System.out.println("Remote Exception received from the Worker");
				workerProxy.exit();
				workerID = entry.getKey();
				failedWorkers.add(workerID);
				master.removeWorker(workerID);
				continue;
			}
		}
	}

	public static void main(String[] args) {
	}
}
