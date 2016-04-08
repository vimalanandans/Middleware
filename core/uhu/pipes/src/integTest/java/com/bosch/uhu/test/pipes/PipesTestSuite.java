
package com.bezirk.test.pipes;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bosch.upa.cloud.UhuWebServer;

@RunWith(Suite.class)
@SuiteClasses({ 
	CloudPipeClientTest.class, 
	PipeManagerTest.class, 
}) 
public class PipesTestSuite {
	
	private static Logger log = LoggerFactory.getLogger(PipesTestSuite.class);
	
	private static Thread webServerThread;
	
    @BeforeClass
    public static void setUpTestSuite() throws Exception {
    	log.info("setting up: " + PipesTestSuite.class.getSimpleName());
		
		// Start web server
		webServerThread = new Thread( new UhuWebServer() );
		webServerThread.start();
		Thread.sleep(2000);
    }

    @AfterClass
    public static void tearDownTestSuite() {
    	log.info("tearing down");
    }
    
    public static boolean isWebServerRunning() {
    	if (webServerThread != null && webServerThread.isAlive()) {
    		return true;
    	}
    	return false;
    }
} 
