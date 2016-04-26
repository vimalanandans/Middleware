/**
 *
 */
package com.bezirk.sphere.api;

import com.bezirk.proxy.api.impl.UhuZirkId;

/**
 * @author Rishabh Gulati
 */
public interface IUhuSphereRegistration {

    /**
     * Registers the service with UhuSphere's. In case the service is already
     * registered, call to this method updates the name of the service to
     * serviceName passed
     *
     * @param serviceId   UhuZirkId to be registered
     * @param serviceName Name to be associated with the service
     * @return true if service was added successfully
     * <p/>
     * false otherwise
     */
    public boolean registerService(UhuZirkId serviceId, String serviceName);

    /**
     * Unregisters service
     *
     * @param serviceId
     * @return
     */
    public boolean unregisterService(UhuZirkId serviceId);
}
