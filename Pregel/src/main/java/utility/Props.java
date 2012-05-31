package utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import exceptions.PropertyNotFoundException;

public class Props {

	private static Props props;

	private static Properties properties;

	private static File propertiesFile;

	private static long lastModified;

	private Props(String propertiesFilePath) {
		properties = new Properties();
		propertiesFile = new File(propertiesFilePath);
		loadProperties();
	}

	public static synchronized Props getInstance() {
		if (props == null) {
			props = new Props("../../config/system.properties");
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

	public String getStringProperty(String key)
			throws PropertyNotFoundException {
		checkProperties();
		String value = properties.getProperty(key);
		if (value == null)
			throw new PropertyNotFoundException(key);
		return value;
	}

	public Integer getIntProperty(String key) throws PropertyNotFoundException {
		checkProperties();
		String value = properties.getProperty(key);
		if (value == null)
			throw new PropertyNotFoundException(key);
		return Integer.parseInt(value);
	}

	public Double getDoubleProperty(String key)
			throws PropertyNotFoundException {
		checkProperties();
		String value = properties.getProperty(key);
		if (value == null)
			throw new PropertyNotFoundException(key);
		return Double.parseDouble(value);
	}

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
