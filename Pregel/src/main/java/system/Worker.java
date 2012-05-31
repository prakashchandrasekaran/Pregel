/*
 * @author gautham
 */
package system;

import java.rmi.Naming;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import api.Partition;

public class Worker {
	
	private int numThreads;
	private BlockingQueue<Partition> partitionQueue;
	
	/**
	 * Adds the partition to be assigned to the worker.
	 *
	 * @param partition the partition to be assigned
	 */
	public void addPartition(Partition partition) {
		this.partitionQueue.add(partition);
	}

	public Worker(){
		this.partitionQueue = new LinkedBlockingDeque<Partition>();
		this.numThreads = Runtime.getRuntime().availableProcessors();
		for(int i = 0; i < numThreads; i++){
			WorkerThread workerThread = new WorkerThread();
			workerThread.start();
		}
	}
	
	public int getNumThreads() {
		return numThreads;
	}
	
	private class WorkerThread extends Thread{		
		@Override
		public void run() {
			while(true){
				try {
					Partition partition = partitionQueue.take();
					// do some work on the partition.
					
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	public static void main(String[] args) throws Exception{
		Master master = (Master) Naming.lookup("//localhost/Master");
		Worker worker = new Worker();
		master.register(worker);
	}
}
