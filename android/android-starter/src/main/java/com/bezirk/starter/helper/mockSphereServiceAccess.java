package com.bezirk.starter.helper;

import com.bezirk.control.messages.discovery.DiscoveryRequest;
import com.bezirk.proxy.api.impl.ZirkId;
import com.bezirk.sphere.api.SphereServiceAccess;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class mockSphereServiceAccess implements SphereServiceAccess {


    @Override
    public boolean registerService(ZirkId zirkId, String zirkName) {
        return true;
    }

    @Override
    public boolean unregisterService(ZirkId serviceId) {
        return true;
    }

    @Override
    public Iterable<String> getSphereMembership(ZirkId zirkId) {
        List<String> sphereIdList = new ArrayList<String>();

        sphereIdList.add(0, UUID.randomUUID().toString());
        return sphereIdList;
    }

    @Override
    public boolean isServiceInSphere(ZirkId service, String sphereId) {
        return true;
    }

    @Override
    public String getServiceName(ZirkId serviceId) {
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
