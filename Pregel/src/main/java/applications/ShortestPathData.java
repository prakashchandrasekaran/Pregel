package applications;

import api.Data;

/**
 * Defines the implementation of Data that is specific to the Shortest-Path
 * graph problem.
 * 
 * @author Prakash Chandrasekaran
 * @author Gautham Narayanasamy
 * @author Vijayaraghavan Subbaiah
 */
public class ShortestPathData implements Data<Double> {
	/** Represents the value of the shortest path data */
	Double value;
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -3455938165928334533L;

	/**
	 * Constructs the shortest path data
	 * 
	 * @param value
	 *            Represents the value of the shortest path data
	 */
	public ShortestPathData(Double value) {
		this.value = value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see api.Data#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Data<Double> other) {
		if (this.getValue() == other.getValue()) {
			return 0;
		} else if (this.getValue() > other.getValue()) {
			return 1;
		}
		return -1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see api.Data#getValue()
	 */
	@Override
	public Double getValue() {
		return value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see api.Data#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(Double value) {
		this.value = value;
	}

	/**
	 * String representation of the shortest path data
	 */
	public String toString() {
		return Double.toString(this.value);
	}
}
