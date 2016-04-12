package com.bezirk.starter;

import com.bezirk.control.messages.discovery.DiscoveryRequest;
import com.bezirk.proxy.api.impl.UhuServiceId;
import com.bezirk.sphere.api.IUhuSphereForSadl;

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
public class UhuSphereForSadlStub implements IUhuSphereForSadl {
    private static final Logger LOGGER = LoggerFactory.getLogger(IUhuSphereForSadl.class);

    @Override
    public byte[] encryptSphereContent(final String sphereId, final String serializedContent) {

        if (serializedContent == null) {
            LOGGER.error("Serialized content null for send to sphere: " + sphereId);
            return null;
        }

        return serializedContent.getBytes();
    }

    @Override
    public String decryptSphereContent(final String sphereId,final byte[] serializedContent) {
        return new String(serializedContent);
    }

    @Override
    public void encryptSphereContent(final InputStream inputStream,final OutputStream outputStream,
            final String sphereId) {
        LOGGER.error("Interface not implemented > encryptSphereContent");
    }

    @Override
    public void decryptSphereContent(final InputStream inputStream,final OutputStream outputStream,
            final String sphereId) {
        LOGGER.error("Interface not implemented > decryptSphereContent");
    }

    @Override
    public Iterable<String> getSphereMembership(final UhuServiceId serviceId) {
        final Set<String> spheres = new HashSet<String>();
        spheres.add("default sphere");
        return spheres;
    }

    @Override
    public boolean isServiceInSphere(final UhuServiceId service,final String sphereId) {

        return true;
    }

    @Override
    public String getServiceName(final UhuServiceId serviceId) {
        // I see, used during sadl discovery
        LOGGER.error("Interface not implemented > getServiceName.");
        return null;
    }

    @Override
    public void processSphereDiscoveryRequest(final DiscoveryRequest discoveryRequest) {
        // I see, used during sadl discovery
        LOGGER.error("Interface not implemented > processSphereDiscoveryRequest.");

    }

    @Override
    public String getDeviceNameFromSphere(final String deviceId) {
        LOGGER.error("Interface not implemented > getDeviceNameFromSphere.");
        return null;
    }
}
