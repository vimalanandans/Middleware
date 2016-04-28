/**
 *
 */
package com.bezirk.sphere.api;

import com.bezirk.middleware.objects.BezirkSphereInfo;

import java.util.Set;

/**
 * @author Rishabh Gulati
 */
public interface IUhuSphereDiscovery {

    public void processDiscoveredSphereInfo(Set<BezirkSphereInfo> discoveredSphereInfoSet, String sphereId);
}
