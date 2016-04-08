package com.bosch.upa.uhu.starter.helper;

import com.bosch.upa.uhu.control.messages.discovery.DiscoveryRequest;
import com.bosch.upa.uhu.proxy.api.impl.UhuServiceId;
import com.bosch.upa.uhu.sphere.api.IUhuSphereForSadl;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by vnd2kor on 11/18/2015.
 */
public class mockUhuSphereForSadl implements IUhuSphereForSadl {
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
        //Not mandatory for mock implementation

    }

    @Override
    public void decryptSphereContent(InputStream inputStream, OutputStream outputStream, String sphereId) {
        //Not mandatory for mock implementation
    }

    @Override
    public Iterable<String> getSphereMembership(UhuServiceId serviceId) {
        List<String> sphereIdList = new ArrayList<String>();

        sphereIdList.add(0, UUID.randomUUID().toString());
        return sphereIdList;
    }

    @Override
    public boolean isServiceInSphere(UhuServiceId service, String sphereId) {
        return true;
    }

    @Override
    public String getServiceName(UhuServiceId serviceId) {
        return null;
    }

    @Override
    public void processSphereDiscoveryRequest(DiscoveryRequest discoveryRequest) {
        //Not mandatory for mock implementation

    }

    @Override
    public String getDeviceNameFromSphere(String deviceId) {
        return null;
    }
}
