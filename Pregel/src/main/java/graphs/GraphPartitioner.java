package graphs;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
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
	double numVertices;
	double numWorkerManager;
	double numWorker;
	double numPartitions;
	String fileName;
	FileInputStream fstream;
	BufferedReader br;
	DataInputStream in;
	public static final double MAX_VERTICES_PER_PARTITION = 1000;
	/**
	 * 
	 * @param fileName Represents the input graph generated file
	 * @param numWorkerManager Represents the number of worker nodes registered for computations
	 * @param numWorker Represents the number of total number of worker threads available for computations  
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	public GraphPartitioner(String fileName, double numWorkerManager,
			double numWorker) throws NumberFormatException, IOException {
		this.fileName = fileName;
		this.numWorkerManager = numWorkerManager;
		this.numWorker = numWorker;
		fstream = new FileInputStream(fileName);
		in = new DataInputStream(fstream);
		br = new BufferedReader(new InputStreamReader(in));
		numVertices = Double.parseDouble(br.readLine());
		if (numVertices < MAX_VERTICES_PER_PARTITION)
			numPartitions = 1;
		else
			numPartitions = numVertices / MAX_VERTICES_PER_PARTITION;
	}

	/**
	 * 
	 * @return Returns list of vertices comprising a partition
	 */
	public List<Vertex> getNextVertices() {
		List<Vertex> vertexList = new ArrayList<>();
		try {
			String strLine;
			double vertexCounter = 0;
			while (((strLine = br.readLine()) != null)
					&& (vertexCounter < MAX_VERTICES_PER_PARTITION)) {
				vertexCounter += 1;
				vertexList.add(VertexGenerator.getInstance().generate(strLine));
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
						in.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					return false;
				}
			}

			@Override
			public Partition next() {
				Partition nextPartition = getNextPartition();
				partitionCounter += 1;
				return nextPartition;
			}

			private Partition getNextPartition() {
				return new Partition(getNextVertices());
			}

			@Override
			public void remove() {
			}
		};
		return iter;
	}
}