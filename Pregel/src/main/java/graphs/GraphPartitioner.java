package graphs;

import java.util.*;

/**
 * Constructs the graph partitions
 * 
 * @author Prakash Chandrasekaran
 * @author Gautham Narayanasamy
 * @author Vijayaraghavan Subbaiah
 * 
 */
public class GraphPartitioner implements Iterable<Integer> {
	/**
	 * Constructs the graph partitions
	 */
	double numVertices;
	double numWorkerManager;
	double numWorker;
	double numPartitions;
	public static final double MAX_VERTICES_PER_PARTITION = 1000;

	public GraphPartitioner() {

	}

	@Override
	public Iterator<Integer> iterator() {
		Iterator<Integer> iter = new Iterator<Integer>() {

			private int partitionCounter = 0;

			@Override
			public boolean hasNext() {
				return partitionCounter < numPartitions;
			}

			@Override
			public Integer next() {
				partitionCounter += 1;
				return 1;
			}

			@Override
			public void remove() {
			}
		};
		return iter;

	}
}