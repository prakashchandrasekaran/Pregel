package utility;

/**
 * Represents the properties class 
 * 
 * @author Prakash Chandrasekaran
 * @author Gautham Narayanasamy
 * @author Vijayaraghavan Subbaiah
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import exceptions.PropertyNotFoundException;

public class Props {
	/** The static props instance */
	private static Props props;
	/** Represents the properties */
	private static Properties properties;
	/** File object containing properties */
	private static File propertiesFile;
	/** file last modified */
	private static long lastModified;

	/**
	 * Constructs the props instance
	 * 
	 * @param propertiesFilePath
	 *            Represents the file path of the properties file
	 */
	private Props(String propertiesFilePath) {
		properties = new Properties();
		propertiesFile = new File(propertiesFilePath);
		loadProperties();
	}

	/**
	 * Gets the instance of the props
	 * 
	 * @return returns the instance of the props
	 */
	public static synchronized Props getInstance() {
		if (props == null) {
			props = new Props("config/system.properties");
		}
		return props;
	}

	/**
	 * Checks if the properties file has been modified after it was loaded.
	 */
	private static void checkProperties() {
		if (propertiesFile.lastModified() > lastModified) {
			loadProperties();
		}
	}

	/**
	 * Loads the properties file
	 */
	private static void loadProperties() {
		lastModified = System.currentTimeMillis();
		InputStream in = null;
		try {
			in = new FileInputStream(propertiesFile);
			properties.load(in);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Gets the string property for the given key
	 * 
	 * @param key
	 *            represents the property member
	 * @return Returns the string property for the given key
	 * @throws PropertyNotFoundException
	 */
	public String getStringProperty(String key)
			throws PropertyNotFoundException {
		checkProperties();
		String value = properties.getProperty(key);
		if (value == null)
			throw new PropertyNotFoundException(key);
		return value;
	}

	/**
	 * Gets the integer property for the given key
	 * 
	 * @param key
	 *            represents the property member
	 * @return Returns the integer property for the given key
	 * @throws PropertyNotFoundException
	 */
	public Integer getIntProperty(String key) throws PropertyNotFoundException {
		checkProperties();
		String value = properties.getProperty(key);
		if (value == null)
			throw new PropertyNotFoundException(key);
		return Integer.parseInt(value);
	}

	/**
	 * Gets the double property for the given key
	 * 
	 * @param key
	 *            represents the property member
	 * @return Returns the double property for the given key
	 * @throws PropertyNotFoundException
	 */
	public Double getDoubleProperty(String key)
			throws PropertyNotFoundException {
		checkProperties();
		String value = properties.getProperty(key);
		if (value == null)
			throw new PropertyNotFoundException(key);
		return Double.parseDouble(value);
	}

	/**
	 * Gets the long property for the given key
	 * 
	 * @param key
	 *            represents the property member
	 * @return Returns the long property for the given key
	 * @throws PropertyNotFoundException
	 */
	public Long getLongProperty(String key) throws PropertyNotFoundException {
		checkProperties();
		String value = properties.getProperty(key);
		if (value == null)
			throw new PropertyNotFoundException(key);
		return Long.parseLong(value);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Props props = Props.getInstance();
		try {
			System.out.println(props.getStringProperty("FILE_NAME"));
			System.out.println(props.getStringProperty("NOT_FOUND_PROPERTY"));
		} catch (PropertyNotFoundException e) {
			e.printStackTrace();
		}
	}
}
