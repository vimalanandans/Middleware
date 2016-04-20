/**
 * 
 */
package com.bezirk.sphere.api;

import com.bezirk.middleware.objects.UhuDeviceInfo;
import com.bezirk.middleware.objects.UhuServiceInfo;
import com.bezirk.proxy.api.impl.UhuServiceId;
import com.bezirk.sphere.impl.DeviceInformation;
import com.google.zxing.common.BitMatrix;

/**
 * @author rishabh
 *
 */
public interface ISphereUtils {

    /**
     * Checks if the service is a local service
     * 
     * @param deviceId
     *            : service owner deviceId
     * @return
     */
    public boolean isServiceLocal(String deviceId);

    /**
     * Validates the services passed by checking the registry
     * 
     * @param serviceIds
     * @return
     */
    public boolean validateServices(Iterable<UhuServiceId> serviceIds);

    /**
     * Add the member services
     * 
     */

    public DeviceInformation getDeviceInformation(String deviceId);

    public boolean addMemberServices(UhuDeviceInfo uhuDeviceInfo, String sphereId, String ownerDeviceId);

    public boolean addLocalServicesToSphere(String sphereId, Iterable<UhuServiceInfo> serviceInfos);

    public boolean addLocalServicesToSphere(Iterable<UhuServiceId> serviceIds, String sphereId);

    public boolean addLocalServicesToSphere(String sphereId);

    public Iterable<UhuServiceInfo> getUhuServiceInfo(Iterable<UhuServiceId> services);

    public String getShareCode(String sphereId);

    public String getShareCodeString(String sphereId);

    public BitMatrix getQRCodeMatrix(String sphereId);

    public BitMatrix getQRCodeMatrix(String sphereId, int width, int height);

    public String createSphere(String sphereName, String sphereType, IUhuSphereListener uhuSphereListener);

}
