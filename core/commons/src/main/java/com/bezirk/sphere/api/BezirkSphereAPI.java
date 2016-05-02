package com.bezirk.sphere.api;

import com.bezirk.middleware.objects.BezirkDeviceInfo;
import com.bezirk.middleware.objects.BezirkPipeInfo;
import com.bezirk.middleware.objects.BezirkSphereInfo;
import com.bezirk.middleware.objects.BezirkZirkInfo;
import com.bezirk.proxy.api.impl.BezirkZirkId;
import com.google.zxing.common.BitMatrix;

import java.util.List;

/**
 * BezirkSphereAPI - Platform-independent API offered by UhU to sphere management
 * application
 *
 * @author Rishabh Gulati, Vimal
 */

public interface BezirkSphereAPI {

    /**
     * Creates a sphere
     *
     * @param: sphereName
     * name of the sphere to be created
     * @param: sphereType
     * type of the sphere to be created
     */
    public String createSphere(String sphereName, String sphereType);

    /**
     * Delete the sphere removes the list of services in the sphere and informs
     * other services and deletes the sphere from internal list
     */
    public boolean deleteSphere(String sphereId);

    /**
     * Get all the spheres visible/authorized to this device bezirk This contains
     * the complete data of Spheres and its devices / pipe available
     *
     * @return : Iterator sphere Info
     */
    public Iterable<BezirkSphereInfo> getSpheres();

    /**
     * Get the sphere Info by sphere Id
     *
     * @param : String sphereId
     * @return : sphere Info if found else null
     */
    public BezirkSphereInfo getSphere(String sphereId);

    /**
     * Get the sphere Info by sphere Id
     *
     * @param : BezirkSphereInfo
     * @return : true if this device owns this sphere or not
     */
    public boolean isThisDeviceOwnsSphere(BezirkSphereInfo sphereInfo);

    /**
     * Get all the devices visible/authorized to this device bezirk *
     *
     * @return : Iterator device info
     * @deprecated : {@link BezirkDeviceInfo} is available inside
     * {@link BezirkZirkInfo} which can be retrieved using
     * {@link #getSpheres()}
     */
    @Deprecated
    public Iterable<BezirkDeviceInfo> getDevicesOnSphere(String sphereId);

    /**
     * Get all the pipes visible/authorized to this device bezirk *
     *
     * @return : Iterable pipe info
     * @deprecated : {@link BezirkPipeInfo} is available inside
     * {@link BezirkZirkInfo} which can be retrieved using
     * {@link #getSpheres()}
     */
    @Deprecated
    public Iterable<BezirkPipeInfo> getPipesOnSphere(String sphereId);

    /**
     * Get all devices known to this Bezirk apart from the one's that are a part of
     * the sphereId passed
     *
     * @param sphereId
     * @return Iterable device info
     */
    public Iterable<BezirkDeviceInfo> getOtherDevices(String sphereId);

    /**
     * Add services to sphere (Add the local services to a sphere owned by the
     * device)
     *
     * @param: serviceIds
     * of Services to be added
     * @param: sphereId
     * of sphere to which the services need to be added
     * @return: true if request is accepted to process. Task completion via
     * Listener
     * @deprecated: use {@link #addLocalServicesToSphere(String, Iterable)}
     */
    @Deprecated // UI has BezirkZirkInfo not BezirkZirkId
    public boolean addLocalServicesToSphere(Iterable<BezirkZirkId> serviceIds, String sphereId);

    /**
     * Add services to sphere (Add the local services to a sphere owned by the
     * device)
     *
     * @param: serviceInfo
     * of Services to be added
     * @param: sphereId
     * of sphere to which the services need to be added
     * @return: true if request is accepted to process. Task completion via
     * Listener
     */
    public boolean addLocalServicesToSphere(String sphereId, Iterable<BezirkZirkInfo> serviceInfo);

    /**
     * Add the local services (from default sphere) to the given sphere
     */
    public boolean addLocalServicesToSphere(String sphereId);

    /**
     * request the zirk to leave the sphere
     *
     * @param: zirkId
     * of Zirk
     * @param: sphereId
     * of sphere
     * @return: true if request is accepted to process. Task completion via
     * Listener
     */
    public boolean serviceLeaveRequest(String serviceId, String sphereId);

    /**
     * Expel zirk from sphere
     *
     * @param: zirkId
     * of Zirk
     * @param: sphereId
     * of sphere
     * @return: true if request is accepted to process. Task completion via
     * Listener
     * @deprecated use {@link #expelDeviceFromSphere(String, String)}
     */
    @Deprecated // concept is always expel the device from zirk
    public boolean expelServiceFromSphere(String serviceId, String sphereId);

    /**
     * Expel zirk from sphere
     *
     * @param: zirkId
     * of Zirk
     * @param: sphereId
     * of sphere
     * @return: true if request is accepted to process. Task completion via
     * Listener
     */
    public boolean expelDeviceFromSphere(String deviceId, String sphereId);

    /**
     * TODO Discover sphere (needs feedback how many discovered, how many
     * cached(may be when it is cached))
     */

    /**
     * Adds device information received from operations like invite-join
     *
     * @param deviceInformation
     */

    /**
     * Provides the BitMatrix for generating platform specific QR codes
     *
     * @param sphereId
     * @return BitMatrix
     */
    public BitMatrix getQRCodeMatrix(String sphereId);

    /**
     * Provides the BitMatrix for generating platform specific QR codes with
     * specific dimensions
     *
     * @param sphereId Name of the sphere that will be imprinted in the QRCode image
     * @param width    width of the image
     * @param height   height of the image
     * @return Bitmatrix containing the QRCode imprinted with the sphereId
     */
    public BitMatrix getQRCodeMatrix(String sphereId, int width, int height);

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

    public boolean processShareQRCode(String qrcodeString, String sphereId);

    public boolean processCatchQRCodeRequest(String qrcodeString, String joinSphereId);

    public boolean discoverSphere(String sphereId);

    public List<BezirkZirkInfo> getServiceInfo();

}
