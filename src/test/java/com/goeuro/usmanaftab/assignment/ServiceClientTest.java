package com.goeuro.usmanaftab.assignment;

import com.goeuro.usmanaftab.assignment.serviceclient.ServiceClient;
import com.goeuro.usmanaftab.assignment.serviceclient.ServiceClientFactory;
import org.junit.*;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.integration.ClientAndProxy;
import org.mockserver.model.Header;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.socket.PortFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringReader;

import static com.goeuro.usmanaftab.assignment.Constants.TARGET_URL_PROP_NAME;
import static org.mockserver.integration.ClientAndProxy.startClientAndProxy;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;

/**
 * Created by usmanaftab on 2/19/15.
 */
public class ServiceClientTest {

    private static final Logger logger = LoggerFactory.getLogger(ServiceClientTest.class);

    private static final String TARGET_URL = AppProperties.instance().getProperty(TARGET_URL_PROP_NAME);

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
    public void testWrongUrlCreation() {
        logger.debug("testing - testWrongUrlCreation");
        ServiceClient serviceClient = ServiceClientFactory.getJsonServiceClient();
        serviceClient.makeGetRequest("Wrong url");
    }

    @Test(expected = RuntimeException.class)
    public void testBadServerResponse() {
        logger.debug("testing - testBadServerResponse");
        mockServer.when(HttpRequest.request().withMethod("GET").withPath("/germany").withHeader(new Header("Accept", "application/json")))
                .respond(HttpResponse.response().withStatusCode(404).withHeader(new Header("Content-Type", "application/json")).withBody("Mocked Response"));

        ServiceClient serviceClient = ServiceClientFactory.getJsonServiceClient();
        serviceClient.makeGetRequest(String.format(TARGET_URL, "german"));
    }

    @Test
    public void testEverythingOk() throws IOException {
        logger.debug("testing - testEverythingOk");
        mockServer.when(HttpRequest.request().withMethod("GET").withPath("/germany").withHeader(new Header("Accept", "application/json")))
                .respond(HttpResponse.response().withStatusCode(200).withHeader(new Header("Content-Type", "application/json")).withBody("[{_id: 377387,key: null,},{_id: 471145,key: null,}]"));

        ServiceClient serviceClient = ServiceClientFactory.getJsonServiceClient();
        StringReader sr = serviceClient.makeGetRequest(String.format(TARGET_URL, "germany"));
        Assert.assertEquals(UtilsForTest.getStringOf(sr), "[{_id: 377387,key: null,},{_id: 471145,key: null,}]");
    }
}
