package applications;
import java.rmi.Naming;
import system.Worker2Master;
/**
 * Defines a deployment convenience to stop the system
 * 
 * @author Prakash Chandrasekaran
 * @author Gautham Narayanasamy
 * @author Vijayaraghavan Subbaiah
 */

public class ShutdownClient {
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws Exception the exception
	 */
	public static void main(String[] args) throws Exception {
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
		try {
			String masterMachineName = args[0];
			System.out.println("masterMachineName " + masterMachineName);
			String masterURL = "//" + masterMachineName + "/" + Worker2Master.SERVICE_NAME;
			Worker2Master worker2Master = (Worker2Master) Naming.lookup(masterURL);
			worker2Master.shutdown();
		} catch (Exception e) {
			System.err.println("Shutdown process Completed");
		}
	}
}
