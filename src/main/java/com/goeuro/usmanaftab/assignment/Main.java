package com.goeuro.usmanaftab.assignment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
	private static final Logger logger = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) {
		try{
            if (args.length ==0){
                throw new IllegalArgumentException("Input is not provided.");
            }

            String queryString = args[0];
            logger.info("Main - input: " + queryString);

            ServiceManager.instance().queryAndCreateCSV(queryString);
        } catch(Exception ex){
            logger.info(ex.getMessage());
            logger.error("Exception", ex);
            System.exit(-1);
        }
	}
}
