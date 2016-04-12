package com.bezirk.util;

import com.bezirk.control.messages.Header;
import com.bezirk.control.messages.discovery.DiscoveryRequest;
import com.bezirk.control.messages.logging.LoggingServiceMessage;
import com.bezirk.control.messages.streaming.StreamRequest;
import com.bezirk.proxy.api.impl.SubscribedRole;
import com.bezirk.proxy.api.impl.UhuServiceEndPoint;
import com.bezirk.proxy.api.impl.UhuServiceId;

/**
 * @author Vijet Badigannavar(bvijet@in.bosch.com)
 * @Date  4-11-2014
 * Utility class that is used to Validate different Data Structures used in Uhu.
 * UhuDevelopers are advised to create corresponding functions here and validate the data structure.
 */
public final class UhuValidatorUtility {
	
	/* Utility Class. All methods are static. Adding private constructor to suppress PMD warnings.*/
	private UhuValidatorUtility(){
		
	}
	
	/**
     * Checks for Validity of UhuServiceId.
     * @param serviceId UhuServiceId that will check for ServiceId
     * @return true if UhuServiceId is valid, false otherwise
     */
    public static boolean checkUhuServiceId(final UhuServiceId serviceId) {
        if (serviceId == null || !checkForString(serviceId.getUhuServiceId())) {
            return false;
        }
        return true;
    }

    /**
     * @return true if object is not null
     */
    public static boolean isObjectNotNull(final Object object) {
        
        return object!=null;
    }
    
    /**
     * Checks for Validity of UhuServiceEndPoint
     * @param uhuServiceEndPoint - UhuServiceEndPoint that should be validated
     * @return true if valid, false otherwise.
     */
    public static boolean checkUhuServiceEndPoint(final UhuServiceEndPoint uhuServiceEndPoint) {
        if (uhuServiceEndPoint == null || !checkUhuServiceId(uhuServiceEndPoint.serviceId) || !checkForString(uhuServiceEndPoint.device)) {
				return false;
		}
        return true;
    }

    /**
     * Checks for the Validity of ProtocolRole.
     * @param role prototoclRole that should be validated
     * @return true if valid, false otherwise
     */
    public static boolean checkProtocolRole(final SubscribedRole role) {
        if (null == role || !checkForString(role.getProtocolName())){
        	
        	return false;
        }
        return true;
    }

    /**
     * Checks for Validity of String for Not null and not empty
     * @param str - string to be validated
     * @return true if valid(not null & non empty), false otherwise 
     */
	public static boolean checkForString(final String... stringValues) {
		
		if(stringValues ==null || stringValues.length==0){

            return false;
        }

		for (String str : stringValues) {

			if (str == null || str.isEmpty()) {

				return false;
			}

		}

		return true;
	}

    public static boolean checkHeader(final Header mHeader) {
        if (!checkForString(mHeader.getSphereName(), mHeader.getTopic())||
                !checkUhuServiceEndPoint(mHeader.getSenderSEP()))
                 {
            return false;
        }

        return true;

    }

    public static boolean checkDiscoveryRequest (DiscoveryRequest request){
        if(!checkForString(request.getSphereId()) || request.getMessageId()== -1 || !checkUhuServiceEndPoint(request.getSender())){
        	
        	return  false;
        }

        return true;
    }

    public static boolean checkStreamRequest(final StreamRequest request){
        if(null == request || !checkForString(request.serialzedString,request.fileName,request.streamLabel,request.getSphereId()) || !checkEndPoints(request.getSender(),request.getRecipient())){
            return false;
        }
        return true;
    }
    
	private static boolean checkEndPoints(UhuServiceEndPoint... serviceEndPoints) {
		
		for(UhuServiceEndPoint serviceEndPoint : serviceEndPoints){
			
			if(!checkUhuServiceEndPoint(serviceEndPoint)){
				
				return false;
			}
			
		}
		
		return true;
		
	}
	
    
    public static boolean checkLoggingServiceMessage(final LoggingServiceMessage logServiceMsg){
        if(null == logServiceMsg || !checkRemoteLoggingIPAndPort(logServiceMsg) || !checkSphereListIsEmpty(logServiceMsg.getSphereList()) ){
            return false;
        }
        return true;
    }
    
	private static boolean checkRemoteLoggingIPAndPort(
			LoggingServiceMessage logServiceMsg) {
		
		if(!checkForString(logServiceMsg.getRemoteLoggingServiceIP()) ||
        logServiceMsg.getRemoteLoggingServicePort() == -1){
			
			return false;
		}
		return true;
	}

	private static boolean checkSphereListIsEmpty(String[] sphereList) {
		
		if(sphereList==null || sphereList.length==0){
			
			return false;
		}
			
		
		return true;
		
	}
	
	public static boolean checkRTCStreamRequest(final UhuServiceId serviceId, final UhuServiceEndPoint sep){
        return checkUhuServiceId(serviceId) && checkUhuServiceEndPoint(sep);
	}
}