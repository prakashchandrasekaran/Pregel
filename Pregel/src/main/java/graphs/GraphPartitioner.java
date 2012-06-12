package graphs;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.*;

import exceptions.PropertyNotFoundException;

import system.Partition;
import utility.Props;
import utility.GeneralUtils;
import api.Vertex;

/**
 * Constructs the graph partitions
 * 
 * @author Prakash Chandrasekaran
 * @author Gautham Narayanasamy
 * @author Vijayaraghavan Subbaiah
 * 
 */
public class GraphPartitioner implements Iterable<Partition> {
	/** Number of vertices */
	private long numVertices;
	/** Number of partitions */
	private int numPartitions;
	/** Buffered Reader to buffer file */
	private BufferedReader br;
	/** Vertex class name of the application */
	private String vertexClassName;
	/** Maximum number of vertices per partition */
	public static long MAX_VERTICES_PER_PARTITION;

	static {
		try {
			MAX_VERTICES_PER_PARTITION = Props.getInstance().getLongProperty(
					"MAX_VERTICES_PER_PARTITION");
		} catch (PropertyNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Constructs the graph partitioner
	 * 
	 * @param fileName
	 *            Represents the input graph generated file
	 * @param numWorkerManager
	 *            Represents the number of worker nodes registered for
	 *            computations
	 * @param numWorker
	 *            Represents the number of total number of worker threads
	 *            available for computations
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	public GraphPartitioner(String fileName, String vertexClassName)
			throws NumberFormatException, IOException {
		this.vertexClassName = vertexClassName;
		br = new BufferedReader(new FileReader(fileName));
		numVertices = Long.parseLong(br.readLine());
		if (numVertices < MAX_VERTICES_PER_PARTITION)
			numPartitions = 1;
		else {
			numPartitions = (int) (numVertices / MAX_VERTICES_PER_PARTITION);
			if (numVertices % MAX_VERTICES_PER_PARTITION != 0)
				numPartitions += 1;
		}
	}

	/**
	 * Gets the list of vertices comprising a partition
	 * 
	 * @return Returns list of vertices comprising a partition
	 */
	public Map<VertexID, Vertex> getNextVertices() {
		Map<VertexID, Vertex> vertexMap = new HashMap<>();
		try {
			String strLine;
			long vertexCounter = 0;
			Vertex vertex = null;
			while ((vertexCounter < MAX_VERTICES_PER_PARTITION)
					&& ((strLine = br.readLine()) != null)) {
				vertexCounter += 1;
				vertex = GeneralUtils.generateVertex(strLine, vertexClassName);
				vertexMap.put(vertex.getID(), vertex);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vertexMap;
	}

	/**
	 * Iterator to iterate through the partitions of a graph
	 */
	@Override
	public Iterator<Partition> iterator() {
		Iterator<Partition> iter = new Iterator<Partition>() {
			/** partition counter */
			private int partitionCounter = 0;

			/** Overrides the hasNext method */
			@Override
			public boolean hasNext() {
				if (partitionCounter < numPartitions)
					return true;
				else {
					try {
						br.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					return false;
				}
			}

			/** Overrides the next method */
			@Override
			public Partition next() {
				Partition nextPartition = null;
				try {
					nextPartition = new Partition(partitionCounter,
							getNextVertices());
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				partitionCounter += 1;
				return nextPartition;
			}

			@Override
			public void remove() {
			}
		};
		return iter;
	}

	/**
	 * Gets the number of partitions
	 * 
	 * @return Returns the number of partitions
	 */
	public int getNumPartitions() {
		return numPartitions;
	}

	/**
	 * Sets the number of partitions
	 * 
	 * @param numPartitions
	 *            Represents the number of partitions
	 */
	public void setNumPartitions(int numPartitions) {
		this.numPartitions = numPartitions;
	}

}