/*
 * @author gautham
 */
package system;

import java.util.List;
import api.Partition;

public class Worker {
	
	private int numThreads;
	private List<Partition> partitionList;
	
	/**
	 * Adds the list of partitions to be assigned to the worker.
	 *
	 * @param partition the partition to be assigned
	 */
	public void addPartition(Partition partition) {
		this.partitionList.add(partition);
	}

	public Worker(){
		this.numThreads = Runtime.getRuntime().availableProcessors();
		for(int i = 0; i < numThreads; i++){
			WorkerThread workerThread = new WorkerThread();
			workerThread.start();
		}
	}
	
	public int getNumThreads() {
		return numThreads;
	}
		
}
