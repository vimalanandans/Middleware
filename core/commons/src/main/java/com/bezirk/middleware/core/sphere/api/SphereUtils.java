/**
 *
 */
package com.bezirk.middleware.core.sphere.api;

import com.bezirk.middleware.objects.BezirkZirkInfo;
import com.bezirk.middleware.objects.BezirkDeviceInfo;
import com.bezirk.middleware.proxy.api.impl.ZirkId;
import com.bezirk.middleware.core.sphere.impl.DeviceInformation;
import com.google.zxing.common.BitMatrix;

/**
 * @author rishabh
 */
public interface SphereUtils {

    /**
     * Checks if the zirk is a local zirk
     *
     * @param deviceId : zirk owner deviceId
     */
    boolean isServiceLocal(String deviceId);

    /**
     * Validates the services passed by checking the registry
     */
    boolean validateServices(Iterable<ZirkId> serviceIds);

    DeviceInformation getDeviceInformation(String deviceId);

    boolean addMemberServices(BezirkDeviceInfo bezirkDeviceInfo, String sphereId, String ownerDeviceId);

    boolean addLocalServicesToSphere(String sphereId, Iterable<BezirkZirkInfo> serviceInfo);

    boolean addLocalServicesToSphere(Iterable<ZirkId> serviceIds, String sphereId);

    boolean addLocalServicesToSphere(String sphereId);

    Iterable<BezirkZirkInfo> getBezirkServiceInfo(Iterable<ZirkId> services);

    String getShareCode(String sphereId);

    String getShareCodeString(String sphereId);

    BitMatrix getQRCodeMatrix(String sphereId);

    BitMatrix getQRCodeMatrix(String sphereId, int width, int height);

    String createSphere(String sphereName, String sphereType, SphereListener sphereListener);

}
