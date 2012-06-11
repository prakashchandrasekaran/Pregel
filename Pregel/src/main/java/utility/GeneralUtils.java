package utility;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

import system.Edge;

import api.Vertex;
import exceptions.InvalidVertexLineException;
import exceptions.PropertyNotFoundException;
import graphs.VertexID;

/**
 * General Utility class
 */
public class GeneralUtils {

	private static Props props = Props.getInstance();
	private static String sourceVertexDelimiter;
	private static String edgesDelimiter;
	private static String vertexWeightDelimiter;
	private static long maxVerticesPerPartition;

	static {
		try {
			sourceVertexDelimiter = props
					.getStringProperty("VERTEX_LIST_SEPARATOR");
			edgesDelimiter = props.getStringProperty("LIST_VERTEX_SEPARATOR");
			vertexWeightDelimiter = props
					.getStringProperty("LIST_VERTEX_WEIGHT_SEPARATOR");
			maxVerticesPerPartition = props
					.getLongProperty("MAX_VERTICES_PER_PARTITION");
		} catch (PropertyNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * generate vertex object from vertexLine <br>
	 * vertexLine is of the form sourceVertex-Vertex1:Weight1,Vertex2:Weight2 <br>
	 * Example : 1-2:10,3:15,4:12
	 * 
	 * @param vertexLine
	 * @return
	 * @throws InvalidVertexLineException
	 */
	public static Vertex generateVertex(String vertexLine, String vertexClassName)
			throws InvalidVertexLineException {
		if (vertexLine == null || vertexLine.length() == 0)
			throw new InvalidVertexLineException(vertexLine,
					"Vertex Line is Null");
		
		Vertex vertex = null;
		String[] vertexSplit = vertexLine.split(sourceVertexDelimiter);

		// Source Vertex
		long vertexIdentifier = Long.parseLong(vertexSplit[0]);
		VertexID sourceVertex = new VertexID(
				(int) (vertexIdentifier / maxVerticesPerPartition),
				vertexIdentifier);

		List<Edge> outGoingEdges = new LinkedList<Edge>();
		// A vertex may not have any outgoing edges.
		if(vertexSplit.length > 1){
			// List of Edges
			String[] edges = vertexSplit[1].split(edgesDelimiter);
			
			String[] edgeData = null;
			VertexID destVertex = null;
			Double edgeWeight = 0.0;
			for (String edge : edges) {
				edgeData = edge.split(vertexWeightDelimiter);
				vertexIdentifier = Long.parseLong(edgeData[0]);
				destVertex = new VertexID(
						getPartitionID(vertexIdentifier),
						vertexIdentifier);
				edgeWeight = Double.parseDouble(edgeData[1]);
				outGoingEdges.add(new Edge(sourceVertex, destVertex, edgeWeight));
			}
		}
		// Create a new instance of the vertex class that the application programmer passes.
		try {
			Class<?> c = Class.forName(vertexClassName);		
			Constructor<?> constructor = c.getConstructor(VertexID.class, List.class);
			vertex = (Vertex) constructor.newInstance(new Object[]{sourceVertex, outGoingEdges});
			
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {			
			e.printStackTrace();			
		}	
		return vertex;
	}

	/**
	 * for a given vertexId, partitionId is computed and returned, partitionId
	 * starts from 0
	 * 
	 * @param vertexId
	 *            , input vertedId for which PatitionId is computed
	 * @return respective partition Id
	 */
	public static int getPartitionID(long vertexId) {
		int partitionId = (int) (vertexId / maxVerticesPerPartition);
		return partitionId;
	}
	
	public static void serialize(String filePath, Object obj){
		FileOutputStream fileOutputStream = null;
		ObjectOutputStream objectOutputStream = null;
		try {			
			fileOutputStream = new FileOutputStream(filePath); 
			objectOutputStream = new ObjectOutputStream(fileOutputStream); 
			objectOutputStream.writeObject(obj); 
			objectOutputStream.flush(); 
			objectOutputStream.close();
		} catch (IOException e) {			
			e.printStackTrace();
		}
		finally{
			try {
				fileOutputStream.close();			
				objectOutputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	
	public static void main(String args[]) {
		try {
			System.out.println(GeneralUtils.generateVertex("1-2:10,3:15,4:12", "Vertex"));
			System.out.println(GeneralUtils.getPartitionID(123456));
		} catch (InvalidVertexLineException e) {
			e.printStackTrace();
		}
	}
}
