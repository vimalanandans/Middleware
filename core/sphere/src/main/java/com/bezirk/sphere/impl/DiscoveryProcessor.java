/**
 *
 */
package com.bezirk.sphere.impl;

import com.bezirk.control.messages.discovery.DiscoveryRequest;
import com.bezirk.control.messages.discovery.SphereDiscoveryResponse;
import com.bezirk.devices.DeviceInterface;
import com.bezirk.proxy.api.impl.BezirkDiscoveredZirk;
import com.bezirk.pubsubbroker.discovery.DiscoveryLabel;
import com.bezirk.sphere.api.SphereListener;
import com.bezirk.sphere.discovery.SphereDiscoveryProcessor;
import com.bezirk.sphere.discovery.SphereDiscoveryRecord;
import com.bezirk.middleware.objects.BezirkDeviceInfo;
import com.bezirk.middleware.objects.BezirkSphereInfo;
import com.bezirk.middleware.objects.BezirkZirkInfo;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.proxy.api.impl.ZirkId;
import com.bezrik.network.BezirkNetworkUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author rishabh
 */
public class DiscoveryProcessor {
    private static final Logger logger = LoggerFactory.getLogger(DiscoveryProcessor.class);

    private DeviceInterface deviceInterface;
    private CommsUtility comms;
    private SphereRegistryWrapper sphereRegistryWrapper;
    private SphereListener sphereListener;

    // sphere discovery parameters
    private int discoveryId = 0;
    private int maxDiscovered = 20;
    private int timeout = 5000; /* ms */

    public DiscoveryProcessor(DeviceInterface deviceInterface, CommsUtility comms,
                              SphereRegistryWrapper sphereRegistryWrapper, SphereListener sphereListener) {
        this.deviceInterface = deviceInterface;
        this.comms = comms;
        this.sphereRegistryWrapper = sphereRegistryWrapper;
        this.sphereListener = sphereListener;
    }

