package api;

public class Edge {

	private String sourceId;
	private String destId;
	private double weight;
	
	public Edge(String src, String dst, double w) {
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
		Edge edge = new Edge("source", "dest", 123.456);
		System.out.println(edge);
	}

}
