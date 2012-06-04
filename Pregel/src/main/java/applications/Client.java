package applications;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import api.Client2Master;

public class Client {

	public static void main(String[] args) throws RemoteException, NotBoundException {
		String masterMachineName = args[0];
		Registry registry = LocateRegistry.getRegistry(masterMachineName);
		Client2Master client2Master = (Client2Master) registry
				.lookup(Client2Master.SERVICE_NAME);
		String graphFileName = "output/output.txt";
		String vertexClassName = "ShortestPathVertex";
		client2Master.putTask(graphFileName, vertexClassName);
	}
}
