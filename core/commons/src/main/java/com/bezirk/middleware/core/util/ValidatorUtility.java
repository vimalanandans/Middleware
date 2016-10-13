/**
 * The MIT License (MIT)
 * Copyright (c) 2016 Bezirk http://bezirk.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.bezirk.middleware.core.util;

import com.bezirk.middleware.core.control.messages.Header;
import com.bezirk.middleware.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.middleware.proxy.api.impl.ZirkId;

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
        return !(bezirkServiceEndPoint == null || !checkBezirkZirkId(bezirkServiceEndPoint.zirkId) ||
                !checkForString(bezirkServiceEndPoint.device));
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
        return !(!checkForString(mHeader.getSphereId()) ||
                !checkBezirkZirkEndPoint(mHeader.getSender()));

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
