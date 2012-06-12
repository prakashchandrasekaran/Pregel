package api;

import java.io.Serializable;

/**
 * Represents an interface which should be overridden by the users to provide
 * application specific data
 * 
 * @author Prakash Chandrasekaran
 * @author Gautham Narayanasamy
 * @author Vijayaraghavan Subbaiah
 */

public interface Data<T> extends Comparable<Data<T>>, Serializable {
	/**
	 * Override this method to do a comparison between the value contained as
	 * data
	 * 
	 * @param other
	 *            Represents the other data object to be compared
	 */
	@Override
	public int compareTo(Data<T> other);

	/**
	 * Gets the value contained in Data
	 * 
	 * @return Returns the value contained in Data
	 */
	public T getValue();

	/**
	 * Sets the value given to the data value
	 * 
	 * @param value
	 *            Represents the value to be set to the data value
	 */
	public void setValue(T value);

	/**
	 * String Representation of data
	 */
	public String toString();
}
