package utility;

import graphs.VertexID;
import java.util.LinkedList;
import java.util.List;
import exceptions.InvalidVertexLineException;
import exceptions.PropertyNotFoundException;
import api.Edge;
import api.Vertex;

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
	public static Vertex generateVertex(String vertexLine)
			throws InvalidVertexLineException {
		if (vertexLine == null || vertexLine.length() == 0)
			throw new InvalidVertexLineException(vertexLine,
					"Vertex Line is Null");

		String[] vertexSplit = vertexLine.split(sourceVertexDelimiter);

		// Source Vertex
		long vertexIdentifier = Long.parseLong(vertexSplit[0]);
		VertexID sourceVertex = new VertexID(
				(int) (vertexIdentifier / maxVerticesPerPartition),
				vertexIdentifier);

		// List of Edges
		String[] edges = vertexSplit[1].split(edgesDelimiter);
		List<Edge> outGoingEdges = new LinkedList<Edge>();
		String[] edgeData = null;
		VertexID destVertex = null;
		Double edgeWeight = 0.0;
		for (String edge : edges) {
			edgeData = edge.split(vertexWeightDelimiter);
			vertexIdentifier = Long.parseLong(edgeData[0]);
			destVertex = new VertexID(
					(int) (vertexIdentifier / maxVerticesPerPartition),
					vertexIdentifier);
			edgeWeight = Double.parseDouble(edgeData[1]);
			outGoingEdges.add(new Edge(sourceVertex, destVertex, edgeWeight));
		}

		return new Vertex(sourceVertex, outGoingEdges);
	}

	/**
	 * for a given vertexId, partitionId is computed and returned, partitionId
	 * starts from 0
	 * 
	 * @param vertexId
	 *            , input vertedId for which PatitionId is computed
	 * @return respective partition Id
	 */
	public static int getPartitionId(long vertexId) {
		int partitionId = (int) (vertexId / maxVerticesPerPartition);
		return partitionId;
	}

	public static void main(String args[]) {
		try {
			System.out.println(GeneralUtils.generateVertex("1-2:10,3:15,4:12"));
			System.out.println(GeneralUtils.getPartitionId(123456));
		} catch (InvalidVertexLineException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
