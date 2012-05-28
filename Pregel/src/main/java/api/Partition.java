package api;
import java.util.*;
/**
 * Represents the partition of a graph consisting of set of vertices and their
 * outgoing edges
 * 
 * @author Prakash Chandrasekaran
 * @author Gautham Narayanasamy
 * @author Vijayaraghavan Subbaiah
 * 
 */

public class Partition implements Iterable{
	ArrayList<Vertex> vertexList;
	int length;
	/** 
	 * @param vertexList Represents the list of vertices in a partition
	 */
	public Partition(ArrayList<Vertex> vertexList) {
       this.vertexList = vertexList;
       length = vertexList.size();
	}
	
	@Override
    public Iterator<Vertex> iterator() {
		Iterator<Vertex> iter = vertexList.iterator();
		return iter;
    }
}
