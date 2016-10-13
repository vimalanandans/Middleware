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
package com.bezirk.middleware.objects;

import java.util.ArrayList;

public class BezirkSphereInfo {
    private final String sphereID;
    private final String sphereName;
    private final String sphereType;
    private final ArrayList<BezirkDeviceInfo> deviceList;

    // current device owns this sphere.
    private boolean isThisDeviceOwnsSphere;

    public BezirkSphereInfo(final String sphereID, final String sphereName,
                            final String sphereType,
                            final ArrayList<BezirkDeviceInfo> deviceList) {
        this.sphereID = sphereID;
        this.sphereType = sphereType;
        this.sphereName = sphereName;
        this.deviceList = new ArrayList<>(deviceList);
    }

    /**
     * Copy constructor
     * Requires not null {@link BezirkSphereInfo}} to be passed
     */

    public BezirkSphereInfo(BezirkSphereInfo other) {
        this(other.sphereID, other.sphereName, other.sphereType, other.deviceList);
    }


    /**
     * @return the sphereID
     */
    public final String getSphereID() {
        return sphereID;
    }

    /**
     * @return the sphereName
     */
    public final String getSphereName() {
        return sphereName;
    }

    /**
     * @return the <code>sphereType</code>
     */
    public final String getSphereType() {
        return sphereType;
    }

    /**
     * @return the sphere owned by this device
     */
    public boolean isThisDeviceOwnsSphere() {
        return isThisDeviceOwnsSphere;
    }

    public void setThisDeviceOwnsSphere(boolean status) {
        isThisDeviceOwnsSphere = status;
    }

    /**
     * @return the deviceList
     */
    public final ArrayList<BezirkDeviceInfo> getDeviceList() {
        return new ArrayList<>(deviceList);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "BezirkSphereInfo [sphereID=" + sphereID + ",\nsphereName="
                + sphereName + ",\nsphereType=" + sphereType + ",\ndeviceList=" + deviceList +
                "]";
    }


}
