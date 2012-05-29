package utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

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
		if(props == null) {
			props = new Props("../../config/system.properties");		
		}
		return props;
	}
	
	/**
	 * Checks if the properties file has been modified after it was loaded.
	 */
	private static void checkProperties(){
		if(propertiesFile.lastModified() > lastModified){
			loadProperties();
		}
	}
	
	private static void loadProperties(){
		lastModified = System.currentTimeMillis();
		InputStream in = null;
		try{
			in = new FileInputStream(propertiesFile);
			properties.load(in);
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally{
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public String getStringProperty(String key) {
		checkProperties();
		return properties.getProperty(key);
	}
	
	public Integer getIntProperty(String key) {
		checkProperties();
		return Integer.parseInt(properties.getProperty(key));
	}
	
	public Double getDoubleProperty(String key) {
		checkProperties();
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
