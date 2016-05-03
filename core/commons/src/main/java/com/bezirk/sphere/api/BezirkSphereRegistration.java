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
     * zirkName passed
     *
     * @param zirkId   BezirkZirkId to be registered
     * @param zirkName Name to be associated with the zirk
     * @return <code>true</code> if zirk was added successfully
     */
    public boolean registerZirk(BezirkZirkId zirkId, String zirkName);

    public boolean unregisterZirk(BezirkZirkId serviceId);
}
