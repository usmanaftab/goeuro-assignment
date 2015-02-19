package com.goeuro.usmanaftab.assignment;

import static org.mockserver.integration.ClientAndProxy.startClientAndProxy;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;

import java.io.IOException;
import java.io.StringReader;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.integration.ClientAndProxy;
import org.mockserver.model.Header;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.socket.PortFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.goeuro.usmanaftab.assignment.serviceclient.ServiceClient;
import com.goeuro.usmanaftab.assignment.serviceclient.ServiceClientFactory;

/**
 * Created by usmanaftab on 2/19/15.
 */
public class ServiceClientTest {

    private static final Logger logger = LoggerFactory.getLogger(ServiceClientTest.class);

    private MockServerClient mockServer;
    private static ClientAndProxy proxy   ;

    @BeforeClass
    public static void startProxy() {
        proxy = startClientAndProxy(PortFactory.findFreePort());
    }

    @AfterClass
    public static void stopProxy() {
        proxy.stop();
    }

    @Before
    public void startMockServer() {
        mockServer = startClientAndServer(8888);
        proxy.reset();
    }

    @After
    public void stopMockServer() {
        mockServer.stop();
    }

    @Test(expected = RuntimeException.class)
    public void testBadServerResponse() {
        logger.debug("testing - testBadServerResponse");
        mockServer.when(HttpRequest.request().withMethod("GET").withPath("/germany").withHeader(new Header("Accept", "application/json")))
                .respond(HttpResponse.response().withStatusCode(404).withHeader(new Header("Content-Type", "application/json")).withBody("Mocked Response"));

        ServiceClient serviceClient = ServiceClientFactory.getJsonServiceClient();
        serviceClient.makeGetRequest(ServiceManager.instance().getURI("germany"));
    }

    @Test
    public void testEverythingOk() throws IOException {
        logger.debug("testing - testEverythingOk");
        mockServer.when(HttpRequest.request().withMethod("GET").withPath("/germany").withHeader(new Header("Accept", "application/json")))
                .respond(HttpResponse.response().withStatusCode(200).withHeader(new Header("Content-Type", "application/json")).withBody("[{_id: 377387,key: null,},{_id: 471145,key: null,}]"));

        ServiceClient serviceClient = ServiceClientFactory.getJsonServiceClient();
        StringReader sr = serviceClient.makeGetRequest(ServiceManager.instance().getURI("germany"));
        Assert.assertEquals(UtilsForTest.getStringOf(sr), "[{_id: 377387,key: null,},{_id: 471145,key: null,}]");
    }
}
