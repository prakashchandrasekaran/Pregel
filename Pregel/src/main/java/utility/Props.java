package utility;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class Props {

	private static Props props;
	private static Properties properties;
	
	private Props(String propertyFile) {
		try {
			properties = new Properties();
			properties.load(new FileInputStream(propertyFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static synchronized Props getInstance() {
		if(props == null) {
			props = new Props("props");		
		}
		return props;
	}
	
	public String getStringProperty(String key) {
		return properties.getProperty(key);
	}
	
	public Integer getIntProperty(String key) {
		return Integer.parseInt(properties.getProperty(key));
	}
	
	public Double getDoubleProperty(String key) {
		return Double.parseDouble(properties.getProperty(key));
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Props props = Props.getInstance();
		System.out.println(props.getStringProperty("FILE_NAME"));
	}

}
