package system;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.rmi.RemoteException;
import java.util.HashMap;
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
	Map<String, WorkerProxy> workerProxyMap;
	Master master;
	long pingInterval;
	String checkpointDir;

	public HealthManager(Map<String, WorkerProxy> workerProxyMap, Master master)
			throws PropertyNotFoundException {
		this.workerProxyMap = workerProxyMap;
		this.master = master;
		Props properties = Props.getInstance();
		pingInterval = properties.getLongProperty("PING_INTERVAL");
		checkpointDir = properties.getStringProperty("CHECKPOINT_DIR");
		failedWorkers = new HashSet<>();
	}

	public boolean checkHealth() {
		boolean health = true;
		String workerID;
		WorkerProxy workerProxy;
		for (Map.Entry<String, WorkerProxy> entry : workerProxyMap.entrySet()) {
			workerProxy = entry.getValue();
			try {
				workerProxy.heartBeat();
			} catch (RemoteException e) {
				System.out.println("Remote Exception received from the Worker");
				workerProxy.exit();
				workerID = entry.getKey();
				failedWorkers.add(workerID);
				master.removeWorker(workerID);
				health = false;
				continue;
			}
		}
		return health;
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
		FileInputStream fis;
		ObjectInputStream ois;
		Iterator<String> iter = failedWorkers.iterator();
		String workerID;
		String workerStateFile;
		while(iter.hasNext())
		{
			workerID = iter.next();
			workerStateFile = checkpointDir + workerID;
			fis = new FileInputStream(workerStateFile);
		    ois = new ObjectInputStream(fis);
		    Object obj = ois.readObject();
		    // assign partitions
		    ois.close();
		}
		workerProxyMap = master.workerProxyMap;
	}

	public static void main(String[] args) {
	}
}
