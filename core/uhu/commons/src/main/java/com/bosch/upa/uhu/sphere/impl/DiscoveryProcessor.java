/**
 * 
 */
package com.bosch.upa.uhu.sphere.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bosch.upa.devices.UPADeviceInterface;
import com.bosch.upa.uhu.api.objects.UhuDeviceInfo;
import com.bosch.upa.uhu.api.objects.UhuServiceInfo;
import com.bosch.upa.uhu.api.objects.UhuSphereInfo;
import com.bosch.upa.uhu.control.messages.discovery.DiscoveryRequest;
import com.bosch.upa.uhu.control.messages.discovery.DiscoveryResponse;
import com.bosch.upa.uhu.control.messages.discovery.SphereDiscoveryResponse;
import com.bosch.upa.uhu.discovery.DiscoveryLabel;
import com.bosch.upa.uhu.discovery.SphereDiscoveryProcessor;
import com.bosch.upa.uhu.discovery.SphereDiscoveryRecord;
import com.bosch.upa.uhu.network.UhuNetworkUtilities;
import com.bosch.upa.uhu.proxy.api.impl.UhuDiscoveredService;
import com.bosch.upa.uhu.proxy.api.impl.UhuServiceEndPoint;
import com.bosch.upa.uhu.proxy.api.impl.UhuServiceId;
import com.bosch.upa.uhu.sphere.api.IUhuSphereListener;

/**
 * @author rishabh
 *
 */
