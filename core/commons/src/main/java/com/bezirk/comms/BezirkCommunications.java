/**
 *
 */
package com.bezirk.comms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;


/**
 * The BezirkCommunications class serves a dual purpose:
 * <ul>
 *   <li>Initialize several static variables such as portNumbers, addresses, queueSizes that are
 *   necessary for comms. These values are set by <code>com.bezirk.comms.BezirkCommsPC.init()</code>
 *   and <code>com.bezirk.comms.BezirkCommsAndroid.init()</code></li>
 *   <li>Deals with the reception of duplicate messages</li>
 * </ul>
 * If a duplicate message is received Sadl uses this class to detect duplicacy of the message.
 * Subsequently, Sadl acks the duplicate message and drops it (since the message has already been
 * processed)
 */
public final class BezirkCommunications {
    private static final Logger logger = LoggerFactory.getLogger(BezirkCommunications.class);

    private static final int retransmitTime = 10000;
    private static final int mapSize = 10;
    private static LinkedHashMap<String, Long> duplicateMap = new LinkedHashMap<String, Long>();
    //Interface
    private static String INTERFACE_NAME;

    //Ports and Ips
    private static String MULTICAST_ADDRESS;
    private static int MULTICAST_PORT;
    private static int UNICAST_PORT;
    private static String CTRL_MULTICAST_ADDRESS;
    private static int CTRL_MULTICAST_PORT;
    private static int CTRL_UNICAST_PORT;
    private static int MAX_BUFFER_SIZE;
    //Thread Pool Size
    private static int POOL_SIZE;


    // Streaming properties
    private static int STARTING_PORT_FOR_STREAMING;
    private static int ENDING_PORT_FOR_STREAMING;        //   ( STARTINGPORT - END PORT )
    private static int MAX_SUPPORTED_STREAMS;
    private static boolean isStreamingEnabled;
    private static String DOWNLOAD_PATH;                    // Rename

    //Logging
    private static int REMOTE_LOGGING_PORT;
    private static boolean isRemoteLoggingServiceEnabled;
    // sphere properties
    private static boolean DEMO_SPHERE_MODE;

    private static int NO_OF_RETRIES = 0;

    /* Utility Class. All methods are static. Adding private constructor to suppress PMD warnings.*/
    private BezirkCommunications() {
    }

    public static String getINTERFACE_NAME() {
        return INTERFACE_NAME;
    }

    public static void setINTERFACE_NAME(String INTERFACE_NAME) {
        BezirkCommunications.INTERFACE_NAME = INTERFACE_NAME;
    }

    public static String getMULTICAST_ADDRESS() {
        return MULTICAST_ADDRESS;
    }

    public static void setMULTICAST_ADDRESS(String MULTICAST_ADDRESS) {
        BezirkCommunications.MULTICAST_ADDRESS = MULTICAST_ADDRESS;
    }

    public static int getMULTICAST_PORT() {
        return MULTICAST_PORT;
    }

    public static void setMULTICAST_PORT(int MULTICAST_PORT) {
        BezirkCommunications.MULTICAST_PORT = MULTICAST_PORT;
    }

    public static int getUNICAST_PORT() {
        return UNICAST_PORT;
    }

    public static void setUNICAST_PORT(int uNICAST_PORT) {
        UNICAST_PORT = uNICAST_PORT;
    }

    public static String getCTRL_MULTICAST_ADDRESS() {
        return CTRL_MULTICAST_ADDRESS;
    }

    public static void setCTRL_MULTICAST_ADDRESS(String cTRL_MULTICAST_ADDRESS) {
        CTRL_MULTICAST_ADDRESS = cTRL_MULTICAST_ADDRESS;
    }

    public static int getCTRL_MULTICAST_PORT() {
        return CTRL_MULTICAST_PORT;
    }

    public static void setCTRL_MULTICAST_PORT(int cTRL_MULTICAST_PORT) {
        CTRL_MULTICAST_PORT = cTRL_MULTICAST_PORT;
    }

    public static int getCTRL_UNICAST_PORT() {
        return CTRL_UNICAST_PORT;
    }

    public static void setCTRL_UNICAST_PORT(int cTRL_UNICAST_PORT) {
        CTRL_UNICAST_PORT = cTRL_UNICAST_PORT;
    }

