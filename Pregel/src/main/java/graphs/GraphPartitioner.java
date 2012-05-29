package graphs;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import utility.Props;
import utility.VertexGenerator;
import api.Partition;
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
	/**
	 * Constructs the graph partitions
	 */
	private long numVertices;
	private long numWorkerManager;
	private long numWorker;
	private long numPartitions;
	String fileName;
	BufferedReader br;
	public static final long MAX_VERTICES_PER_PARTITION = Props.getInstance().getLongProperty("MAX_VERTICES_PER_PARTITION");
	
	/**
	 * 
	 * @param fileName Represents the input graph generated file
	 * @param numWorkerManager Represents the number of worker nodes registered for computations
	 * @param numWorker Represents the number of total number of worker threads available for computations  
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	public GraphPartitioner(String fileName, long numWorkerManager,
			long numWorker) throws NumberFormatException, IOException {
		this.fileName = fileName;
		this.numWorkerManager = numWorkerManager;
		this.numWorker = numWorker;
		br = new BufferedReader(new FileReader(fileName));
		numVertices = Long.parseLong(br.readLine());
		if (numVertices < MAX_VERTICES_PER_PARTITION)
			numPartitions = 1;
		else
		{
			numPartitions = numVertices / MAX_VERTICES_PER_PARTITION;
			if(numPartitions % MAX_VERTICES_PER_PARTITION != 0)
				numPartitions +=1;
		}
	}

	/**
	 * 
	 * @return Returns list of vertices comprising a partition
	 */
	public List<Vertex> getNextVertices() {
		List<Vertex> vertexList = new ArrayList<>();
		try {
			String strLine;
			long vertexCounter = 0;
			while ( (vertexCounter < MAX_VERTICES_PER_PARTITION) && ((strLine = br.readLine()) != null)
					) {
				vertexCounter += 1;
				vertexList.add(VertexGenerator.generate(strLine));
			}
		} catch (Exception e) {
			System.err.println("File Read Error: " + e.getMessage());
		}
		return vertexList;
	}

	/**
	 * Iterator to iterate through the partitions of a graph
	 */
	@Override
	public Iterator<Partition> iterator() {
		Iterator<Partition> iter = new Iterator<Partition>() {

			private int partitionCounter = 0;

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

			@Override
			public Partition next() {
				Partition nextPartition = new Partition(getNextVertices());
				partitionCounter += 1;
				return nextPartition;
			}

			@Override
			public void remove() {
			}
		};
		return iter;
	}
}