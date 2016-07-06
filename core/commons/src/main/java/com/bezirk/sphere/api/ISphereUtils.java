/**
 *
 */
package com.bezirk.sphere.api;

import com.bezirk.middleware.objects.BezirkZirkInfo;
import com.bezirk.middleware.objects.BezirkDeviceInfo;
import com.bezirk.proxy.api.impl.ZirkId;
import com.bezirk.sphere.impl.DeviceInformation;
import com.google.zxing.common.BitMatrix;

/**
 * @author rishabh
 */
public interface ISphereUtils {

    /**
     * Checks if the zirk is a local zirk
     *
     * @param deviceId : zirk owner deviceId
     * @return
     */
    public boolean isServiceLocal(String deviceId);

    /**
     * Validates the services passed by checking the registry
     *
     * @param serviceIds
     * @return
     */
    public boolean validateServices(Iterable<ZirkId> serviceIds);

    public DeviceInformation getDeviceInformation(String deviceId);

    public boolean addMemberServices(BezirkDeviceInfo bezirkDeviceInfo, String sphereId, String ownerDeviceId);

    public boolean addLocalServicesToSphere(String sphereId, Iterable<BezirkZirkInfo> serviceInfo);

    public boolean addLocalServicesToSphere(Iterable<ZirkId> serviceIds, String sphereId);

    public boolean addLocalServicesToSphere(String sphereId);

    public Iterable<BezirkZirkInfo> getBezirkServiceInfo(Iterable<ZirkId> services);

    public String getShareCode(String sphereId);

    public String getShareCodeString(String sphereId);

    public BitMatrix getQRCodeMatrix(String sphereId);

    public BitMatrix getQRCodeMatrix(String sphereId, int width, int height);

    public String createSphere(String sphereName, String sphereType, BezirkSphereListener bezirkSphereListener);

}
