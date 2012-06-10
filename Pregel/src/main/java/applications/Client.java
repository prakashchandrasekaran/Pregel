package applications;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import api.Client2Master;

/**
 * Client class
 * @author Prakash Chandrasekaran
 * @author Gautham Narayanasamy
 * @author Vijayaraghavan Subbaiah
 */

public class Client {
	public static void main(String[] args) throws RemoteException, NotBoundException, MalformedURLException, ClassNotFoundException {
		
		String masterMachineName = args[0];
		System.out.println("masterMachineName " + masterMachineName);
		
		String masterURL = "//" + masterMachineName + "/" + Client2Master.SERVICE_NAME;
		Client2Master client2Master = (Client2Master) Naming.lookup(masterURL);
		String graphFileName = "/storage/shelf2/eclipse-workspace/Pregel/input/input.txt";
		String vertexClassName = "applications.ShortestPathVertex";
		Class.forName(vertexClassName);
		ShortestPathData data = new ShortestPathData(new Double(0));
		client2Master.putTask(graphFileName, vertexClassName, 0, data);
	}
}
