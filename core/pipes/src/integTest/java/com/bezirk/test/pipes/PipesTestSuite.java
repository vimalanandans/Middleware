package com.bezirk.test.pipes;

import com.bezirk.cloud.BezirkWebServer;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(Suite.class)
@SuiteClasses({
        CloudPipeClientTest.class,
        PipeManagerTest.class,
})
public class PipesTestSuite {
    private static final Logger logger = LoggerFactory.getLogger(PipesTestSuite.class);

    private static Thread webServerThread;

    @BeforeClass
    public static void setUpTestSuite() throws Exception {
        logger.info("setting up: " + PipesTestSuite.class.getSimpleName());

        // Start web server
        webServerThread = new Thread(new BezirkWebServer());
        webServerThread.start();
        Thread.sleep(2000);
    }

    @AfterClass
    public static void tearDownTestSuite() {
        logger.info("tearing down");
    }

    public static boolean isWebServerRunning() {
        if (webServerThread != null && webServerThread.isAlive()) {
            return true;
        }
        return false;
    }
} 
