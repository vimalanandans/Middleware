/**
 *
 */
package com.bezirk.middleware.core.sphere.api;

import com.bezirk.middleware.objects.BezirkSphereInfo;

import java.util.Set;

/**
 * @author Rishabh Gulati
 */
public interface SphereDiscovery {

    void processDiscoveredSphereInfo(Set<BezirkSphereInfo> discoveredSphereInfoSet, String sphereId);
}
