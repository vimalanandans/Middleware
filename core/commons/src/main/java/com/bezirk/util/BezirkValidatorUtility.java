package com.bezirk.util;

import com.bezirk.control.messages.Header;
import com.bezirk.control.messages.discovery.DiscoveryRequest;
import com.bezirk.control.messages.logging.LoggingServiceMessage;
import com.bezirk.control.messages.streaming.StreamRequest;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.proxy.api.impl.SubscribedRole;
import com.bezirk.proxy.api.impl.BezirkZirkId;

/**
 * @author Vijet Badigannavar(bvijet@in.bosch.com)
 * @Date 4-11-2014
 * Utility class that is used to Validate different Data Structures used in Uhu.
 * UhuDevelopers are advised to create corresponding functions here and validate the data structure.
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
    public static boolean checkUhuServiceId(final BezirkZirkId serviceId) {
        if (serviceId == null || !checkForString(serviceId.getBezirkZirkId())) {
            return false;
        }
        return true;
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
     * @param uhuServiceEndPoint - BezirkZirkEndPoint that should be validated
     * @return true if valid, false otherwise.
     */
    public static boolean checkBezirkZirkEndPoint(final BezirkZirkEndPoint uhuServiceEndPoint) {
        if (uhuServiceEndPoint == null || !checkUhuServiceId(uhuServiceEndPoint.zirkId) || !checkForString(uhuServiceEndPoint.device)) {
            return false;
        }
        return true;
    }

    /**
     * Checks for the Validity of ProtocolRole.
     *
     * @param role prototoclRole that should be validated
     * @return true if valid, false otherwise
     */
    public static boolean checkProtocolRole(final SubscribedRole role) {
        if (null == role || !checkForString(role.getProtocolName())) {

            return false;
        }
        return true;
    }

    /**
     * Checks for Validity of String for Not null and not empty
     *
     * @param str - string to be validated
     * @return true if valid(not null & non empty), false otherwise
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
        if (!checkForString(mHeader.getSphereName(), mHeader.getTopic()) ||
                !checkBezirkZirkEndPoint(mHeader.getSenderSEP())) {
            return false;
        }

        return true;

    }

    public static boolean checkDiscoveryRequest(DiscoveryRequest request) {
        if (!checkForString(request.getSphereId()) || request.getMessageId() == -1 || !checkBezirkZirkEndPoint(request.getSender())) {

            return false;
        }

        return true;
    }

    public static boolean checkStreamRequest(final StreamRequest request) {
        if (null == request || !checkForString(request.serialzedString, request.fileName, request.streamLabel, request.getSphereId()) || !checkEndPoints(request.getSender(), request.getRecipient())) {
            return false;
        }
        return true;
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
        if (null == logServiceMsg || !checkRemoteLoggingIPAndPort(logServiceMsg) || !checkSphereListIsEmpty(logServiceMsg.getSphereList())) {
            return false;
        }
        return true;
    }

    private static boolean checkRemoteLoggingIPAndPort(
            LoggingServiceMessage logServiceMsg) {

        if (!checkForString(logServiceMsg.getRemoteLoggingServiceIP()) ||
                logServiceMsg.getRemoteLoggingServicePort() == -1) {

            return false;
        }
        return true;
    }

    private static boolean checkSphereListIsEmpty(String[] sphereList) {

        if (sphereList == null || sphereList.length == 0) {

            return false;
        }


        return true;

    }

    public static boolean checkRTCStreamRequest(final BezirkZirkId serviceId, final BezirkZirkEndPoint sep) {
        return checkUhuServiceId(serviceId) && checkBezirkZirkEndPoint(sep);
    }
}