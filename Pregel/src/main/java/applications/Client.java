package applications;

import exceptions.PropertyNotFoundException;
import graphs.InputGenerator;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import utility.Props;

import api.Client2Master;

/**
 * Client class
 * @author Prakash Chandrasekaran
 * @author Gautham Narayanasamy
 * @author Vijayaraghavan Subbaiah
 */

public class Client {
	public static void main(String[] args) throws RemoteException, NotBoundException, MalformedURLException, ClassNotFoundException, PropertyNotFoundException {
		String masterMachineName = args[0];
		System.out.println("masterMachineName " + masterMachineName);
		String masterURL = "//" + masterMachineName + "/" + Client2Master.SERVICE_NAME;
		Client2Master client2Master = (Client2Master) Naming.lookup(masterURL);
		Props properties = Props.getInstance();
		int numVertices = properties.getIntProperty("TOTAL_NUM_VERTICES");
		double minEdgeWeight = properties.getDoubleProperty("MIN_EDGE_WEIGHT");
		double maxEdgeWeight = properties.getDoubleProperty("MAX_EDGE_WEIGHT");
		String graphFile = properties.getStringProperty("INPUT_GRAPH");
		InputGenerator inputGenerator = new InputGenerator(numVertices,
				minEdgeWeight, maxEdgeWeight, graphFile);
		inputGenerator.generateInput();
		String vertexClassName = "applications.ShortestPathVertex";
		ShortestPathData data = new ShortestPathData(new Double(0));
		client2Master.putTask(graphFile, vertexClassName, 0, data);
	}
}
