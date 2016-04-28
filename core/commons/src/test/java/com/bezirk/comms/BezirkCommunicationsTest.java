package com.bezirk.comms;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * This testcase verifies the working of BezirkCommunications by setting and retrieving the properties.
 *
 * @author AJC6KOR
 */
public class BezirkCommunicationsTest {

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

        BezirkCommunications.setCTRL_MULTICAST_ADDRESS(cTRL_MULTICAST_ADDRESS);
        BezirkCommunications.setCTRL_MULTICAST_PORT(cTRL_MULTICAST_PORT);
        BezirkCommunications.setCTRL_UNICAST_PORT(cTRL_UNICAST_PORT);
        BezirkCommunications.setDEMO_SPHERE_MODE(dEMO_SPHERE_MODE);
        BezirkCommunications.setDOWNLOAD_PATH(dOWNLOAD_PATH);
        BezirkCommunications.setENDING_PORT_FOR_STREAMING(eNDING_PORT_FOR_STREAMING);
        BezirkCommunications.setINTERFACE_NAME(iNTERFACE_NAME);
        BezirkCommunications.setMAX_BUFFER_SIZE(mAX_BUFFER_SIZE);
        BezirkCommunications.setMAX_SUPPORTED_STREAMS(mAX_SUPPORTED_STREAMS);
        BezirkCommunications.setMULTICAST_ADDRESS(mULTICAST_ADDRESS);
        BezirkCommunications.setMULTICAST_PORT(mULTICAST_PORT);
        BezirkCommunications.setNO_OF_RETRIES(nO_OF_RETRIES);
        BezirkCommunications.setPOOL_SIZE(pOOL_SIZE);
        BezirkCommunications.setREMOTE_LOGGING_PORT(rEMOTE_LOGGING_PORT);
        BezirkCommunications.setRemoteLoggingServiceEnabled(isRemoteLoggingServiceEnabled);
        BezirkCommunications.setSTARTING_PORT_FOR_STREAMING(sTARTING_PORT_FOR_STREAMING);
        BezirkCommunications.setStreamingEnabled(isStreamingEnabled);
        BezirkCommunications.setUNICAST_PORT(uNICAST_PORT);

        assertEquals("CTRL_MULTICAST_ADDRESS is not equal to the set value.",
                cTRL_MULTICAST_ADDRESS, BezirkCommunications.getCTRL_MULTICAST_ADDRESS());
        assertEquals("CTRL_MULTICAST_PORT is not equal to the set value.",
                cTRL_MULTICAST_PORT, BezirkCommunications.getCTRL_MULTICAST_PORT());
        assertEquals("CTRL_UNICAST_PORT is not equal to the set value.",
                cTRL_UNICAST_PORT, BezirkCommunications.getCTRL_UNICAST_PORT());
        assertFalse("DEMO_SPHERE_MODE is not equal to the set value.",
                BezirkCommunications.isDEMO_SPHERE_MODE());
        assertEquals("DOWNLOAD_PATH is not equal to the set value.",
                dOWNLOAD_PATH, BezirkCommunications.getDOWNLOAD_PATH());
        assertEquals(
                "ENDING_PORT_FOR_STREAMING is not equal to the set value.",
                eNDING_PORT_FOR_STREAMING, BezirkCommunications.getENDING_PORT_FOR_STREAMING());
        assertEquals("INTERFACE_NAME is not equal to the set value.",
                iNTERFACE_NAME, BezirkCommunications.getINTERFACE_NAME());
        assertEquals("MAX_BUFFER_SIZE is not equal to the set value.",
                mAX_BUFFER_SIZE, BezirkCommunications.getMAX_BUFFER_SIZE());
        assertEquals("MAX_SUPPORTED_STREAMS is not equal to the set value.",
                mAX_SUPPORTED_STREAMS, BezirkCommunications.getMAX_SUPPORTED_STREAMS());
        assertEquals("MULTICAST_ADDRESS is not equal to the set value.",
                mULTICAST_ADDRESS, BezirkCommunications.getMULTICAST_ADDRESS());
        assertEquals("MULTICAST_PORT is not equal to the set value.",
                mULTICAST_PORT, BezirkCommunications.getMULTICAST_PORT());
        assertEquals("NO_OF_RETRIES is not equal to the set value.",
                nO_OF_RETRIES, BezirkCommunications.getNO_OF_RETRIES());
        assertEquals("POOL_SIZE is not equal to the set value.", pOOL_SIZE,
                BezirkCommunications.getPOOL_SIZE());
        assertEquals("REMOTE_LOGGING_PORT is not equal to the set value.",
                rEMOTE_LOGGING_PORT, BezirkCommunications.getREMOTE_LOGGING_PORT());
        assertTrue(
                "RemoteLoggingServiceEnabled is not equal to the set value.",
                BezirkCommunications.isRemoteLoggingServiceEnabled());
        assertEquals("STARTING_PORT_FOR_STREAMING is not equal to the set value.",
                sTARTING_PORT_FOR_STREAMING, BezirkCommunications.getSTARTING_PORT_FOR_STREAMING());
        assertTrue(
                "StreamingEnabled is not equal to the set value.",
                BezirkCommunications.isStreamingEnabled());
        assertEquals("UNICAST_PORT is not equal to the set value.",
                uNICAST_PORT, BezirkCommunications.getUNICAST_PORT());


    }
}
