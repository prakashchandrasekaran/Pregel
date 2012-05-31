/*
 * @author Prakash Chandrasekaran
 * @author Gautham Narayanasamy
 * @author Vijayaraghavan Subbaiah
 */
package graphs;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import exceptions.PropertyNotFoundException;

import utility.Props;

/**
 * Generates an adjacency list representing a graph based on the number of
 * vertices and the range of weights to be assigned to the edges.
 */
public class InputGenerator {

	/** The number of vertices. */
	private int numVertices;

	/** The min edge weight. */
	private double minEdgeWeight;

	/** The max edge weight. */
	private double maxEdgeWeight;

	/** The output file path. */
	private String outputFilePath;

	/** The writer object. */
	private BufferedWriter writer;

	/**
	 * Instantiates a new input generator.
	 * 
	 * @param numVertices
	 *            the number of vertices in the graph
	 * @param minEdgeWeight
	 *            the minimum edge weight
	 * @param maxEdgeWeight
	 *            the maximum edge weight
	 * @param outputFilePath
	 *            the output file path
	 */
	public InputGenerator(int numVertices, double minEdgeWeight,
			double maxEdgeWeight, String outputFilePath) {
		this.numVertices = numVertices;
		this.minEdgeWeight = minEdgeWeight;
		this.maxEdgeWeight = maxEdgeWeight;
		this.outputFilePath = outputFilePath;
		setWriter();
	}

	/**
	 * Sets the writer.
	 */
	private void setWriter() {
		try {
			writer = new BufferedWriter(new FileWriter(outputFilePath));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Close writer.
	 */
	private void closeWriter() {
		try {
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Generate the input to be processed by graph generator. The resulting
	 * input file will be in the following format: <Number of vertices> (line 1)
	 * <Vertex1-Vertex2:Weight1,Vertex3:Weight2,...> (from line 2 onwards)
	 * Example : 1-2:10,3:15,4:12
	 * 
	 * @throws PropertyNotFoundException
	 */
	public void generateInput() throws PropertyNotFoundException {
		Props properties = Props.getInstance();
		// Assuming that the numVertices is a perfect square.
		int squareRoot = (int) Math.sqrt(numVertices);
		Random random = new Random();
		StringBuilder adjacencyList = new StringBuilder();

		// Getting the separators and buffer size from the properties file
		String vertexListSeparator = properties
				.getStringProperty("VERTEX_LIST_SEPARATOR");
		String listVertexSeparator = properties
				.getStringProperty("LIST_VERTEX_SEPARATOR");
		String listVertexWeightSeparator = properties
				.getStringProperty("LIST_VERTEX_WEIGHT_SEPARATOR");
		String lineSeparator = properties.getStringProperty("LINE_SEPARATOR");
		int inputBufferSize = properties.getIntProperty("INPUTGEN_BUFFER_SIZE");

		adjacencyList.append(numVertices);
		adjacencyList.append(lineSeparator);

		for (int vertexId = 0; vertexId < numVertices; vertexId++) {
			int rightVertexId = (vertexId % squareRoot != 0) ? (vertexId + 1)
					: 0;
			int topVertexId = ((vertexId + squareRoot) <= numVertices) ? (vertexId + squareRoot)
					: 0;
			adjacencyList.append(vertexId).append(vertexListSeparator);

			// if the right vertex exists, add it to the adjacency list
			if (rightVertexId != 0) {
				double weight = minEdgeWeight + (maxEdgeWeight - minEdgeWeight)
						* random.nextDouble();
				adjacencyList.append(rightVertexId)
						.append(listVertexWeightSeparator).append(weight);
			}

			// if the top vertex exists, add it to the adjacency list
			if (topVertexId != 0) {
				if (rightVertexId != 0) {
					adjacencyList.append(listVertexSeparator);
				}
				double weight = minEdgeWeight + (maxEdgeWeight - minEdgeWeight)
						* random.nextDouble();
				adjacencyList.append(topVertexId)
						.append(listVertexWeightSeparator).append(weight);
			}
			adjacencyList.append(lineSeparator);

			// append the content to the file in batches.
			if (vertexId % inputBufferSize == 0) {
				System.out.println("Flushing to file");
				// Write the graph input to the output file path
				writeToFile(adjacencyList.toString());
				adjacencyList = new StringBuilder();
			}
		}
		if (adjacencyList.length() > 0) {
			System.out.println("Writing the remaining content to file");
			// Write the graph input to the output file path
			writeToFile(adjacencyList.toString());
		}
		closeWriter();
	}

	/**
	 * Write the graph output to the output file path.
	 * 
	 * @param output
	 *            the output
	 */
	private void writeToFile(String output) {
		try {
			writer.write(output);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 * @throws Exception
	 *             the exception
	 */
	public static void main(String[] args) throws Exception {
		int numVertices = 16;// Integer.parseInt(args[0]);
		double minEdgeWeight = 1;// Double.parseDouble(args[1]);
		double maxEdgeWeight = 1;// Double.parseDouble(args[2]);
		String outputFilePath = "/storage/shelf2/ucsb/cs290b/output.txt";

		InputGenerator inputGenerator = new InputGenerator(numVertices,
				minEdgeWeight, maxEdgeWeight, outputFilePath);
		inputGenerator.generateInput();

		System.out.println("File generated successfully!");
	}

}
