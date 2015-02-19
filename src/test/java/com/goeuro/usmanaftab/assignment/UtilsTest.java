package com.goeuro.usmanaftab.assignment;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringReader;

/**
 * 
 * @author usmanaftab
 *
 */
public class UtilsTest {

    public static final Logger logger = LoggerFactory.getLogger(UtilsTest.class);

    @Test
    public void testUtilsStringReaderToString() throws IOException {
        logger.debug("testing - testUtilsStringReaderToString");
        Assert.assertNull(UtilsForTest.getStringOf(null));

        StringReader sr = new StringReader("Testing");
        Assert.assertEquals(UtilsForTest.getStringOf(sr), "Testing");

        sr = new StringReader("");
        Assert.assertEquals(UtilsForTest.getStringOf(sr), "");
    }
}
