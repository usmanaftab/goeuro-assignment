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

import java.io.IOException;
import java.io.StringReader;

import static org.mockserver.integration.ClientAndProxy.startClientAndProxy;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;

import static com.goeuro.usmanaftab.assignment.Constants.*;

/**
 * 
 * @author usmanaftab
 *
 */
public class EvnTest {
	private static final Logger logger = LoggerFactory.getLogger(EvnTest.class);
	
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

        // for debugging test
        //proxy.dumpToLogAsJSON();
        //proxy.dumpToLogAsJava();
    }

    @Test
	public void testTest() {
		logger.info("testing - testTest");
	}
	
	@Test
	public void testMockingServer() throws IOException {
        logger.debug("testing - testMockingServer");
        mockServer.when(HttpRequest.request().withMethod("GET").withPath("/germany").withHeader(new Header("Accept", "application/json")))
                .respond(HttpResponse.response().withStatusCode(200).withHeader(new Header("Content-Type", "application/json")).withBody("Mocked Response"));

		StringReader sr = ServiceManager.instance().getServiceResponse("germany");
		Assert.assertEquals(UtilsForTest.getStringOf(sr), "Mocked Response");
	}

    @Test
    public void testAppProperties() {
        Assert.assertNotNull(AppProperties.instance().getProperty(TARGET_URL_PROP_NAME));
        Assert.assertNotNull(AppProperties.instance().getProperty(OUTPUT_FILE_PROP_NAME));
    }

}
