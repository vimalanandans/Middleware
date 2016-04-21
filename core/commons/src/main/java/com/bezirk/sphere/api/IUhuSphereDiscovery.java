/**
 *
 */
package com.bezirk.sphere.api;

import com.bezirk.middleware.objects.UhuSphereInfo;

import java.util.Set;

/**
 * @author Rishabh Gulati
 */
public interface IUhuSphereDiscovery {

    public void processDiscoveredSphereInfo(Set<UhuSphereInfo> discoveredSphereInfoSet, String sphereId);
}
