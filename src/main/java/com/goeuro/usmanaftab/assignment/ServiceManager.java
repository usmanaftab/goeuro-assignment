package com.goeuro.usmanaftab.assignment;

import static com.goeuro.usmanaftab.assignment.Constants.OUTPUT_FILE_PROP_NAME;
import static com.goeuro.usmanaftab.assignment.Constants.TARGET_URL_PROP_NAME;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.net.URLEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.goeuro.usmanaftab.assignment.serviceclient.ServiceClient;
import com.goeuro.usmanaftab.assignment.serviceclient.ServiceClientFactory;
import com.google.gson.Gson;

/**
 * 
 * @author usmanaftab
 *
 */
public class ServiceManager {
	private static final Logger logger = LoggerFactory.getLogger(ServiceManager.class);
	
	private static final String TARGET_URL = AppProperties.instance().getProperty(TARGET_URL_PROP_NAME);
	private static final String OUTPUT_FILE_NAME = AppProperties.instance().getProperty(OUTPUT_FILE_PROP_NAME);
	private static final String ENCODING_SCHEME = "ASCII";
	
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
	 * Th
	 * @param queryString to be appended at the end of TARGET_URL.
	 * @return StringReader pointing to Json response string.
	 * @throws runtime exception if its not able to construct a valid url.
	 */
	public StringReader getServiceResponse(String queryString) {
		ServiceClient endPointClient = ServiceClientFactory.getJsonServiceClient();
		return endPointClient.makeGetRequest(getURI(queryString));
	}
	
	/**
	 * Construct URI by encoding query string first and then appending it to TARGET_URL.
	 * @param queryString the query string.
	 * @return resultant uri.
	 * @throws an IllegalArgument exception in case method is not able to construct a valid uri.
	 */
	public URI getURI(String queryString) {
		try {
		return new URI(String.format(TARGET_URL, URLEncoder.encode(queryString, ENCODING_SCHEME).replace("+", "%20")));
		} catch (Exception ex) {
			throw new IllegalArgumentException("Unable to construct url from query string: " + queryString);
		}
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
