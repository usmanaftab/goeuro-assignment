package com.goeuro.usmanaftab.assignment.serviceclient;

import com.goeuro.usmanaftab.assignment.serviceclient.ServiceClient.ResponseType;

/**
 * 
 * @author usmanaftab
 *
 */
public class ServiceClientFactory {
	
	/**
	 * Builds and returns a singleton object of ServiceClient which generate response in Json format.
	 * @return the ServiceClient object.
	 */
	public static ServiceClient getJsonServiceClient() {
		return new ServiceClient.Builder(ResponseType.JSON).build();
	}
}
