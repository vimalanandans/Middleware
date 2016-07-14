package com.bezirk.starter;

import com.bezirk.control.messages.discovery.DiscoveryRequest;
import com.bezirk.proxy.api.impl.ZirkId;
import com.bezirk.sphere.api.PubSubSphereAccess;

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
public class PubSubSphereAccessStub implements PubSubSphereAccess {
    private static final Logger logger = LoggerFactory.getLogger(PubSubSphereAccessStub.class);

    @Override
    public byte[] encryptSphereContent(String sphereId, String serializedContent) {

        return serializedContent.getBytes();
    }

    @Override
    public String decryptSphereContent(String sphereId, byte[] serializedContent) {
        return new String(serializedContent);
    }

    @Override
    public void encryptSphereContent(InputStream inputStream, OutputStream outputStream, String sphereId) {
        logger.error("Interface not implemented > encryptSphereContent");
    }

    @Override
    public void decryptSphereContent(InputStream inputStream, OutputStream outputStream, String sphereId) {
        logger.error("Interface not implemented > decryptSphereContent");
    }

    @Override
    public Iterable<String> getSphereMembership(ZirkId zirkId) {
        Set<String> spheres = new HashSet<String>();
        spheres.add("default sphere");
        return spheres;
    }

    @Override
    public boolean isZirkInSphere(ZirkId zirk, String sphereId) {

        return true;
    }

    @Override
    public String getZirkName(ZirkId zirkId) {
        // I see, used during sadl discovery
        logger.error("Interface not implemented > getZirkName.");
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
