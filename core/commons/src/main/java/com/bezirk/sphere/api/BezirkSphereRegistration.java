/**
 *
 */
package com.bezirk.sphere.api;

import com.bezirk.proxy.api.impl.BezirkZirkId;

/**
 * @author Rishabh Gulati
 */
public interface BezirkSphereRegistration {

    /**
     * Registers the zirk with BezirkSphere's. In case the zirk is already
     * registered, call to this method updates the name of the zirk to
     * serviceName passed
     *
     * @param serviceId   BezirkZirkId to be registered
     * @param serviceName Name to be associated with the zirk
     * @return true if zirk was added successfully
     * <p/>
     * false otherwise
     */
    public boolean registerService(BezirkZirkId serviceId, String serviceName);

    /**
     * Unregisters zirk
     *
     * @param serviceId
     * @return
     */
    public boolean unregisterService(BezirkZirkId serviceId);
}
