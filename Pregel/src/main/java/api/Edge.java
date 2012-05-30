package api;

public class Edge {

	private long sourceId;
	private long destId;
	private double weight;
	
	
	public Edge(long src, long dst, double w) {
		sourceId = src;
		destId = dst;
		weight = w;
	}
	
	public String toString() {
		return "(" + sourceId + "," + destId + "-" + weight + ")";
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Edge edge = new Edge(1, 2, 123.456);
		System.out.println(edge);
	}

}
