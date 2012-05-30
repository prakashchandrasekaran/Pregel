package system;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import api.Partition;

public class Master extends UnicastRemoteObject implements Runnable {
	public Master() throws RemoteException {
		super();
		masterThread = new Thread(this);
		masterThread.start();
	}

	private Thread masterThread;
	private static final long serialVersionUID = -4329526430075226361L;
	private static final String SERVICE_NAME = "Master";
	private int numWorkers = 0;
	private int numWorkerThreads = 0;

	public void register(Worker worker, int numWorkerThreads) {
		numWorkers += 1;
		this.numWorkerThreads += numWorkerThreads;
	}

	public void assignPartitions() {
		List<Partition> partitionList = new ArrayList<>();
	}

	public static void main(String[] args) {
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
		Master master;
		try {
			master = new Master();
			Registry registry = LocateRegistry.createRegistry(1099);
			registry.rebind(Master.SERVICE_NAME, master);
			System.out.println("Master Instance is bound and ready");
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {

	}

}
