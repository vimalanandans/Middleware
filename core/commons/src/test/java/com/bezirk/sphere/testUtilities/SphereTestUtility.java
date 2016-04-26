/**
 *
 */
package com.bezirk.sphere.testUtilities;

import com.bezirk.commons.UhuId;
import com.bezirk.devices.UPADeviceInterface;
import com.bezirk.middleware.objects.UhuDeviceInfo;
import com.bezirk.middleware.objects.UhuServiceInfo;
import com.bezirk.proxy.api.impl.UhuServiceEndPoint;
import com.bezirk.proxy.api.impl.UhuServiceId;
import com.bezirk.sphere.api.UhuSphereType;
import com.bezirk.sphere.impl.OwnerService;
import com.bezirk.sphere.impl.Service;
import com.bezirk.sphere.impl.Sphere;
import com.bezirk.sphere.impl.SphereExchangeData;
import com.bezirk.sphere.impl.SphereRegistryWrapper;
import com.bezirk.sphere.messages.CatchRequest;
import com.bezirk.sphere.messages.CatchResponse;
import com.bezirk.sphere.messages.ShareRequest;
import com.bezirk.sphere.messages.ShareResponse;
import com.bezrik.network.UhuNetworkUtilities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

/**
 * @author rishabh
 */
public class SphereTestUtility {

    /* owner sphere names and ids */
    public final String OWNER_SPHERE_NAME_1 = "OWNER_SPHERE_NAME_1";
    public final String OWNER_SPHERE_NAME_2 = "OWNER_SPHERE_NAME_2";
    // public final String OWNER_SPHERE_ID_1 = UUID.randomUUID().toString();
    /* owner sphere names and ids */
    public final String MEMBER_SPHERE_NAME_1 = "MEMBER_SPHERE_NAME_1";
    // public final String OWNER_SPHERE_ID_2 = UUID.randomUUID().toString();
    public final String MEMBER_SPHERE_NAME_2 = "MEMBER_SPHERE_NAME_2";
    // public final String MEMBER_SPHERE_ID_1 = UUID.randomUUID().toString();
    /* owner services names and ids */
    public final String OWNER_SERVICE_NAME_1 = "OWNER_SERVICE_NAME_1";
    // public final String MEMBER_SPHERE_ID_2 = UUID.randomUUID().toString();
    public final UhuServiceId OWNER_SERVICE_ID_1 = new UhuServiceId(OWNER_SERVICE_NAME_1);
    public final String OWNER_SERVICE_NAME_2 = "OWNER_SERVICE_NAME_2";
    public final UhuServiceId OWNER_SERVICE_ID_2 = new UhuServiceId(OWNER_SERVICE_NAME_2);
    public final String OWNER_SERVICE_NAME_3 = "OWNER_SERVICE_NAME_3";
    public final UhuServiceId OWNER_SERVICE_ID_3 = new UhuServiceId(OWNER_SERVICE_NAME_3);
    public final String OWNER_SERVICE_NAME_4 = "OWNER_SERVICE_NAME_4";
    public final UhuServiceId OWNER_SERVICE_ID_4 = new UhuServiceId(OWNER_SERVICE_NAME_4);
    /* member services names and ids */
    public final String MEMBER_SERVICE_NAME_1 = "MEMBER_SERVICE_NAME_1";
    public final UhuServiceId MEMBER_SERVICE_ID_1 = new UhuServiceId(MEMBER_SERVICE_NAME_1);
    public final String MEMBER_SERVICE_NAME_2 = "MEMBER_SERVICE_NAME_2";
    public final UhuServiceId MEMBER_SERVICE_ID_2 = new UhuServiceId(MEMBER_SERVICE_NAME_2);
    public final String MEMBER_SERVICE_NAME_3 = "MEMBER_SERVICE_NAME_3";
    public final UhuServiceId MEMBER_SERVICE_ID_3 = new UhuServiceId(MEMBER_SERVICE_NAME_3);
    public final String MEMBER_SERVICE_NAME_4 = "MEMBER_SERVICE_NAME_4";
    public final UhuServiceId MEMBER_SERVICE_ID_4 = new UhuServiceId(MEMBER_SERVICE_NAME_4);
    /* devices */
    public final UPADeviceInterface DEVICE_1;
    public final UPADeviceInterface DEVICE_2;

    ;
    private SphereRegistryWrapper sphereRegistryWrapper;
    public SphereTestUtility(SphereRegistryWrapper sphereRegistryWrapper, UPADeviceInterface localDevice) {
        this.sphereRegistryWrapper = sphereRegistryWrapper;
        this.DEVICE_1 = localDevice;
        this.DEVICE_2 = new Device();
    }

