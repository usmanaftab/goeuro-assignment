package com.goeuro.usmanaftab.assignment;

import com.goeuro.usmanaftab.assignment.serviceclient.ServiceClient;
import com.goeuro.usmanaftab.assignment.serviceclient.ServiceClientFactory;
import com.google.gson.Gson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import static com.goeuro.usmanaftab.assignment.Constants.*;

/**
 * 
 * @author usmanaftab
 *
 */
public class ServiceManager {
	private static final Logger logger = LoggerFactory.getLogger(ServiceManager.class);
	
	private static final String TARGET_URL = AppProperties.instance().getProperty(TARGET_URL_PROP_NAME);
	private static final String OUTPUT_FILE_NAME = AppProperties.instance().getProperty(OUTPUT_FILE_PROP_NAME);
	
	private static ServiceManager INSTANCE = new ServiceManager();
	
	private ServiceManager() {
		//Singleton class.
	}
	
	/**
	 * ServiceManager is a singleton class
	 * @return Singleton instance of the class
	 */
	public static ServiceManager instance() {
		return INSTANCE;
	}
	
	/**
	 * Appends queryString at the end of TARGET_URL defined in application.properties
	 * Hit RestFul webservice and write response in csv format on filesystem.
	 * @param queryString to be appended at the end of TARGET_URL.
	 */
	public void queryAndCreateCSV(String queryString) {
		ResponseVO[] responseVOs = getResponseVosFor(queryString);
		createCSVFile(responseVOs);
	}
	
	/**
	 * Appends queryString at the end of TARGET_URL defined in application.properties
	 * Hit RestFul webservice, map json response to ResponseVO[] object.
	 * @param queryString to be appended at the end of TARGET_URL.
	 * @return ResponseVO[] represents Json response.
	 */
	public ResponseVO[] getResponseVosFor(String queryString) {
		return getRespVosFromJsonStrReader(getServiceResponse(queryString));
	}
	
	/**
	 * Appends queryString at the end of TARGET_URL defined in application.properties
	 * Hit RestFul webservice and return StringReader pointing to Json response string.
	 * @param queryString to be appended at the end of TARGET_URL.
	 * @return StringReader pointing to Json response string.
	 */
	public StringReader getServiceResponse(String queryString) {
		ServiceClient endPointClient = ServiceClientFactory.getJsonServiceClient();
		return endPointClient.makeGetRequest(String.format(TARGET_URL, queryString));
	}
	
	/**
	 * Takes a string reader which must be pointing to a Json string
	 * @param reader
	 * @return ResponseVO[] represents Json.
	 */
	public ResponseVO[] getRespVosFromJsonStrReader(Reader reader) {
		Gson gson = new Gson();
		return gson.fromJson(reader, ResponseVO[].class);
	}
	
	/**
	 * Takes an array of ResponseVos and calls toString() method to write on output file 
	 * mentioned in application.properties file.
	 * @param responseVOs
	 */
	public void createCSVFile(ResponseVO[] responseVOs) {
		try {
			FileWriter writer = new FileWriter(OUTPUT_FILE_NAME);
			logger.info("ServiceManager - creating CSV file: " + OUTPUT_FILE_NAME);
			if (responseVOs != null) {
				for (ResponseVO responseVO : responseVOs) {
					logger.debug("Writing: " + responseVO.toString());
					writer.append(responseVO.toString());
					writer.append("\n");
				}
			}
			writer.flush();
			writer.close();
		} catch (IOException ex) {
			logger.error("Unable to create CSV file");
			throw new RuntimeException(ex.getMessage(), ex);
		}
		
	}

}
