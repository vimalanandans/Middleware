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
package com.bezirk.middleware.core.sphere.api;

import com.bezirk.middleware.proxy.api.impl.ZirkId;

public interface SphereServiceAccess {

    /**
     * Registers the zirk with BezirkSphere's. In case the zirk is already
     * registered, call to this method updates the name of the zirk to
     * zirkName passed
     *
     * @param zirkId   ZirkId to be registered
     * @param zirkName Name to be associated with the zirk
     * @return <code>true</code> if zirk was added successfully
     */
    boolean registerService(ZirkId zirkId, String zirkName);

    /**
     * unregister service
     */
    boolean unregisterService(ZirkId serviceId);

    /**
     * Provides iterable collection of sphereIds associated with passed
     * ZirkId
     *
     * @param zirkId ZirkId for retrieving stored membership information
     * @return iterable Collection of sphereIds for the passed ZirkId, <code>null</code> in case
     * the <code>zirkId</code> passed is <code>null</code> or not registered
     */
    Iterable<String> getSphereMembership(ZirkId zirkId);

    // TODO add to wiki : found while refactoring to the new API

    /**
     * Checks if the zirk is a part of the sphere
     *
     * @param service  ZirkId for finding existence in a sphere
     * @param sphereId sphere to be tested
     * @return true if the zirk exist in the sphere false otherwise
     */
    boolean isServiceInSphere(ZirkId service, String sphereId);

    /**
     * Gets the zirk name of the passed ZirkId
     *
     * @param serviceId ZirkId for retrieving the zirk name
     * @return Zirk name if the zirk id is valid and not null null
     * otherwise
     */
    String getServiceName(ZirkId serviceId);

    /**
     * @param deviceId the deviceId whose Device Name needs to be known
     * @return Device Name if exists, null otherwise
     */
    String getDeviceNameFromSphere(String deviceId);
}
