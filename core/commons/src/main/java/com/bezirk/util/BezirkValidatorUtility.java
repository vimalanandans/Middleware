package com.bezirk.util;

import com.bezirk.control.messages.Header;
import com.bezirk.control.messages.discovery.DiscoveryRequest;
import com.bezirk.control.messages.logging.LoggingServiceMessage;
import com.bezirk.control.messages.streaming.StreamRequest;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.proxy.api.impl.SubscribedRole;
import com.bezirk.proxy.api.impl.BezirkZirkId;

/**
 * Utility class that is used to Validate different Data Structures used in Bezirk.
 * BezirkDevelopers are advised to create corresponding functions here and validate the data structure.
 */
public final class BezirkValidatorUtility {

    /* Utility Class. All methods are static. Adding private constructor to suppress PMD warnings.*/
    private BezirkValidatorUtility() {

    }

    /**
     * Checks for Validity of BezirkZirkId.
     *
     * @param serviceId BezirkZirkId that will check for ZirkId
     * @return true if BezirkZirkId is valid, false otherwise
     */
    public static boolean checkBezirkZirkId(final BezirkZirkId serviceId) {
        return !(serviceId == null || !checkForString(serviceId.getBezirkZirkId()));
    }

    /**
     * @return true if object is not null
     */
    public static boolean isObjectNotNull(final Object object) {

        return object != null;
    }

    /**
     * Checks for Validity of BezirkZirkEndPoint
     *
     * @param bezirkServiceEndPoint - BezirkZirkEndPoint that should be validated
     * @return true if valid, false otherwise.
     */
    public static boolean checkBezirkZirkEndPoint(final BezirkZirkEndPoint bezirkServiceEndPoint) {
        return !(bezirkServiceEndPoint == null || !checkBezirkZirkId(bezirkServiceEndPoint.zirkId) || !checkForString(bezirkServiceEndPoint.device));
    }

    /**
     * Checks for the Validity of ProtocolRole.
     *
     * @param role protocolRole that should be validated
     * @return true if valid, false otherwise
     */
    public static boolean checkProtocolRole(final SubscribedRole role) {
        return !(null == role || !checkForString(role.getProtocolName()));
    }

    /**
     * Checks for Validity of String for Not null and not empty
     *
     * @param stringValues - string to be validated
     * @return true if valid(not null and non empty), false otherwise
     */
    public static boolean checkForString(final String... stringValues) {
        if (stringValues == null || stringValues.length == 0) {
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
        return !(!checkForString(mHeader.getSphereName(), mHeader.getTopic()) ||
                !checkBezirkZirkEndPoint(mHeader.getSenderSEP()));

    }

    public static boolean checkDiscoveryRequest(DiscoveryRequest request) {
        return !(!checkForString(request.getSphereId()) || request.getMessageId() == -1 || !checkBezirkZirkEndPoint(request.getSender()));

    }

    public static boolean checkStreamRequest(final StreamRequest request) {
        return !(null == request || !checkForString(request.serialzedString, request.fileName, request.streamLabel, request.getSphereId()) || !checkEndPoints(request.getSender(), request.getRecipient()));
    }

    private static boolean checkEndPoints(BezirkZirkEndPoint... serviceEndPoints) {

        for (BezirkZirkEndPoint serviceEndPoint : serviceEndPoints) {

            if (!checkBezirkZirkEndPoint(serviceEndPoint)) {

                return false;
            }

        }

        return true;

    }


    public static boolean checkLoggingServiceMessage(final LoggingServiceMessage logServiceMsg) {
        return !(null == logServiceMsg || !checkRemoteLoggingIPAndPort(logServiceMsg) || !checkSphereListIsEmpty(logServiceMsg.getSphereList()));
    }

    private static boolean checkRemoteLoggingIPAndPort(
            LoggingServiceMessage logServiceMsg) {

        return !(!checkForString(logServiceMsg.getRemoteLoggingServiceIP()) ||
                logServiceMsg.getRemoteLoggingServicePort() == -1);
    }

    private static boolean checkSphereListIsEmpty(String[] sphereList) {
        return !(sphereList == null || sphereList.length == 0);
    }

    public static boolean checkRTCStreamRequest(final BezirkZirkId serviceId, final BezirkZirkEndPoint sep) {
        return checkBezirkZirkId(serviceId) && checkBezirkZirkEndPoint(sep);
    }
}