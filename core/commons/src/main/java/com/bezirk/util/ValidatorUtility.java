package com.bezirk.util;

import com.bezirk.control.messages.Header;
import com.bezirk.control.messages.streaming.StreamRequest;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.proxy.api.impl.ZirkId;

/**
 * Utility class that is used to Validate different Data Structures used in Bezirk.
 * BezirkDevelopers are advised to create corresponding functions here and validate the data structure.
 */
public final class ValidatorUtility {

    /* Utility Class. All methods are static. Adding private constructor to suppress PMD warnings.*/
    private ValidatorUtility() {

    }

    /**
     * Checks for Validity of ZirkId.
     *
     * @param serviceId ZirkId that will check for ZirkId
     * @return true if ZirkId is valid, false otherwise
     */
    public static boolean checkBezirkZirkId(final ZirkId serviceId) {
        return !(serviceId == null || !checkForString(serviceId.getZirkId()));
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
        return !(!checkForString(mHeader.getSphereName()) ||
                !checkBezirkZirkEndPoint(mHeader.getSenderSEP()));

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



    public static boolean checkRTCStreamRequest(final ZirkId serviceId, final BezirkZirkEndPoint sep) {
        return checkBezirkZirkId(serviceId) && checkBezirkZirkEndPoint(sep);
    }
}