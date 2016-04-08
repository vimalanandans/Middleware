/**
 * 
 */
package com.bosch.upa.uhu.sphere.api;

import java.util.Set;

import com.bosch.upa.uhu.api.objects.UhuSphereInfo;

/**
 * @author Rishabh Gulati
 *
 */
public interface IUhuSphereDiscovery {

    public void processDiscoveredSphereInfo(Set<UhuSphereInfo> discoveredSphereInfoSet, String sphereId);
}
