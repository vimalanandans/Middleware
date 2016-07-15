package com.bezirk.starter;

import com.bezirk.control.messages.discovery.DiscoveryRequest;
import com.bezirk.proxy.api.impl.ZirkId;
import com.bezirk.sphere.api.SphereServiceAccess;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by vnd2kor on 6/12/2015.
 * This is the stub code for making sphere opaque or flat without encryption
 * This is the Quick fix. in actual this suppose to be a package altered via build
 */
public class SphereServiceAccessStub implements SphereServiceAccess {
    private static final Logger logger = LoggerFactory.getLogger(SphereServiceAccessStub.class);

    @Override
    public boolean registerService(ZirkId zirkId, String zirkName) {
        return false;
    }

    @Override
    public boolean unregisterService(ZirkId serviceId) {
        return false;
    }

    @Override
    public Iterable<String> getSphereMembership(ZirkId zirkId) {
        Set<String> spheres = new HashSet<String>();
        spheres.add("default sphere");
        return spheres;
    }

    @Override
    public boolean isServiceInSphere(ZirkId zirk, String sphereId) {

        return true;
    }

    @Override
    public String getServiceName(ZirkId zirkId) {
        // I see, used during sadl discovery
        logger.error("Interface not implemented > getServiceName.");
        return null;
    }

    @Override
    public void processSphereDiscoveryRequest(DiscoveryRequest discoveryRequest) {
        // I see, used during sadl discovery
        logger.error("Interface not implemented > processSphereDiscoveryRequest.");

    }

    @Override
    public String getDeviceNameFromSphere(String deviceId) {
        logger.error("Interface not implemented > getDeviceNameFromSphere.");
        return null;
    }
}
