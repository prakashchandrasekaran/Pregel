package utility;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
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
 * General Utility class.
 * 
 * @author Prakash Chandrasekaran
 * @author Gautham Narayanasamy
 * @author Vijayaraghavan Subbaiah
 */
public class GeneralUtils {

	/** The props. */
	private static Props props = Props.getInstance();

	/** The source vertex delimiter. */
	private static String sourceVertexDelimiter;

	/** The edges delimiter. */
	private static String edgesDelimiter;

	/** The vertex weight delimiter. */
	private static String vertexWeightDelimiter;

	/** The max vertices per partition. */
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
	 * Example : 1-2:10,3:15,4:12.
	 * 
	 * @param vertexLine
	 *            the vertex line
	 * @param vertexClassName
	 *            the vertex class name
	 * @return vertex
	 * @throws InvalidVertexLineException
	 *             the invalid vertex line exception
	 */
	public static Vertex generateVertex(String vertexLine,
			String vertexClassName) throws InvalidVertexLineException {
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
		if (vertexSplit.length > 1) {
			// List of Edges
			String[] edges = vertexSplit[1].split(edgesDelimiter);

			String[] edgeData = null;
			VertexID destVertex = null;
			Double edgeWeight = 0.0;
			for (String edge : edges) {
				edgeData = edge.split(vertexWeightDelimiter);
				vertexIdentifier = Long.parseLong(edgeData[0]);
				destVertex = new VertexID(getPartitionID(vertexIdentifier),
						vertexIdentifier);
				edgeWeight = Double.parseDouble(edgeData[1]);
				outGoingEdges
						.add(new Edge(sourceVertex, destVertex, edgeWeight));
			}
		}
		// Create a new instance of the vertex class that the application
		// programmer passes.
		try {
			Class<?> c = Class.forName(vertexClassName);
			Constructor<?> constructor = c.getConstructor(VertexID.class,
					List.class);
			vertex = (Vertex) constructor.newInstance(new Object[] {
					sourceVertex, outGoingEdges });

		} catch (ClassNotFoundException | NoSuchMethodException
				| SecurityException | InstantiationException
				| IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			e.printStackTrace();
		}
		return vertex;
	}

	/**
	 * for a given vertexId, partitionId is computed and returned, partitionId
	 * starts from 0.
	 * 
	 * @param vertexId
	 *            , input vertedId for which PatitionId is computed
	 * @return respective partition Id
	 */
	public static int getPartitionID(long vertexId) {
		int partitionId = (int) (vertexId / maxVerticesPerPartition);
		return partitionId;
	}

	/**
	 * Serialize the object to the file specified by the file path.
<<<<<<< HEAD
	 * 
	 * @param filePath
	 *            the file path
	 * @param obj
	 *            the object to be serialized
=======
	 *
	 * @param filePath the file path
	 * @param obj the object to be serialized
	 * @return true, if successful
>>>>>>> 7600b67fcc11d75a41b22c480f830549f201fa50
	 */
	public static boolean serialize(String filePath, Object obj) {
		boolean serialized = false;
		FileOutputStream fileOutputStream = null;
		ObjectOutputStream objectOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream(filePath);
			objectOutputStream = new ObjectOutputStream(fileOutputStream);
			objectOutputStream.writeObject(obj);
			objectOutputStream.flush();
			objectOutputStream.close();
			serialized = true;
		} catch (IOException e) {
			e.printStackTrace();
			serialized = false;
		} finally {
			try {
				fileOutputStream.close();
				objectOutputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
				serialized = false;
			}
		}
		return serialized;
	}

	/**
	 * Deserialize the object from the file specified by the file path.
	 * 
	 * @param filePath
	 *            the file path
	 * @return obj the deserialized object
	 */
	public static Object deserialize(String filePath) {
		FileInputStream fileInputStream = null;
		ObjectInputStream objectInputStream = null;
		Object obj = null;
		try {
			fileInputStream = new FileInputStream(filePath);
			objectInputStream = new ObjectInputStream(fileInputStream);
			obj = objectInputStream.readObject();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				fileInputStream.close();
				objectInputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return obj;
	}

	/**
	 * Write to file.
	 * 
	 * @param outputFilePath
	 *            the output file path
	 * @param contents
	 *            the contents to be written
	 * @param append
	 *            parameter indicating whether or not to write in append mode.
	 */
	public static void writeToFile(String outputFilePath, String contents,
			boolean append) {
		System.out.println("GeneralUtils: writeToFile " + outputFilePath);
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(outputFilePath, append));
			writer.write(contents);
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Rename file.
	 * 
	 * @param oldFilePath
	 *            the old file path
	 * @param newFilePath
	 *            the new file path
	 */
	public static void renameFile(String oldFilePath, String newFilePath) {
		File f = new File(oldFilePath);
		f.renameTo(new File(newFilePath));
	}
	
	/**
	 * Removes the file.
	 *
	 * @param filePath the file path
	 */
	public static void removeFile(String filePath){
		System.out.println("GeneralUtils: removeFile " + filePath );
		File f = new File(filePath);
		f.delete();
	}

	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 */
	public static void main(String args[]) {
		try {
			System.out.println(GeneralUtils.generateVertex("1-2:10,3:15,4:12",
					"Vertex"));
			System.out.println(GeneralUtils.getPartitionID(123456));
		} catch (InvalidVertexLineException e) {
			e.printStackTrace();
		}
	}
}
