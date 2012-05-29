package utility;

import java.util.LinkedList;
import java.util.List;
import exceptions.InvalidVertexLineException;
import api.Edge;
import api.Vertex;

/**
Utility class for generating Vertex Object from VertexInputLine
*/
public class VertexGenerator {
 
 
	 private static VertexGenerator instance = null; 
	 private static String sourceVertexDelimiter;
	 private static String edgesDelimiter;
	 private static String vertexWeightDelimiter;
	 private static Props props;
	 private VertexGenerator() { }
	
	 public static synchronized VertexGenerator getInstance() {
		  if (instance == null) {
			  instance = new VertexGenerator();
		  }
		  props = Props.getInstance();
		  sourceVertexDelimiter = props.getStringProperty("VERTEX_LIST_SEPARATOR");
		  edgesDelimiter = props.getStringProperty("LIST_VERTEX_SEPARATOR");
		  vertexWeightDelimiter = props.getStringProperty("LIST_VERTEX_WEIGHT_SEPARATOR");
		  return instance;
	 }
	 
	 /**
	  * generate vertex object from vertexLine
	  * <br> vertexLine is of the form sourceVertex-Vertex1:Weight1,Vertex2:Weight2
	  * <br> Example : 1-2:10,3:15,4:12
	  * @param vertexLine
	  * @return
	  * @throws InvalidVertexLineException 
	  */
	 public static Vertex generate(String vertexLine) throws InvalidVertexLineException {
		  if(vertexLine == null || vertexLine.length() == 0) 
			  throw new InvalidVertexLineException(vertexLine, "Vertex Line is Null");
		  
		  
		  String[] vertexSplit = vertexLine.split(sourceVertexDelimiter);
		  // Source Vertex
		  String sourceVertex = vertexSplit[0];
		 
		  // List of Edges
		  String[] edges = vertexSplit[1].split(edgesDelimiter);
		  List<Edge> outGoingEdges = new LinkedList<>();
		  String[] edgeData = null;
		  String destVertex = null;
		  Double edgeWeight = 0.0;
		  for(String edge : edges) {
			  edgeData = edge.split(vertexWeightDelimiter);
			  destVertex = edgeData[0];
			  edgeWeight = Double.parseDouble(edgeData[1]);
			  outGoingEdges.add(new Edge(sourceVertex, destVertex, edgeWeight));
		  }
		  
		  return new Vertex(sourceVertex, outGoingEdges);
 }

 /**
 */
 public static void main(String args[]) {
	 VertexGenerator vg = VertexGenerator.getInstance();
	 try {
		System.out.println(vg.generate("1-2:10,3:15,4:12"));
	} catch (InvalidVertexLineException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} }
}
