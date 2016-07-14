/**
 *
 */
package com.bezirk.sphere.api;

import com.bezirk.proxy.api.impl.ZirkId;

/**
 * @author Rishabh Gulati
 */
public interface SphereRegistration {

    /**
     * Registers the zirk with BezirkSphere's. In case the zirk is already
     * registered, call to this method updates the name of the zirk to
     * zirkName passed
     *
     * @param zirkId   ZirkId to be registered
     * @param zirkName Name to be associated with the zirk
     * @return <code>true</code> if zirk was added successfully
     */
    public boolean registerZirk(ZirkId zirkId, String zirkName);

    public boolean unregisterZirk(ZirkId serviceId);
}
