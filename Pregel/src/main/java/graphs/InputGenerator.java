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

/**
 * Generates an adjacency list representing a graph based on the number of vertices and the range of weights to be assigned to the edges.
 */
public class InputGenerator {
	
	/** Represents the separator between the vertex and its adjacency list. */
	private final static String VERTEX_LIST_SEPARATOR = "-";
	
	/** Represents the separator between a vertex and its weight in the adjacency list. */
	private final static String LIST_VERTEX_WEIGHT_SEPARATOR = ":";
	
	/** Represents the separator between two elements in the adjacency list. */
	private final static String LIST_VERTEX_SEPARATOR = ",";
		
	/** Represents the line separator. */
	private final static String LINE_SEPARATOR = "\n";
	
	/** The number of vertices. */
	private int numVertices;
	
	/** The min edge weight. */
	private double minEdgeWeight;
	
	/** The max edge weight. */
	private double maxEdgeWeight;
	
	/** The output file path. */
	private String outputFilePath;
	
	/**
	 * Instantiates a new input generator.
	 *
	 * @param numVertices the number of vertices in the graph
	 * @param minEdgeWeight the minimum edge weight
	 * @param maxEdgeWeight the maximum edge weight
	 * @param outputFilePath the output file path
	 */
	public InputGenerator(int numVertices, double minEdgeWeight, double maxEdgeWeight, String outputFilePath){
		this.numVertices = numVertices;
		this.minEdgeWeight = minEdgeWeight;
		this.maxEdgeWeight = maxEdgeWeight;
		this.outputFilePath = outputFilePath;
	}
	
	
	/**
	 * Generate the input to be processed by graph generator.
	 *
	 * @return string
	 */
	public void generateInput(){
		// Assuming that the numVertices is a perfect square.
		int squareRoot = (int)Math.sqrt(numVertices);
		Random random = new Random();
		StringBuilder adjacencyList = new StringBuilder();
		
		adjacencyList.append(numVertices);
		adjacencyList.append(LINE_SEPARATOR);
		
		for (int vertexId = 1; vertexId <= numVertices; vertexId++){
			int rightVertexId = (vertexId % squareRoot != 0) ? (vertexId + 1) : 0;
			int topVertexId = ((vertexId + squareRoot) <= numVertices) ? (vertexId + squareRoot) : 0;
			adjacencyList.append(vertexId).append(VERTEX_LIST_SEPARATOR);
		
			// if the right vertex exists, add it to the adjacency list	
			if(rightVertexId != 0){
				double weight = minEdgeWeight + (maxEdgeWeight - minEdgeWeight) * random.nextDouble();
				adjacencyList.append(rightVertexId).append(LIST_VERTEX_WEIGHT_SEPARATOR).append(weight);
			}
			
			// if the top vertex exists, add it to the adjacency list
			if(topVertexId != 0){
				if(rightVertexId != 0){
					adjacencyList.append(LIST_VERTEX_SEPARATOR);
				}
				double weight = minEdgeWeight + (maxEdgeWeight - minEdgeWeight) * random.nextDouble();
				adjacencyList.append(topVertexId).append(LIST_VERTEX_WEIGHT_SEPARATOR).append(weight);
			}
			adjacencyList.append(LINE_SEPARATOR);
		}
		//Write the graph input to the output file path
		writeToFile(adjacencyList.toString());
	}
		
	/**
	 * Write the graph output to the output file path.
	 *
	 * @param output the output
	 */
	private void writeToFile(String output){	
		BufferedWriter writefile = null;
		try {
			writefile = new BufferedWriter(new FileWriter(outputFilePath));
			writefile.write(output);			
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally{
			try {
				writefile.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws Exception the exception
	 */
	public static void main(String[] args) throws Exception {
		int numVertices = 16;//Integer.parseInt(args[0]);
		double minEdgeWeight = 1;//Double.parseDouble(args[1]);
		double maxEdgeWeight = 100;//Double.parseDouble(args[2]);
		String outputFilePath = "/storage/shelf2/ucsb/cs290b/output.txt";
		InputGenerator inputGenerator = new InputGenerator(numVertices, minEdgeWeight, maxEdgeWeight, outputFilePath);		
		inputGenerator.generateInput();
		
		System.out.println("File generated successfully!");
	}
	
}
