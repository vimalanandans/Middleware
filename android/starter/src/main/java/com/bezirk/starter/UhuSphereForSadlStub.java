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
 * Created by vnd2kor on 6/12/2015.
 * This is the stub code for making sphere opaque or flat without encryption
 * This is the Quick fix. in actual this suppose to be a package altered via build
 */
public class UhuSphereForSadlStub implements IUhuSphereForSadl {
    private final Logger log = LoggerFactory.getLogger(UhuSphereForSadlStub.class);

    @Override
    public byte[] encryptSphereContent(String sphereId, String serializedContent) {

        return serializedContent.getBytes();
    }

    @Override
    public String decryptSphereContent(String sphereId, byte[] serializedContent) {
        String data = new String(serializedContent);
        return data;
    }

    @Override
    public void encryptSphereContent(InputStream inputStream, OutputStream outputStream, String sphereId) {
        log.error("Interface not implemented > encryptSphereContent");
    }

    @Override
    public void decryptSphereContent(InputStream inputStream, OutputStream outputStream, String sphereId) {
        log.error("Interface not implemented > decryptSphereContent");
    }

    @Override
    public Iterable<String> getSphereMembership(UhuServiceId serviceId) {
        Set<String> spheres = new HashSet<String>();
        spheres.add("default sphere");
        return spheres;
    }

    @Override
    public boolean isServiceInSphere(UhuServiceId service, String sphereId) {

        return true;
    }

    @Override
    public String getServiceName(UhuServiceId serviceId) {
        // I see, used during sadl discovery
        log.error("Interface not implemented > getServiceName.");
        return null;
    }

    @Override
    public void processSphereDiscoveryRequest(DiscoveryRequest discoveryRequest) {
        // I see, used during sadl discovery
        log.error("Interface not implemented > processSphereDiscoveryRequest.");

    }

    @Override
    public String getDeviceNameFromSphere(String deviceId) {
        log.error("Interface not implemented > getDeviceNameFromSphere.");
        return null;
    }
}