    /**
     * Generates the following<br>
     * sphere [Name: {@link #OWNER_SPHERE_NAME_1} Id: {@link #OWNER_SPHERE_ID_1}
     * Owner: {@link #DEVICE_1}]<br>
     * Contained Services:<br>
     * 1) [Name: {@link #OWNER_SERVICE_NAME_1} Id: {@link #OWNER_SERVICE_ID_1}
     * Owner: {@link #DEVICE_1}]<br>
     * 2) [Name: {@link #OWNER_SERVICE_NAME_2} Id: {@link #OWNER_SERVICE_ID_2}
     * Owner: {@link #DEVICE_1}]
     */

    public final String generateOwnerCombo() {
        // create owner sphere
        String sphereId = sphereRegistryWrapper.createSphere(OWNER_SPHERE_NAME_1, null, null);
        Sphere sphere = sphereRegistryWrapper.getSphere(sphereId);

        // create service1
        HashSet<String> sphereSet1 = new HashSet<>();
        sphereSet1.add(sphereId);
        Service service1 = new OwnerService(OWNER_SERVICE_NAME_1, DEVICE_1.getDeviceId(), sphereSet1);
        sphereRegistryWrapper.addService(OWNER_SERVICE_ID_1.getUhuServiceId(), service1);

        // create service2
        HashSet<String> sphereSet2 = new HashSet<>();
        sphereSet2.add(sphereId);
        Service service2 = new OwnerService(OWNER_SERVICE_NAME_2, DEVICE_1.getDeviceId(), sphereSet2);
        sphereRegistryWrapper.addService(OWNER_SERVICE_ID_2.getUhuServiceId(), service2);

        LinkedHashMap<String, ArrayList<UhuServiceId>> deviceServices = new LinkedHashMap<>();

        // create list of services for the sphere
        ArrayList<UhuServiceId> services = new ArrayList<>();
        services.add(OWNER_SERVICE_ID_1);
        services.add(OWNER_SERVICE_ID_2);

        // add services to the deviceServices map for the sphere
        deviceServices.put(DEVICE_1.getDeviceId(), services);
        sphere.setDeviceServices(deviceServices);
        sphereRegistryWrapper.persist();
        return sphereId;
    }

    /**
     * Generates the following dummy data [not persisted in the registry]<br>
     * UhuDeviceInfo [Device: {@link #DEVICE_2}]<br>
     * Contained UhuServiceInfo(s):<br>
     * 1) [Name: {@link #OWNER_SERVICE_NAME_3} Id: {@link #OWNER_SERVICE_ID_3}
     * Owner: {@link #DEVICE_2}]<br>
     * 2) [Name: {@link #OWNER_SERVICE_NAME_4} Id: {@link #OWNER_SERVICE_ID_4}
     * Owner: {@link #DEVICE_2}]
     */
    public final UhuDeviceInfo getUhuDeviceInfo() {
        // create sharingService1
        UhuServiceInfo sharingService1 = new UhuServiceInfo(OWNER_SERVICE_ID_3.getUhuServiceId(), OWNER_SERVICE_NAME_3,
                Type.OWNER.toString(), false, false);

        // create sharingService2
        UhuServiceInfo sharingService2 = new UhuServiceInfo(OWNER_SERVICE_ID_4.getUhuServiceId(), OWNER_SERVICE_NAME_4,
                Type.OWNER.toString(), false, false);

        // add services to list
        List<UhuServiceInfo> servicesShared = new ArrayList<UhuServiceInfo>();
        servicesShared.add(sharingService1);
        servicesShared.add(sharingService2);

        // create dummy UhuDeviceInfo to be shared
        UhuDeviceInfo uhuDeviceInfo = new UhuDeviceInfo(DEVICE_2.getDeviceId(), DEVICE_2.getDeviceName(),
                DEVICE_2.getDeviceType(), null, false, servicesShared);
        return uhuDeviceInfo;
    }

    /**
     * Generates the dummy exchange data<br>
     *
     * @return - exchange data as a string
     */
    public final String getExchangeData() {
        SphereExchangeData shareData = getSphereExchangeDataObj();
        return shareData.serialize();
    }

    /**
     * Generates the following dummy exchange data<br>
     * Device: {@link #DEVICE_2}
     * SphereId: randomly generated id
     * SphereName: {@link #OWNER_SPHERE_NAME_2}
     * SphereType: {@link UhuSphereType#UHU_SPHERE_TYPE_HOME_ENTERTAINMENT}
     * SphereKey: randomly generated id
     * SpherePublicKey: randomly generated id
     *
     * @return - constructed SphereExchangeData object
     */
    public final SphereExchangeData getSphereExchangeDataObj() {
        SphereExchangeData shareData = new SphereExchangeData();
        // device info
        shareData.setDeviceID(DEVICE_2.getDeviceId());
        shareData.setDeviceName(DEVICE_2.getDeviceName());
        shareData.setDeviceType(DEVICE_2.getDeviceType());

        // sphere info
        shareData.setSphereID(UUID.randomUUID().toString());
        shareData.setSphereName(OWNER_SPHERE_NAME_2);
        shareData.setSphereType(UhuSphereType.UHU_SPHERE_TYPE_HOME_ENTERTAINMENT);

        shareData.setSphereKey(UUID.randomUUID().toString().getBytes());
        shareData.setOwnerPublicKeyBytes(UUID.randomUUID().toString().getBytes());

        return shareData;
    }

