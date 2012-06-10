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
		int numVertices = 10000;//properties.getIntProperty("INPUTGEN_BUFFER_SIZE");
		double minEdgeWeight = 1;//properties.getDoubleProperty("MIN_EDGE_WEIGHT");
		double maxEdgeWeight = 1;//properties.getDoubleProperty("MAX_EDGE_WEIGHT");
		String graph = "/storage/shelf2/eclipse-workspace/Pregel/input/input.txt";//properties.getStringProperty("INPUT_GRAPH");
		InputGenerator inputGenerator = new InputGenerator(numVertices,
				minEdgeWeight, maxEdgeWeight, graph);
		inputGenerator.generateInput();
		//System.out.println("File generated successfully!");
		String vertexClassName = "applications.ShortestPathVertex";
		ShortestPathData data = new ShortestPathData(new Double(0));
		client2Master.putTask(graph, vertexClassName, 0, data);
	}
}
