package com.goeuro.usmanaftab.assignment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Properties;

import static com.goeuro.usmanaftab.assignment.Constants.APP_PROP_FILE_NAME;

/**
 * 
 * @author usmanaftab
 *
 */
public class AppProperties {
	public static final Logger logger = LoggerFactory.getLogger(AppProperties.class);
	
	private Properties properties = new Properties();
	private static final AppProperties INSTANCE = new AppProperties();

	private AppProperties() {
		try {
            InputStream inputStream = AppProperties.class.getResourceAsStream(APP_PROP_FILE_NAME);
			properties.load(inputStream);
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			throw new AssertionError();
		}
	}

	public static AppProperties instance() {
		return INSTANCE;
	}

	/**
	 * Searches for the property with the specified key in this 
	 * property list. If the key is not found in this property list, 
 	 * the default property list, and its defaults, recursively, are 
 	 * then checked. The method returns null if the property is 
 	 * not found.
	 * @param key the property key.
	 * @return the value in this property list with the specified key 
 	 * value.
	 */
	public String getProperty(String key) {
		return properties.getProperty(key);
	}

}
