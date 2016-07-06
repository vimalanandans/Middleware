package com.bezirk.sphere.shareProcessor;

import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.sphere.impl.ShareProcessor;
import com.bezirk.sphere.impl.SphereRegistryWrapper;
import com.bezirk.sphere.messages.ShareRequest;
import com.bezirk.sphere.testUtilities.MockSetUpUtility;
import com.bezirk.sphere.testUtilities.SphereTestUtility;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

/**
 * @author rishabh
 */
public class ProcessRequest {

    private static final MockSetUpUtility mockSetUp = new MockSetUpUtility();
    private static final Logger logger = LoggerFactory.getLogger(ProcessRequest.class);
    private static ShareProcessor shareProcessor;
    private static SphereTestUtility sphereTestUtility;
    private static SphereRegistryWrapper sphereRegistryWrapper; //for setting up shortCode-sphereId mapping in registry

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        logger.info("***** Setting up ShareProcessor:ProcessRequest TestCase *****");
        mockSetUp.setUPTestEnv();
        shareProcessor = mockSetUp.shareProcessor;
        sphereRegistryWrapper = mockSetUp.sphereRegistryWrapper;
        sphereTestUtility = new SphereTestUtility(mockSetUp.sphereRegistryWrapper, mockSetUp.upaDevice);
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        logger.info("***** Shutting down ShareProcessor:ProcessRequest TestCase *****");
        mockSetUp.destroyTestSetUp();
        sphereTestUtility = null;
    }

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {

    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test the case where<br>
     * Share request is valid<br>
     */
    @Test
    public final void validShareRequest() {
        //add the sphere which has shared the code/qr, so that the request can be validated and processed.
        String sphereId = sphereTestUtility.generateOwnerCombo();
        String shortCode = sphereRegistryWrapper.getShareCode(sphereId);

        BezirkZirkEndPoint sender = new BezirkZirkEndPoint(sphereTestUtility.OWNER_SERVICE_ID_3);
        sender.device = sphereTestUtility.DEVICE_2.getDeviceName();
        ShareRequest shareRequest = new ShareRequest(shortCode, sphereTestUtility.getBezirkDeviceInfo(), sender, "");
        assertTrue(shareProcessor.processRequest(shareRequest));
    }

    /**
     * Test the case where<br>
     * Share request is invalid(null)<br>
     */
    @Test
    public final void invalidShareRequest() {
        ShareRequest shareRequest = null;
        assertFalse(shareProcessor.processRequest(shareRequest));
    }
    // TODO: add more cases
    // TODO: expand/improve the SphereUtility for sphere and zirk generation
    // and also use mockito instead of actual classes especially registry
}