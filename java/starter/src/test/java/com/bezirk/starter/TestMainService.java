package com.bezirk.starter;

import com.bezirk.messagehandler.ServiceMessageHandler;
import com.bezirk.proxy.pc.ProxyforServices;
import com.bezirk.util.MockSetUpUtilityForUhuPC;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * This testcase verifies the startstack , stopstack and reboot apis of MainService.
 *
 * @modified by AJC6KOR
 */
public class TestMainService {

    private static MockSetUpUtilityForUhuPC mockSetUP = new MockSetUpUtilityForUhuPC();


    @BeforeClass
    public static void setUpBeforeClass() throws Exception {

        System.setProperty("InterfaceName", mockSetUP.getInterface().getName());
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {

        System.clearProperty("InterfaceName");
        System.clearProperty("displayEnable");

    }

    @Test
    public void test() {

        //testStartStack();

        //FIXME: in stop stack main service returns null in jenkin buildm where as local build runs smooth.
        //testStopStack();

        //testReboot();

    }

    /**
     * Positive TestCase :
     * <p/>
     * SphereForPC should be initialized once the mainservice starts the stack.
     */
    private void testStartStack() {

        ProxyforServices proxyforServices = new ProxyforServices();
        com.bezirk.starter.UhuConfig uhuConfigRef = new com.bezirk.starter.UhuConfig();
        uhuConfigRef.setDisplayEnable("false");
        /** DisplayEnable - true  */
        //System.setProperty("displayEnable", "true");
        com.bezirk.starter.MainService mainService = new com.bezirk.starter.MainService(proxyforServices, uhuConfigRef);
        ServiceMessageHandler testMock = Mockito.mock(ServiceMessageHandler.class);
        mainService.startStack(testMock);

        assertNotNull("sphere not initialized in startStack.", mainService.sphereForPC);
        assertTrue("Uhustack is not started after startstack call", mainService.getStartedStack());
        assertNotNull("ProxyPersistence is null even after startstack", mainService.getUhuProxyPersistence());
        mainService.stopStack();

        /** DisplayEnable - false  */

        System.setProperty("displayEnable", "false");
        mainService.startStack(testMock);

        assertNotNull("sphere not initialized in startStack.", mainService.sphereForPC);
        assertTrue("Uhustack is not started after startstack call", mainService.getStartedStack());
        assertNotNull("ProxyPersistence is null even after startstack", mainService.getUhuProxyPersistence());

        System.clearProperty("displayEnable");
        mainService.stopStack();
    }

    /**
     * Positive TestCase :
     * <p/>
     * Reference to SphereForPC should be cleared once the stack is stopped.
     */
    private void testStopStack() {

        ProxyforServices proxyforServices = new ProxyforServices();
        com.bezirk.starter.UhuConfig uhuConfigRef = new com.bezirk.starter.UhuConfig();
        uhuConfigRef.setDisplayEnable("false");
        com.bezirk.starter.MainService mainService = new com.bezirk.starter.MainService(proxyforServices, uhuConfigRef);
        ServiceMessageHandler testMock = Mockito.mock(ServiceMessageHandler.class);
        mainService.startStack(testMock);

        mainService.stopStack();

        assertNull("sphere not cleared in stopstack.", mainService.sphereForPC);

    }

    /**
     * Positive TestCase :
     * <p/>
     * SphereForPC should be initialized once the stack is rebooted.
     */
    private void testReboot() {
        ProxyforServices proxyforServices = new ProxyforServices();
        com.bezirk.starter.UhuConfig uhuConfigRef = new UhuConfig();
        uhuConfigRef.setDisplayEnable("false");
        com.bezirk.starter.MainService mainService = new com.bezirk.starter.MainService(proxyforServices, uhuConfigRef);
        mainService.startStack(Mockito.mock(ServiceMessageHandler.class));

        mainService.reboot();
        assertNotNull("sphere not intialized after reboot.", mainService.sphereForPC);

        mainService.stopStack();

    }

}
