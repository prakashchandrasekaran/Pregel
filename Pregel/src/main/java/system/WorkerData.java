package system;

import graphs.VertexID;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class WorkerData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4779387255673737945L;
	private Queue<Partition> partitions;
	private Map<Integer, Map<VertexID, List<Message>>> messages;
	
	public WorkerData(Queue<Partition> partitions,
			Map<Integer, Map<VertexID, List<Message>>> messages) {
		this.partitions = partitions;
		this.messages = messages;
	}
	
	
	public Queue<Partition> getPartitions() {
		return partitions;
	}


	public void setPartitions(Queue<Partition> partitions) {
		this.partitions = partitions;
	}


	public Map<Integer, Map<VertexID, List<Message>>> getMessages() {
		return messages;
	}


	public void setMessages(Map<Integer, Map<VertexID, List<Message>>> messages) {
		this.messages = messages;
	}


	@Override
	public String toString(){
		return "Partitions:  " + partitions.toString() + "\n" + "Messages: " + messages;
	}
	
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		
		Map<Integer, Map<VertexID, List<Message>>> messages
		 = new HashMap<>();
		Map<VertexID, List<Message>> vertexIDtoMessagesMap 
		 = new HashMap<>();
		VertexID vID = new VertexID(123, 12345678);
		vertexIDtoMessagesMap.put(vID, null);
		messages.put(123, vertexIDtoMessagesMap);
		WorkerData wd = new WorkerData(null, messages);
		
		// serialize
		FileOutputStream fos = new FileOutputStream("serial"); 
		ObjectOutputStream oos = new ObjectOutputStream(fos); 
		oos.writeObject(wd); 
		oos.flush(); 
		oos.close();
		
		// deserialize
		FileInputStream fis = new FileInputStream("serial"); 
		ObjectInputStream ois = new ObjectInputStream(fis); 
		WorkerData workerDataObject = (WorkerData)ois.readObject(); 
		ois.close();
		
		// printing values
		System.out.println("Partitions  " + workerDataObject.partitions);
		System.out.println("Message Map " + workerDataObject.messages);
		System.out.println("Message Map Keys  " + workerDataObject.messages.keySet());
		
	}

}
