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
 * Created by vnd2kor on 6/12/2015. This is the stub code for making sphere
 * opaque or flat without encryption This is the Quick fix. in actual this
 * suppose to be a package altered via build
 */
public class PubSubSphereAccessStub implements PubSubSphereAccess {
    private static final Logger logger = LoggerFactory.getLogger(PubSubSphereAccess.class);

    @Override
    public byte[] encryptSphereContent(final String sphereId, final String serializedContent) {

        if (serializedContent == null) {
            logger.error("Serialized content null for send to sphere: " + sphereId);
            return null;
        }

        return serializedContent.getBytes();
    }

    @Override
    public String decryptSphereContent(final String sphereId, final byte[] serializedContent) {
        return new String(serializedContent);
    }

    @Override
    public void encryptSphereContent(final InputStream inputStream, final OutputStream outputStream,
                                     final String sphereId) {
        logger.error("Interface not implemented > encryptSphereContent");
    }

    @Override
    public void decryptSphereContent(final InputStream inputStream, final OutputStream outputStream,
                                     final String sphereId) {
        logger.error("Interface not implemented > decryptSphereContent");
    }

    @Override
    public Iterable<String> getSphereMembership(final ZirkId zirkId) {
        final Set<String> spheres = new HashSet<String>();
        spheres.add("default sphere");
        return spheres;
    }

    @Override
    public boolean isZirkInSphere(final ZirkId service, final String sphereId) {

        return true;
    }

    @Override
    public String getZirkName(final ZirkId serviceId) {
        // I see, used during sadl discovery
        logger.error("Interface not implemented > getZirkName.");
        return null;
    }

    @Override
    public void processSphereDiscoveryRequest(final DiscoveryRequest discoveryRequest) {
        // I see, used during sadl discovery
        logger.error("Interface not implemented > processSphereDiscoveryRequest.");

    }

    @Override
    public String getDeviceNameFromSphere(final String deviceId) {
        logger.error("Interface not implemented > getDeviceNameFromSphere.");
        return null;
    }
}
