package system;

import graphs.VertexID;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WorkerData implements Serializable {

	List<Partition> partitions;
	Map<Integer, Map<VertexID, List<Message>>> messages;
	
	public WorkerData() {
		
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
