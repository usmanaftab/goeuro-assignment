package com.goeuro.usmanaftab.assignment;

import org.junit.*;
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

import java.io.IOException;

import static org.mockserver.integration.ClientAndProxy.startClientAndProxy;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static com.goeuro.usmanaftab.assignment.Constants.*;
import static com.goeuro.usmanaftab.assignment.ConstantsForTest.*;

/**
 * 
 * @author usmanaftab
 *
 */
public class ServiceManagerTest {

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

    @Test
    public void testEmptyResponse() throws IOException{
        logger.debug("testing - testEmptyResponse");
        mockServer.when(HttpRequest.request().withMethod("GET").withPath("/germany").withHeader(new Header("Accept", "application/json")))
                .respond(HttpResponse.response().withStatusCode(200).withHeader(new Header("Content-Type", "application/json")).withBody(""));

        ResponseVO[] responseVOs = ServiceManager.instance().getResponseVosFor("germany");
        Assert.assertNull(responseVOs);
        ServiceManager.instance().createCSVFile(responseVOs);
        String outputString = UtilsForTest.readFile(AppProperties.instance().getProperty(OUTPUT_FILE_PROP_NAME));
        Assert.assertEquals(outputString, "");
    }

    @Test
    public void testSingleResponse() throws Exception {
        logger.debug("testing - testSingleResponse");
        String expectedResponse = UtilsForTest.readFileFromStream(SINGLE_ROW_FILENAME);
        mockServer.when(HttpRequest.request().withMethod("GET").withPath("/germany").withHeader(new Header("Accept", "application/json")))
                .respond(HttpResponse.response().withStatusCode(200).withHeader(new Header("Content-Type", "application/json")).withBody(expectedResponse));

        ResponseVO[] responseVOs = ServiceManager.instance().getResponseVosFor("germany");
        Assert.assertNotNull(responseVOs);
        Assert.assertEquals(responseVOs.length, 1);
        ResponseVO responseVO = responseVOs[0];
        Assert.assertEquals(responseVO.get_id(), 377387);
        Assert.assertEquals(responseVO.getName(), "Waldkirchen (Niederbay.)");
        Assert.assertEquals(responseVO.getFullName(), "Waldkirchen (Niederbay.), Germany");
        Assert.assertEquals(responseVO.getIata_airport_code(), null);
        Assert.assertEquals(responseVO.getType(), "location");
        Assert.assertEquals(responseVO.getCountry(), "Germany");
        Assert.assertEquals(responseVO.getGeo_position().getLatitude() == 48.732540130615234, true);
        Assert.assertEquals(responseVO.getGeo_position().getLongitude() == 13.600847244262695, true);
        Assert.assertEquals(responseVO.getLocationId(), 9568);
        Assert.assertEquals(responseVO.isInEurope(), true);
        Assert.assertEquals(responseVO.getCountryCode(), "DE");
        Assert.assertEquals(responseVO.isCoreCountry(), true);
        Assert.assertEquals(responseVO.getDistance(), null);
    }

    @Test
    public void testLargeResponse() throws IOException {
        logger.debug("testing - testLargeResponse");
        String expectedResponse = UtilsForTest.readFileFromStream(LARGE_FILENAME);
        mockServer.when(HttpRequest.request().withMethod("GET").withPath("/germany").withHeader(new Header("Accept", "application/json")))
                .respond(HttpResponse.response().withStatusCode(200).withHeader(new Header("Content-Type", "application/json")).withBody(expectedResponse));

        ResponseVO[] responseVOs = ServiceManager.instance().getResponseVosFor("germany");
        Assert.assertNotNull(responseVOs);
        Assert.assertEquals(responseVOs.length, 48);
    }

    @Test
    public void testOutput() throws IOException {
        logger.debug("testing - testOutput");
        String expectedResponse = UtilsForTest.readFileFromStream(SINGLE_ROW_FILENAME);
        mockServer.when(HttpRequest.request().withMethod("GET").withPath("/germany").withHeader(new Header("Accept", "application/json")))
                .respond(HttpResponse.response().withStatusCode(200).withHeader(new Header("Content-Type", "application/json")).withBody(expectedResponse));

        ServiceManager.instance().queryAndCreateCSV("germany");
        String outputString = UtilsForTest.readFile(AppProperties.instance().getProperty(OUTPUT_FILE_PROP_NAME));
        Assert.assertEquals(outputString, "377387, Waldkirchen (Niederbay.), location, 48.732540130615234, 13.600847244262695");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testWrongUrlCreation() {
        logger.debug("testing - testWrongUrlCreation");
        ServiceManager.instance().getURI(null);
    }
}
