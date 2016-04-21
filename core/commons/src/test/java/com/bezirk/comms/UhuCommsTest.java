package com.bezirk.comms;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * This testcase verifies the working of UhuComms by setting and retrieving the properties.
 *
 * @author AJC6KOR
 */
public class UhuCommsTest {

    @Test
    public void test() {

        boolean dEMO_SPHERE_MODE = false;
        boolean isRemoteLoggingServiceEnabled = true;
        boolean isStreamingEnabled = true;

        String cTRL_MULTICAST_ADDRESS = "224.5.6.7";
        String dOWNLOAD_PATH = "testPath";
        String iNTERFACE_NAME = "eth3";
        String mULTICAST_ADDRESS = "224.5.6.7";

        int cTRL_MULTICAST_PORT = 7777;
        int cTRL_UNICAST_PORT = 7787;
        int eNDING_PORT_FOR_STREAMING = 9999;
        int mAX_BUFFER_SIZE = 1024;
        int mAX_SUPPORTED_STREAMS = 5;
        int mULTICAST_PORT = 8891;
        int nO_OF_RETRIES = 5;
        int pOOL_SIZE = 10;
        int rEMOTE_LOGGING_PORT = 9977;
        int uNICAST_PORT = 8887;
        int sTARTING_PORT_FOR_STREAMING = 5555;

        UhuComms.setCTRL_MULTICAST_ADDRESS(cTRL_MULTICAST_ADDRESS);
        UhuComms.setCTRL_MULTICAST_PORT(cTRL_MULTICAST_PORT);
        UhuComms.setCTRL_UNICAST_PORT(cTRL_UNICAST_PORT);
        UhuComms.setDEMO_SPHERE_MODE(dEMO_SPHERE_MODE);
        UhuComms.setDOWNLOAD_PATH(dOWNLOAD_PATH);
        UhuComms.setENDING_PORT_FOR_STREAMING(eNDING_PORT_FOR_STREAMING);
        UhuComms.setINTERFACE_NAME(iNTERFACE_NAME);
        UhuComms.setMAX_BUFFER_SIZE(mAX_BUFFER_SIZE);
        UhuComms.setMAX_SUPPORTED_STREAMS(mAX_SUPPORTED_STREAMS);
        UhuComms.setMULTICAST_ADDRESS(mULTICAST_ADDRESS);
        UhuComms.setMULTICAST_PORT(mULTICAST_PORT);
        UhuComms.setNO_OF_RETRIES(nO_OF_RETRIES);
        UhuComms.setPOOL_SIZE(pOOL_SIZE);
        UhuComms.setREMOTE_LOGGING_PORT(rEMOTE_LOGGING_PORT);
        UhuComms.setRemoteLoggingServiceEnabled(isRemoteLoggingServiceEnabled);
        UhuComms.setSTARTING_PORT_FOR_STREAMING(sTARTING_PORT_FOR_STREAMING);
        UhuComms.setStreamingEnabled(isStreamingEnabled);
        UhuComms.setUNICAST_PORT(uNICAST_PORT);

        assertEquals("CTRL_MULTICAST_ADDRESS is not equal to the set value.",
                cTRL_MULTICAST_ADDRESS, UhuComms.getCTRL_MULTICAST_ADDRESS());
        assertEquals("CTRL_MULTICAST_PORT is not equal to the set value.",
                cTRL_MULTICAST_PORT, UhuComms.getCTRL_MULTICAST_PORT());
        assertEquals("CTRL_UNICAST_PORT is not equal to the set value.",
                cTRL_UNICAST_PORT, UhuComms.getCTRL_UNICAST_PORT());
        assertFalse("DEMO_SPHERE_MODE is not equal to the set value.",
                UhuComms.isDEMO_SPHERE_MODE());
        assertEquals("DOWNLOAD_PATH is not equal to the set value.",
                dOWNLOAD_PATH, UhuComms.getDOWNLOAD_PATH());
        assertEquals(
                "ENDING_PORT_FOR_STREAMING is not equal to the set value.",
                eNDING_PORT_FOR_STREAMING, UhuComms.getENDING_PORT_FOR_STREAMING());
        assertEquals("INTERFACE_NAME is not equal to the set value.",
                iNTERFACE_NAME, UhuComms.getINTERFACE_NAME());
        assertEquals("MAX_BUFFER_SIZE is not equal to the set value.",
                mAX_BUFFER_SIZE, UhuComms.getMAX_BUFFER_SIZE());
        assertEquals("MAX_SUPPORTED_STREAMS is not equal to the set value.",
                mAX_SUPPORTED_STREAMS, UhuComms.getMAX_SUPPORTED_STREAMS());
        assertEquals("MULTICAST_ADDRESS is not equal to the set value.",
                mULTICAST_ADDRESS, UhuComms.getMULTICAST_ADDRESS());
        assertEquals("MULTICAST_PORT is not equal to the set value.",
                mULTICAST_PORT, UhuComms.getMULTICAST_PORT());
        assertEquals("NO_OF_RETRIES is not equal to the set value.",
                nO_OF_RETRIES, UhuComms.getNO_OF_RETRIES());
        assertEquals("POOL_SIZE is not equal to the set value.", pOOL_SIZE,
                UhuComms.getPOOL_SIZE());
        assertEquals("REMOTE_LOGGING_PORT is not equal to the set value.",
                rEMOTE_LOGGING_PORT, UhuComms.getREMOTE_LOGGING_PORT());
        assertTrue(
                "RemoteLoggingServiceEnabled is not equal to the set value.",
                UhuComms.isRemoteLoggingServiceEnabled());
        assertEquals("STARTING_PORT_FOR_STREAMING is not equal to the set value.",
                sTARTING_PORT_FOR_STREAMING, UhuComms.getSTARTING_PORT_FOR_STREAMING());
        assertTrue(
                "StreamingEnabled is not equal to the set value.",
                UhuComms.isStreamingEnabled());
        assertEquals("UNICAST_PORT is not equal to the set value.",
                uNICAST_PORT, UhuComms.getUNICAST_PORT());


    }
}
