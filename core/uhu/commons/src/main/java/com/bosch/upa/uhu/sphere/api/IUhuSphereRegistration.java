/**
 * 
 */
package com.bosch.upa.uhu.sphere.api;

import com.bosch.upa.uhu.proxy.api.impl.UhuServiceId;

/**
 * @author Rishabh Gulati
 *
 */
public interface IUhuSphereRegistration {

    /**
     * Registers the service with UhuSphere's. In case the service is already
     * registered, call to this method updates the name of the service to
     * serviceName passed
     * 
     * @param serviceId
     *            UhuServiceId to be registered
     * @param serviceName
     *            Name to be associated with the service
     * @return true if service was added successfully
     * 
     *         false otherwise
     */
    public boolean registerService(UhuServiceId serviceId, String serviceName);

    /**
     * Unregisters service
     * 
     * @param serviceId
     * @return
     */
    public boolean unregisterService(UhuServiceId serviceId);
}