    /**
     * Generates a CatchResponse object consisting of:
     * UhuServiceEndPoint object: {@link #DEVICE_2}
     * catcherSphereId: {@link #generateOwnerCombo()}
     * catcherDeviceId: {@link #DEVICE_1}
     * UhuDeviceInfo object: {@link #getUhuDeviceInfo()}
     *
     * @return - CatchResponse object.
     */
    public final CatchResponse getCatchResponseObj() {
        String catcherSphereId = generateOwnerCombo();

        /**create the CatchResponse**/
        UhuServiceEndPoint sender = new UhuServiceEndPoint(OWNER_SERVICE_ID_3);
        sender.device = DEVICE_2.getDeviceName();
        CatchResponse catchResponse = new CatchResponse(sender, catcherSphereId, DEVICE_1.getDeviceId(), getUhuDeviceInfo());
        return catchResponse;
    }

    /**
     * Generates a CatchRequest object consisting of:
     * UhuServiceEndPoint object: {@link #DEVICE_3}
     * catcherSphereId: {@link #generateOwnerCombo()}
     * inviterShortCode: {@link #sphereUtils.getShareCode(catcherSphereId)}
     * UhuDeviceInfo object: {@link #getUhuDeviceInfo()}
     * sphereExchangeData: {@link #getExchangeData()}
     *
     * @return - CatchRequest object.
     */
    public final CatchRequest getCatchRequestObj() {
        /**create the catcher sphere**/
        String catcherSphereId = generateOwnerCombo();
        String inviterShortCode = sphereRegistryWrapper.getShareCode(catcherSphereId);
        String sphereExchangeData = getExchangeData();

        /**create the CatchRequest**/
        UhuServiceEndPoint sender = new UhuServiceEndPoint(OWNER_SERVICE_ID_3);
        sender.device = DEVICE_2.getDeviceName();
        CatchRequest catchRequest = new CatchRequest(sender, inviterShortCode, catcherSphereId, getUhuDeviceInfo(), sphereExchangeData);
        return catchRequest;
    }

    /**
     * Generates a ShareRequest object consisting of:
     * UhuServiceEndPoint object: {@link #DEVICE_3}
     * sharerSphereId: {@link #generateOwnerCombo()}
     * shortCode: {@link #UhuId().getShortIdByHash(sharerSphereId)}
     * UhuDeviceInfo object: {@link #getUhuDeviceInfo()}
     *
     * @return - ShareRequest object.
     */
    public final ShareRequest getShareRequestObj() {
        String sharerSphereId = generateOwnerCombo();
        String shortCode = new UhuId().getShortIdByHash(sharerSphereId);
        UhuServiceEndPoint sender = new UhuServiceEndPoint(OWNER_SERVICE_ID_3);
        /**create the ShareRequest**/
        ShareRequest shareRequest = new ShareRequest(shortCode, getUhuDeviceInfo(), sender, sharerSphereId);
        return shareRequest;
    }

    /**
     * Generates a ShareResponse object consisting of:
     * UhuServiceEndPoint sender object: {@link UhuNetworkUtilities.getServiceEndPoint(null)}
     * UhuServiceEndPoint recipient object: {@link #DEVICE_3}
     * uniqueKey: null
     * sharerSphereId: {@link #generateOwnerCombo()}
     * shortCode: {@link #UhuId().getShortIdByHash(sharerSphereId)}
     * UhuDeviceInfo object: {@link #getUhuDeviceInfo()}
     * sphereExchangeData: {@link #getExchangeData()}
     *
     * @return - ShareRequest object.
     */
    public final ShareResponse getShareResponseObj() {
        String sharerSphereId = generateOwnerCombo();
        String shortCode = new UhuId().getShortIdByHash(sharerSphereId);
        UhuServiceEndPoint sender = UhuNetworkUtilities.getServiceEndPoint(null);
        UhuServiceEndPoint recipient = new UhuServiceEndPoint(OWNER_SERVICE_ID_3);
        recipient.device = DEVICE_2.getDeviceName();
        String sphereExchangeData = getExchangeData();
        /**create the ShareRequest**/
        ShareResponse shareResponse = new ShareResponse(sender, recipient, "abcdefg", shortCode,
                getUhuDeviceInfo(), sphereExchangeData, sharerSphereId);
        return shareResponse;
    }

    public enum Type {
        OWNER, MEMBER
    }

}
