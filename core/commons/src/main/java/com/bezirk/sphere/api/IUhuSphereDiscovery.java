/**
 * 
 */
package com.bezirk.sphere.api;

import java.util.Set;

import com.bezirk.middleware.objects.UhuSphereInfo;

/**
 * @author Rishabh Gulati
 *
 */
public interface IUhuSphereDiscovery {

    public void processDiscoveredSphereInfo(Set<UhuSphereInfo> discoveredSphereInfoSet, String sphereId);
}