    public static int getMAX_BUFFER_SIZE() {
        return MAX_BUFFER_SIZE;
    }

    public static void setMAX_BUFFER_SIZE(int mAX_BUFFER_SIZE) {
        MAX_BUFFER_SIZE = mAX_BUFFER_SIZE;
    }

    public static int getPOOL_SIZE() {
        return POOL_SIZE;
    }

    public static void setPOOL_SIZE(int pOOL_SIZE) {
        POOL_SIZE = pOOL_SIZE;
    }

    public static int getSTARTING_PORT_FOR_STREAMING() {
        return STARTING_PORT_FOR_STREAMING;
    }

    public static void setSTARTING_PORT_FOR_STREAMING(
            int sTARTING_PORT_FOR_STREAMING) {
        STARTING_PORT_FOR_STREAMING = sTARTING_PORT_FOR_STREAMING;
    }

    public static int getENDING_PORT_FOR_STREAMING() {
        return ENDING_PORT_FOR_STREAMING;
    }

    public static void setENDING_PORT_FOR_STREAMING(int eNDING_PORT_FOR_STREAMING) {
        ENDING_PORT_FOR_STREAMING = eNDING_PORT_FOR_STREAMING;
    }

    public static int getMAX_SUPPORTED_STREAMS() {
        return MAX_SUPPORTED_STREAMS;
    }

    public static void setMAX_SUPPORTED_STREAMS(int mAX_SUPPORTED_STREAMS) {
        MAX_SUPPORTED_STREAMS = mAX_SUPPORTED_STREAMS;
    }

    public static boolean isStreamingEnabled() {
        return isStreamingEnabled;
    }

    public static void setStreamingEnabled(boolean isStreamingEnabled) {
        BezirkCommunications.isStreamingEnabled = isStreamingEnabled;
    }

    public static String getDOWNLOAD_PATH() {
        return DOWNLOAD_PATH;
    }

    public static void setDOWNLOAD_PATH(String DOWNLOAD_PATH) {
        BezirkCommunications.DOWNLOAD_PATH = DOWNLOAD_PATH;
    }

    public static int getREMOTE_LOGGING_PORT() {
        return REMOTE_LOGGING_PORT;
    }

    public static void setREMOTE_LOGGING_PORT(int rEMOTE_LOGGING_PORT) {
        REMOTE_LOGGING_PORT = rEMOTE_LOGGING_PORT;
    }

    public static boolean isRemoteLoggingServiceEnabled() {
        return isRemoteLoggingServiceEnabled;
    }

    public static void setRemoteLoggingServiceEnabled(
            boolean isRemoteLoggingServiceEnabled) {
        BezirkCommunications.isRemoteLoggingServiceEnabled = isRemoteLoggingServiceEnabled;
    }

    public static boolean isDEMO_SPHERE_MODE() {
        return DEMO_SPHERE_MODE;
    }

    public static void setDEMO_SPHERE_MODE(boolean dEMO_SPHERE_MODE) {
        DEMO_SPHERE_MODE = dEMO_SPHERE_MODE;
    }

    public static int getNO_OF_RETRIES() {
        return NO_OF_RETRIES;
    }

    public static void setNO_OF_RETRIES(int nO_OF_RETRIES) {
        NO_OF_RETRIES = nO_OF_RETRIES;
    }

/*	public static int getPOOL_SIZE() {
        return POOL_SIZE;
	}

	public static void setPOOL_SIZE(int pOOL_SIZE) {
		POOL_SIZE = pOOL_SIZE;
	}
*/


/*	public static Boolean isDuplicateEvent(Header header){
		String dMessage = header.getSenderSEP().device+":"+header.getMessageId().toString()+":"+header.getSphereName();
		Long currentTime = new Date().getTime();
		if(!duplicateMap.containsKey(dMessage)){
			if(duplicateMap.size() < mapSize){
				duplicateMap.put(dMessage, currentTime);
				return true;
			}
			else{
				//remove the head
				duplicateMap.remove(duplicateMap.keySet().iterator().next());
				//add the message
				duplicateMap.put(dMessage,currentTime);
				return true;
			}
		}
		else{
			//message is present - check if the message is stale , time > 10 secs
			if(currentTime - duplicateMap.get(dMessage) > retransmitTime){
				duplicateMap.put(dMessage,currentTime);
				return true;
			}
			else
				return false;
		}
	}*/

}
