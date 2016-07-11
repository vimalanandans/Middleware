package com.bezirk.sphere.impl;

import com.bezirk.proxy.api.impl.ZirkId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Rishab Gulati
 */
public class Sphere implements Serializable {
    private static final long serialVersionUID = -8206651024005270100L;
    private static final Logger logger = LoggerFactory.getLogger(Sphere.class);
    private static final String DEFAULT_SPHERE_NAME = "Default sphere";
    /**
     * Name of the sphere
     */
    protected String sphereName;
    /**
     * List of deviceId's who own the sphere
     */
    protected Set<String> ownerDevices;
    protected String sphereType;
    /**
     * Map maintains the services which belong to a particular device [device id
     * : List of ZirkId]
     */
    protected LinkedHashMap<String, ArrayList<ZirkId>> deviceServices;

    public Sphere() {
    }

    /**
     * @return the sphereName
     */
    public final String getSphereName() {
        return sphereName;
    }

    /**
     * @param sphereName the sphereName to set
     */
    public final void setSphereName(String sphereName) {
        this.sphereName = sphereName;
    }

    /**
     * @return the ownerDevices
     */
    public final Set<String> getOwnerDevices() {
        return ownerDevices;
    }

    /**
     * @return the deviceServices
     */
    public final Map<String, ArrayList<ZirkId>> getDeviceServices() {
        return deviceServices;
    }

    /**
     * @param deviceServices the deviceServices to set
     */
    public final void setDeviceServices(LinkedHashMap<String, ArrayList<ZirkId>> deviceServices) {
        this.deviceServices = deviceServices;
    }

    /**
     * Adds a zirk to the deviceId passed
     *
     * @param deviceId
     * @param service
     * @return
     * @deprecated use {@link #addService(String, String)}
     */
    @Deprecated
    public final boolean addService(String deviceId, ZirkId service) {
        return addService(deviceId, service.getZirkId());
    }

    /**
     * Adds a zirk to the deviceId passed
     *
     * @param deviceId
     * @param zirkId
     * @return
     */
    public final boolean addService(String deviceId, String zirkId) {
        boolean success = false;

        if (deviceId == null) {
            logger.debug("addService: deviceid is null ");
            return success;
        }

        if (zirkId == null) {
            logger.debug("addService: zirk is null ");
            return success;
        }

        if (deviceServices == null) {
            logger.debug("addService: deviceServices is null ");
            return success;
        }

        // check if deviceId is not present in the map
        if (!deviceServices.containsKey(deviceId)) {
            deviceServices.put(deviceId, new ArrayList<ZirkId>());
        }

        // add the zirk to the set of zirkId
        ArrayList<ZirkId> serviceList = deviceServices.get(deviceId);

        ZirkId service = new ZirkId(zirkId);

        if (!serviceList.contains(service)) {
            serviceList.add(service);
            logger.debug("Zirk " + service + " added to sphere " + sphereName + "\ndevice Id " + deviceId);
        }

        success = true;

        return success;
    }

    public final boolean addServices(String deviceId, Iterable<ZirkId> services) {
        boolean success = false;
        if (deviceId != null && services != null && deviceServices != null) {
            // check if deviceId is not present in the map
            if (!deviceServices.containsKey(deviceId)) {
                deviceServices.put(deviceId, new ArrayList<ZirkId>());
            }
            // add all the services to the set of zirkId
            ArrayList<ZirkId> serviceList = deviceServices.get(deviceId);
            for (ZirkId serviceId : services) {
                if (!serviceList.contains(serviceId)) {
                    serviceList.add(serviceId);
                    logger.debug("Zirk " + serviceId + " added to sphere " + sphereName);
                }
            }
            success = true;
        }
        return success;
    }

    /**
     * Removes a zirk from the sphere
     * <p>
     * //TODO : What happens if this zirk is a part of a sphere owned by
     * another device. If we remove the zirk we need to inform the owner
     * about the change. What if the owner is not available?
     * </p>
     *
     * @param zirkId
     * @return
     */
    public final boolean removeService(ZirkId zirkId) {
        return false;
    }

    public String getSphereType() {

        return sphereType;
    }

    public void setSphereType(String sphereType) {
        this.sphereType = sphereType;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((deviceServices == null) ? 0 : deviceServices.hashCode());
        result = prime * result + ((ownerDevices == null) ? 0 : ownerDevices.hashCode());
        result = prime * result + ((sphereName == null) ? 0 : sphereName.hashCode());
        result = prime * result + ((sphereType == null) ? 0 : sphereType.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Sphere other = (Sphere) obj;
        if (deviceServices == null) {
            if (other.deviceServices != null)
                return false;
        } else if (!deviceServices.equals(other.deviceServices))
            return false;
        if (ownerDevices == null) {
            if (other.ownerDevices != null)
                return false;
        } else if (!ownerDevices.equals(other.ownerDevices))
            return false;
        if (sphereName == null) {
            if (other.sphereName != null)
                return false;
        } else if (!sphereName.equals(other.sphereName))
            return false;
        if (sphereType == null) {
            if (other.sphereType != null)
                return false;
        } else if (!sphereType.equals(other.sphereType))
            return false;
        return true;
    }

}
