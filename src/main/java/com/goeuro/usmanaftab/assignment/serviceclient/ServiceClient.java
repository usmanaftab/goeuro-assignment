package com.goeuro.usmanaftab.assignment.serviceclient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 
 * @author usmanaftab
 *
 */
public class ServiceClient {	
	private static final Logger logger = LoggerFactory.getLogger(ServiceClient.class);
	
	private static final String RESPONSE_PROPERTY = "Accept";
	private ResponseType responseType;

	private ServiceClient() {
		throw new AssertionError();
	}

	private ServiceClient(Builder builder) {
		this.responseType = builder.responseType;
	}

	/**
	 * Opens a HttpConnection for provided url to receive a json response and maps it on a string
	 * and returns a StringReader pointing to the string.
	 * In case method is not able to translate url it throws a RuntimeException.
	 * In case method is not able to receive success resopnse from EndPoint it throws a RuntimeException. 
	 * @param url defines endpoint.
	 * @return the StringReader point to Json string.
	 */
	public StringReader makeGetRequest(String url) {
		HttpURLConnection httpConnection = null;
		StringBuilder sb = new StringBuilder();
		
		try{
			URL restServiceURL = new URL(url);
			httpConnection = (HttpURLConnection) restServiceURL.openConnection();
			httpConnection.setRequestMethod(RequestType.GET.getValue());
			httpConnection.setRequestProperty(RESPONSE_PROPERTY, responseType.getValue());

			logger.info("Create request for url: " + restServiceURL.toString());
			if (httpConnection.getResponseCode() != ResponseCode.SUCCESS.getValue()) {
				throw new RuntimeException("HTTP GET Request Failed with Error code : "
						+ httpConnection.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
					(httpConnection.getInputStream())));
			String output;
			while((output = br.readLine()) != null) {
				sb.append(output);
			}
		} catch (MalformedURLException ex ){
			logger.error("Cannot create url: " + url);
			throw new RuntimeException("Cannot create url: " + url, ex);
		} catch (Exception ex){
			logger.error("Error while making get request for url: " + url + "\nError: " + ex.getMessage());
			throw new RuntimeException("Error while making get request for url: " + url + "\nError: " + ex.getMessage(), ex);
		}finally {
			if (httpConnection != null) {
				httpConnection.disconnect();
			}
		}
		logger.debug("Response for url: " + url + " is \n" + sb.toString());
		return new StringReader(sb.toString());
	}

	public static class Builder {
		private ResponseType responseType;

		public Builder(ResponseType responseType) {
			this.responseType = responseType;
		}

		public ServiceClient build() {
			return new ServiceClient(this);
		}
	}

	public static enum RequestType {
		GET("GET"),
		POST("POST");
		//we can introduce other request types here as well.

		private String requestType;
		RequestType(String requestType){
			this.requestType = requestType;
		}

		public String getValue() {
			return requestType;
		}
	}

	public static enum ResponseType {
		JSON("application/json");
        //we can have more response types such as application/xml and etc.

		private String responseType;
		private ResponseType(String responseType) {
			this.responseType = responseType;
		}

		public String getValue() {
			return responseType;
		}
	}

	private static enum ResponseCode {
		SUCCESS(200);

		private int responseCode;
		ResponseCode(int responseCode){
			this.responseCode = responseCode;
		}

		public int getValue() {
			return responseCode;
		}
	}
}
