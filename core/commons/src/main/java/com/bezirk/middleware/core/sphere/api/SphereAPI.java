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

import com.bezirk.middleware.objects.BezirkDeviceInfo;
import com.bezirk.middleware.objects.BezirkSphereInfo;
import com.bezirk.middleware.objects.BezirkZirkInfo;
import com.bezirk.middleware.proxy.api.impl.ZirkId;
import com.google.zxing.common.BitMatrix;

import java.util.List;

public interface SphereAPI {

    /**
     * Creates a sphere
     *
     * @param sphereName name of the sphere to be created
     * @param sphereType type of the sphere to be created
     */
    String createSphere(String sphereName, String sphereType);

    /**
     * Delete the sphere removes the list of services in the sphere and informs
     * other services and deletes the sphere from internal list
     */
    boolean deleteSphere(String sphereId);

    /**
     * Get all the spheres visible/authorized to this device bezirk This contains
     * the complete data of Spheres and its devices / pipe available
     *
     * @return Iterator sphere Info
     */
    Iterable<BezirkSphereInfo> getSpheres();

    /**
     * Get the sphere Info by sphere Id
     *
     * @param sphereId String sphereId
     * @return sphere Info if found else null
     */
    BezirkSphereInfo getSphere(String sphereId);

    /**
     * Get the sphere Info by sphere Id
     *
     * @param sphereInfo
     * @return true if this device owns this sphere or not
     */
    boolean isThisDeviceOwnsSphere(BezirkSphereInfo sphereInfo);

    /**
     * Get all the devices visible/authorized to this device bezirk *
     *
     * @return : Iterator device info
     * @deprecated : {@link BezirkDeviceInfo} is available inside
     * {@link BezirkZirkInfo} which can be retrieved using
     * {@link #getSpheres()}
     */
    @Deprecated
    Iterable<BezirkDeviceInfo> getDevicesOnSphere(String sphereId);

    /**
     * Get all devices known to this Bezirk apart from the one's that are a part of
     * the sphereId passed
     *
     * @param sphereId
     * @return Iterable device info
     */
    Iterable<BezirkDeviceInfo> getOtherDevices(String sphereId);

    /**
     * Add services to sphere (Add the local services to a sphere owned by the
     * device)
     *
     * @param serviceIds of Services to be added
     * @param sphereId   of sphere to which the services need to be added
     * @return true if request is accepted to process. Task completion via
     * Listener
     * @deprecated use {@link #addLocalServicesToSphere(String, Iterable)}
     */
    @Deprecated
    // UI has BezirkZirkInfo not ZirkId
    boolean addLocalServicesToSphere(Iterable<ZirkId> serviceIds, String sphereId);

    /**
     * Add services to sphere (Add the local services to a sphere owned by the
     * device)
     *
     * @param serviceInfo of Services to be added
     * @param sphereId    of sphere to which the services need to be added
     * @return true if request is accepted to process. Task completion via
     * Listener
     */
    boolean addLocalServicesToSphere(String sphereId, Iterable<BezirkZirkInfo> serviceInfo);

    /**
     * Add the local services (from default sphere) to the given sphere
     */
    boolean addLocalServicesToSphere(String sphereId);

    /**
     * request the zirk to leave the sphere
     *
     * @param sphereId of sphere
     * @return true if request is accepted to process. Task completion via
     * Listener
     */
    boolean serviceLeaveRequest(String serviceId, String sphereId);

    /**
     * Expel zirk from sphere
     *
     * @param zirkId   of Zirk
     * @param sphereId of sphere
     * @return true if request is accepted to process. Task completion via
     * Listener
     * @deprecated use {@link #expelDeviceFromSphere(String, String)}
     */
    @Deprecated
    // concept is always expel the device from zirk
    boolean expelServiceFromSphere(String zirkId, String sphereId);

    /**
     * Expel zirk from sphere
     *
     * @param deviceId of Zirk
     * @param sphereId of sphere
     * @return true if request is accepted to process. Task completion via
     * Listener
     */
    boolean expelDeviceFromSphere(String deviceId, String sphereId);

    /**
     * Provides the BitMatrix for generating platform specific QR codes
     *
     * @param sphereId
     * @return BitMatrix
     */
    BitMatrix getQRCodeMatrix(String sphereId);

    /**
     * Provides the BitMatrix for generating platform specific QR codes with
     * specific dimensions
     *
     * @param sphereId Name of the sphere that will be imprinted in the QRCode image
     * @param width    width of the image
     * @param height   height of the image
     * @return Bitmatrix containing the QRCode imprinted with the sphereId
     */
    BitMatrix getQRCodeMatrix(String sphereId, int width, int height);

    /**
     * This implementation along with <code>BarCodeUtilities</code> needs further refinement.
     * Currently using the implementation from version 0.4
     * <p>
     * Processing and parsing of sphere keys needs to be discussed as they come
     * in through the barcode string
     * </p>
     *
     * @param qrcodeString
     * @param sphereId     : sphere which was shared
     * @return boolean about processed or processing accepted
     */

    boolean processShareQRCode(String qrcodeString, String sphereId);

    boolean processCatchQRCodeRequest(String qrcodeString, String joinSphereId);

    List<BezirkZirkInfo> getServiceInfo();

}
