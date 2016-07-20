/**
 *
 */
package com.bezirk.sphere.api;

import com.bezirk.control.messages.discovery.DiscoveryRequest;
import com.bezirk.proxy.api.impl.ZirkId;

/**
 * @author Rishabh Gulati
 */
public interface SphereServiceAccess {

    /**
     * Registers the zirk with BezirkSphere's. In case the zirk is already
     * registered, call to this method updates the name of the zirk to
     * zirkName passed
     *
     * @param zirkId   ZirkId to be registered
     * @param zirkName Name to be associated with the zirk
     * @return <code>true</code> if zirk was added successfully
     */
    public boolean registerService(ZirkId zirkId, String zirkName);

    /** unregister service*/
    public boolean unregisterService(ZirkId serviceId);

    /**
     * Provides iterable collection of sphereIds associated with passed
     * ZirkId
     *
     * @param zirkId ZirkId for retrieving stored membership information
     * @return iterable Collection of sphereIds for the passed ZirkId, <code>null</code> in case
     * the <code>zirkId</code> passed is <code>null</code> or not registered
     */
    public Iterable<String> getSphereMembership(ZirkId zirkId);

    // TODO add to wiki : found while refactoring to the new API

    /**
     * Checks if the zirk is a part of the sphere
     *
     * @param service  ZirkId for finding existence in a sphere
     * @param sphereId sphere to be tested
     * @return true if the zirk exist in the sphere false otherwise
     */
    public boolean isServiceInSphere(ZirkId service, String sphereId);

    /**
     * Gets the zirk name of the passed ZirkId
     *
     * @param serviceId ZirkId for retrieving the zirk name
     * @return Zirk name if the zirk id is valid and not null null
     * otherwise
     */
    public String getServiceName(ZirkId serviceId);

    /**
     * This method handles processing the sphere related discovery request
     *
     * @param discoveryRequest
     */
    public void processSphereDiscoveryRequest(DiscoveryRequest discoveryRequest);

    /**
     * @param deviceId the deviceId whose Device Name needs to be known
     * @return Device Name if exists, null otherwise
     */
    public String getDeviceNameFromSphere(String deviceId);

}