    /**
     * Process the discovered services for a sphere to incorporate more details
     * about the services.
     * <p>
     * The current implementation is limited to changing the BezirkSphereInfo
     * object received by the {@link SphereRegistryWrapper#getSphereInfo(String)} method. This
     * implies :
     * </p><p>
     * 1) When the getSphere is operated on a owner sphere, we get the complete
     * info with the status of active/in-active for all services
     * </p><p>
     * 2) When the getSphere is operated on a member sphere, we get only the
     * info with the status of active/in-active for a local services, i.e. for
     * services on that device. For latest information to be added for a Zirk
     * we would require this information coming as a part of the discovered
     * services
     * </p>
     *
     * @param discoveredServices
     * @param sphereId
     * @return
     * @deprecated use {@link #processDiscoveredSphereInfo(Set, String)}
     */
    @Deprecated
    public BezirkSphereInfo processDiscoveryResponse(Set<BezirkDiscoveredZirk> discoveredServices, String sphereId) {

        BezirkSphereInfo sphereInfo = null;

        if (discoveredServices != null && !discoveredServices.isEmpty()) {

            // get the sphere info
            sphereInfo = sphereRegistryWrapper.getSphereInfo(sphereId);

            if (sphereInfo != null) {
                // get the device list
                ArrayList<BezirkDeviceInfo> devices = sphereInfo.getDeviceList();

                if (devices != null && !devices.isEmpty()) {

                    for (BezirkDeviceInfo device : devices) {
                        // get the zirk list
                        List<BezirkZirkInfo> services = device.getZirkList();

                        if (services != null && !services.isEmpty()) {

                            for (BezirkZirkInfo serviceInfo : services) {
                                // update the zirk info
                                sphereRegistryWrapper.updateBezirkServiceInfo(discoveredServices, serviceInfo);
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
     * Process BezirkSphereInfo set retrieved from running sphere discovery
     *
     * @param bezirkSphereInfoSet - should be not null
     * @param sphereId
     */
    public void processDiscoveredSphereInfo(Set<BezirkSphereInfo> bezirkSphereInfoSet, String sphereId) {
        boolean status = false;
        for (BezirkSphereInfo bezirkSphereInfo : bezirkSphereInfoSet) {
            // verify sphereId of BezirkSphereInfo object
            if (bezirkSphereInfo.getSphereID().equals(sphereId)) {
                ArrayList<BezirkDeviceInfo> bezirkDeviceInfoList = bezirkSphereInfo.getDeviceList();

                // iterate through BezirkDeviceInfo
                for (BezirkDeviceInfo bezirkDeviceInfo : bezirkDeviceInfoList) {
                    // add/update device information
                    sphereRegistryWrapper.addDevice(bezirkDeviceInfo.getDeviceId(),
                            new DeviceInformation(bezirkDeviceInfo.getDeviceName(), bezirkDeviceInfo.getDeviceType()));

                    // add services
                    if (sphereRegistryWrapper.addMemberServices(bezirkDeviceInfo, bezirkSphereInfo.getSphereID(),
                            bezirkDeviceInfo.getDeviceId())) {
                        for (BezirkZirkInfo service : bezirkDeviceInfo.getZirkList()) {
                            Sphere sphere = sphereRegistryWrapper.getSphere(bezirkSphereInfo.getSphereID());
                            sphere.addService(bezirkDeviceInfo.getDeviceId(), service.getZirkId());
                        }
                        status = true;
                        sphereRegistryWrapper.persist();
                    }
                }
            }
        }
        if ((sphereListener != null)) {
            sphereListener.onSphereDiscovered(status, sphereId);
        }
    }

    public boolean discoverSphere(String sphereId) {
        if (!sphereRegistryWrapper.containsSphere(sphereId)) {
            return false;
        }
        // TODO discuss, for now hardcoded for test
        final String serviceIdStr = "______SPHERESCANNER#1";
        final ZirkId serviceId = new ZirkId(serviceIdStr);
        final BezirkZirkEndPoint sender = BezirkNetworkUtilities.getServiceEndPoint(serviceId);
        logger.debug("Discovery initiator device : " + sender.device);

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
     * This happens when a device has services that are a part of the
     * sphere initiating the request.<br>
     * false otherwise
     */
    public boolean processRequest(DiscoveryRequest discoveryRequest) {
        if (validateRequest(discoveryRequest)) {
            BezirkSphereInfo bezirkSphereInfo = sphereRegistryWrapper.getSphereInfo(discoveryRequest.getSphereId());
            if (bezirkSphereInfo != null && bezirkSphereInfo.getDeviceList() != null) {
                // send only the services belongs to this device
                for (BezirkDeviceInfo localDeviceInfo : bezirkSphereInfo.getDeviceList()) {
                    // get the local device or development device id
                    if (localDeviceInfo.getDeviceId().equals(deviceInterface.getDeviceId()) || localDeviceInfo
                            .getDeviceId().equalsIgnoreCase(SphereRegistryWrapper.DEVELOPMENT_DEVICE_ID)) {

                        // construct the bezirk sphere info with only local device
                        // info info
                        ArrayList<BezirkDeviceInfo> localDeviceList = new ArrayList<BezirkDeviceInfo>();
                        localDeviceList.add(localDeviceInfo);

                        BezirkSphereInfo discoverResponseSphereInfo = new BezirkSphereInfo(bezirkSphereInfo.getSphereID(),
                                bezirkSphereInfo.getSphereName(), bezirkSphereInfo.getSphereType(), localDeviceList,
                                bezirkSphereInfo.getPipeList());

                        SphereDiscoveryResponse response = new SphereDiscoveryResponse(discoveryRequest.getSender(),
                                discoveryRequest.getSphereId(), discoveryRequest.getUniqueKey(),
                                discoveryRequest.getDiscoveryId());

                        response.setBezirkSphereInfo(discoverResponseSphereInfo);
                        logger.debug("Discovery Response created. BezirkSphereInfo sent\n" + discoverResponseSphereInfo);
                        return comms.sendMessage(response);
                    }
                }

            } else {
                logger.debug("BezirkSphereInfo or deviceList is null");
            }
        } else {
            logger.debug("SphereDiscovery: sphere Id is invalid");
        }
        return false;

    }

    private boolean validateRequest(DiscoveryRequest discoveryRequest) {
        if (discoveryRequest.getSender().device.equals(BezirkNetworkUtilities.getDeviceIp())) {
            logger.debug("Msg from same device, ignoring discovery request");
            return false;
        }

        if (!sphereRegistryWrapper.containsSphere(discoveryRequest.getSphereId())) {
            logger.error("Request with invalid sphereId received at DiscoveryProcessor");
        }
        return true;
    }

}