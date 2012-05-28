package utility;

import java.util.LinkedList;
import java.util.List;

import api.Edge;
import api.Vertex;

/**
Utility class for generating Vertex Object from VertexInputLine
*/
public class VertexGenerator {
  
 private static VertexGenerator instance = null; 
 private static String sourceVertexDelimiter = "-";
 private static String edgesDelimiter = ",";
 private static String vertexWeightDelimiter = ":";

 private VertexGenerator() { }

 public static synchronized VertexGenerator getInstance() {
  if (instance == null) {
   instance = new VertexGenerator();
  }
  return instance;
 }
 
 /**
  * generate vertex object from vertexLine
  * <br> vertexLine is of the form sourceVertex-Vertex1:Weight1,Vertex2:Weight2
  * <br> Example : 1-2:10,3:15,4:12
  * @param vertexLine
  * @return
  */
 public Vertex generate(String vertexLine) {
  // if(vertexLine != null) { throw some error }
  Vertex vertex = new Vertex();
  String[] vertexSplit = vertexLine.split(sourceVertexDelimiter);
  String sourceVertex = vertexSplit[0];
  vertex.setId(sourceVertex);
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
  vertex.setOutgoingEdges(outGoingEdges);
  return vertex;
 }

 /**
 */
 public static void main(String args[]) {
 }
}