public class DiscoveryProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(DiscoveryProcessor.class);
    private UPADeviceInterface upaDeviceInterface;
    private CommsUtility comms;
    private SphereRegistryWrapper sphereRegistryWrapper;
    private IUhuSphereListener uhuSphereListener;

    // sphere discovery parameters
    private int discoveryId = 0;
    private int maxDiscovered = 20;
    private int timeout = 5000;

    /**
     * 
     * @param sphereUtils
     * @param crypto
     * @param upaDeviceInterface
     * @param uhuComms
     * @param sphereRegistryWrapper
     */
    public DiscoveryProcessor(UPADeviceInterface upaDeviceInterface, CommsUtility comms,
            SphereRegistryWrapper sphereRegistryWrapper, IUhuSphereListener uhuSphereListener) {
        this.upaDeviceInterface = upaDeviceInterface;
        this.comms = comms;
        this.sphereRegistryWrapper = sphereRegistryWrapper;
        this.uhuSphereListener = uhuSphereListener;
    }

    /**
     * Process the discovered services for a sphere to incorporate more details
     * about the services.
     * 
     * The current implementation is limited to changing the UhuSphereInfo
     * object received by the {@link #getSphereInfo(String)} method. This
     * implies :
     * 
     * 1) When the getSphere is operated on a owner sphere, we get the complete
     * info with the status of active/in-active for all services
     * 
     * 2) When the getSphere is operated on a member sphere, we get only the
     * info with the status of active/in-active for a local services, i.e. for
     * services on that device. For latest information to be added for a Service
     * we would require this information coming as a part of the discovered
     * services
     * 
     * @param discoveredServices
     * @param sphereId
     * @deprecated use {@link #processDiscoveredSphereInfo(Set, String)}
     * @return
     */
    @Deprecated
    public UhuSphereInfo processDiscoveryResponse(Set<UhuDiscoveredService> discoveredServices, String sphereId) {

        UhuSphereInfo sphereInfo = null;

        if (discoveredServices != null && !discoveredServices.isEmpty()) {

            // get the sphere info
            sphereInfo = sphereRegistryWrapper.getSphereInfo(sphereId);

            if (sphereInfo != null) {
                // get the device list
                ArrayList<UhuDeviceInfo> devices = sphereInfo.getDeviceList();

                if (devices != null && !devices.isEmpty()) {

                    for (UhuDeviceInfo device : devices) {
                        // get the service list
                        List<UhuServiceInfo> services = device.getServiceList();

                        if (services != null && !services.isEmpty()) {

                            for (UhuServiceInfo serviceInfo : services) {
                                // update the service info
                                sphereRegistryWrapper.updateUhuServiceInfo(discoveredServices, serviceInfo);
                            }
                        }
                    }
                }
            }
        }
        return sphereInfo;
    }

    // TODO Reduce complexity
    /**
     * Process UhuSphereInfo set retrieved from running sphere discovery
     * 
     * @param uhuSphereInfoSet - should be not null
     * @param sphereId
     */
    public void processDiscoveredSphereInfo(Set<UhuSphereInfo> uhuSphereInfoSet, String sphereId) {
        boolean status = false;
        for (UhuSphereInfo uhuSphereInfo : uhuSphereInfoSet) {
            // verify sphereId of UhuSphereInfo object
            if (uhuSphereInfo.getSphereID().equals(sphereId)) {
                ArrayList<UhuDeviceInfo> uhuDeviceInfoList = uhuSphereInfo.getDeviceList();

                // iterate through UhuDeviceInfo
                for (UhuDeviceInfo uhuDeviceInfo : uhuDeviceInfoList) {
                    // add/update device information
                    sphereRegistryWrapper.addDevice(uhuDeviceInfo.getDeviceId(),
                            new DeviceInformation(uhuDeviceInfo.getDeviceName(), uhuDeviceInfo.getDeviceType()));

                    // add services
                    if (sphereRegistryWrapper.addMemberServices(uhuDeviceInfo, uhuSphereInfo.getSphereID(),
                            uhuDeviceInfo.getDeviceId())) {
                        for (UhuServiceInfo service : uhuDeviceInfo.getServiceList()) {
                            Sphere sphere = sphereRegistryWrapper.getSphere(uhuSphereInfo.getSphereID());
                            sphere.addService(uhuDeviceInfo.getDeviceId(), service.getServiceId());
                        }
                        status = true;
                        sphereRegistryWrapper.persist();
                    }
                }
            }
        }
        if ((uhuSphereListener != null)) {
            uhuSphereListener.onSphereDiscovered(status, sphereId);
        }
    }

    /**
     * 
     * @param discoveredSphereInfoSet
     * @param sphereId
     * @return
     */
   
    public boolean discoverSphere(String sphereId) {
        if (!sphereRegistryWrapper.containsSphere(sphereId)) {
            return false;
        }
        // TODO discuss, for now hardcoded for test
        final String serviceIdStr = "______SPHERESCANNER#1";
        final UhuServiceId serviceId = new UhuServiceId(serviceIdStr);
        final UhuServiceEndPoint sender = UhuNetworkUtilities.getServiceEndPoint(serviceId);
        LOGGER.debug("Discovery initiator device : " + sender.device);

        // Assign discovery Id
        discoveryId = (++discoveryId) % Integer.MAX_VALUE;

        final DiscoveryRequest discoveryRequest = new DiscoveryRequest(sphereId, sender, null, null, discoveryId,
                timeout, maxDiscovered);
        comms.sendMessage(discoveryRequest);
        // Add discovery record
        DiscoveryLabel dLabel = new DiscoveryLabel(sender, discoveryId, true);
        SphereDiscoveryRecord discoveryRecord = new SphereDiscoveryRecord(sphereId, timeout, maxDiscovered);
        SphereDiscoveryProcessor.getDiscovery().addRequest(dLabel, discoveryRecord);

        return true;
    }

    /**
     * Process the {@link DiscoveryRequest}
     * 
     * @param discoveryRequest
     * @return true if a {@link DiscoveryResponse} was sent for this request.
     *         This happens when a device has services that are a part of the
     *         sphere initiating the request.<br>
     *         false otherwise
     * 
     */
    public boolean processRequest(DiscoveryRequest discoveryRequest) {
        if (validateRequest(discoveryRequest)) {
            UhuSphereInfo uhuSphereInfo = sphereRegistryWrapper.getSphereInfo(discoveryRequest.getSphereId());
            if (uhuSphereInfo != null && uhuSphereInfo.getDeviceList() != null) {
                // send only the services belongs to this device
                Iterator<UhuDeviceInfo> deviceIterator = uhuSphereInfo.getDeviceList().iterator();
                while (deviceIterator.hasNext()) {
                    UhuDeviceInfo localDeviceInfo = deviceIterator.next();
                    // get the local device or development device id
                    if (localDeviceInfo.getDeviceId().equals(upaDeviceInterface.getDeviceId()) || localDeviceInfo
                            .getDeviceId().equalsIgnoreCase(SphereRegistryWrapper.DEVELOPMENT_DEVICE_ID)) {

                        // construct the uhu sphere info with only local device
                        // info info
                        ArrayList<UhuDeviceInfo> localDeviceList = new ArrayList<UhuDeviceInfo>();
                        localDeviceList.add(localDeviceInfo);

                        UhuSphereInfo discoverResponseSphereInfo = new UhuSphereInfo(uhuSphereInfo.getSphereID(),
                                uhuSphereInfo.getSphereName(), uhuSphereInfo.getSphereType(), localDeviceList,
                                uhuSphereInfo.getPipeList());

                        SphereDiscoveryResponse response = new SphereDiscoveryResponse(discoveryRequest.getSender(),
                                discoveryRequest.getSphereId(), discoveryRequest.getUniqueKey(),
                                discoveryRequest.getDiscoveryId());

                        response.setUhuSphereInfo(discoverResponseSphereInfo);
                        LOGGER.debug("Discovery Response created. UhuSphereInfo sent\n" + discoverResponseSphereInfo);
                        return comms.sendMessage(response);
                    }
                }

            } else {
                LOGGER.debug("UhuSphereInfo or deviceList is null");
            }
        } else {
            LOGGER.debug("SphereDiscovery: Sphere Id is invalid");
        }
        return false;

    }

    private boolean validateRequest(DiscoveryRequest discoveryRequest) {
        if (discoveryRequest.getSender().device.equals(UhuNetworkUtilities.getDeviceIp())) {
            LOGGER.debug("Msg from same device, ignoring dicovery request");
            return false;
        }

        if (!sphereRegistryWrapper.containsSphere(discoveryRequest.getSphereId())) {
            LOGGER.error("Request with invalid sphereId reeived at DiscoveryProcessor");
        }
        return true;
    }

}
