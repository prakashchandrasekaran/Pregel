package applications;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import utility.Props;
import api.Client2Master;
import api.Data;
import exceptions.PropertyNotFoundException;
import graphs.InputGenerator;

/**
 * Represents the Page Rank Client
 *
 * @author Prakash Chandrasekaran
 * @author Gautham Narayanasamy
 * @author Vijayaraghavan Subbaiah
 */

public class PageRankClient {
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws RemoteException the remote exception
	 * @throws NotBoundException the not bound exception
	 * @throws MalformedURLException the malformed url exception
	 * @throws ClassNotFoundException the class not found exception
	 * @throws PropertyNotFoundException the property not found exception
	 */
	public static void main(String[] args) throws RemoteException, NotBoundException, MalformedURLException, ClassNotFoundException, PropertyNotFoundException {
		String masterMachineName = args[0];
		String masterURL = "//" + masterMachineName + "/" + Client2Master.SERVICE_NAME;
		Client2Master client2Master = (Client2Master) Naming.lookup(masterURL);		
		runApplication(client2Master);	
	}
	
	/**
	 * Run application.
	 *
	 * @param client2Master the client2 master
	 * @param applicationID the application id
	 * @throws PropertyNotFoundException the property not found exception
	 * @throws RemoteException the remote exception
	 */
	private static void runApplication(Client2Master client2Master) throws PropertyNotFoundException, RemoteException{
		Props properties = Props.getInstance();
		int numVertices = properties.getIntProperty("TOTAL_NUM_VERTICES");
		double minEdgeWeight = properties.getDoubleProperty("MIN_EDGE_WEIGHT");
		double maxEdgeWeight = properties.getDoubleProperty("MAX_EDGE_WEIGHT");
		String graphFile = properties.getStringProperty("INPUT_GRAPH");
		InputGenerator inputGenerator = new InputGenerator(numVertices,
				minEdgeWeight, maxEdgeWeight, graphFile);
		inputGenerator.generateInput();
		String vertexClassName = null;
		Data<Double> data = null;
			vertexClassName = "applications.PageRankVertex";
			data = new PageRankData(new Double(0));
		System.out.println("Vertex class: " + vertexClassName);
		client2Master.putTask(graphFile, vertexClassName, 0, data);
	}
}
