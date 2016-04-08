package com.bosch.upa.uhu.sphere.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bosch.upa.devices.UPADeviceInterface;
import com.bosch.upa.uhu.api.objects.UhuDeviceInfo;
import com.bosch.upa.uhu.api.objects.UhuServiceInfo;
import com.bosch.upa.uhu.comms.IUhuComms;
import com.bosch.upa.uhu.control.messages.ControlLedger;
import com.bosch.upa.uhu.network.UhuNetworkUtilities;
import com.bosch.upa.uhu.proxy.api.impl.UhuServiceId;
import com.bosch.upa.uhu.sphere.api.ICryptoInternals;
import com.bosch.upa.uhu.sphere.api.IUhuSphereListener.Status;
import com.bosch.upa.uhu.sphere.impl.SphereRegistryWrapper.Operation;
import com.bosch.upa.uhu.sphere.messages.CatchRequest;
import com.bosch.upa.uhu.sphere.messages.CatchResponse;
import com.bosch.upa.uhu.sphere.security.SphereKeys;

/**
 * @author rishabh
 *
 */
public class CatchProcessor { 

    private static final Logger LOGGER = LoggerFactory.getLogger(CatchProcessor.class);
    private SphereRegistryWrapper sphereRegistryWrapper;
    private ICryptoInternals crypto;
    private UPADeviceInterface upaDeviceInterface;
    private CommsUtility comms;
    private static final String CATCH_FAILURE_MSG = "Catch Failed";

    public CatchProcessor(ICryptoInternals crypto, UPADeviceInterface upaDeviceInterface,
            CommsUtility comms, SphereRegistryWrapper sphereRegistryWrapper) {
        this.crypto = crypto;
        this.upaDeviceInterface = upaDeviceInterface;
        this.comms = comms;
        this.sphereRegistryWrapper = sphereRegistryWrapper;
    }

    public boolean processShortCode(String shortCode) {
        return processCatchCode(shortCode, sphereRegistryWrapper.getDefaultSphereId());
    }

    /**
     * Process the catch qr code/short-code request
     * 
     * @param catchCode
     *            can be in the form of qr-code or a 7-digit[may change later]
     *            code
     * @param sphereId
     * @return
     */
    public boolean processCatchCode(String catchCode, String sphereId) {

        String inviterShortCode = null;
        // sphereId in which the services need to be caught
        String catcherSphereId = null;
        CatchRequest catchRequest = null;

        /************************************************************************
         * Step1: validate the catcher sphereId passed and set it.
         ************************************************************************/

        if (sphereRegistryWrapper.containsSphere(sphereId)) {
            catcherSphereId = sphereId;
            LOGGER.info("Catch Process, Step1: catcherSphereId validation complete");
        } else {
            LOGGER.error("Catch Process, Step1: catcherSphereId validation failed");
            return false;
        }

        /************************************************************************
         * Step2: validate request: short code/QR code with version and embedded
         * short code and set the code
         ************************************************************************/

        inviterShortCode = validateCodeString(catchCode);
        if (inviterShortCode != null) {
            LOGGER.info("Catch Process, Step2: shortCode validation complete");
        } else {
            LOGGER.error("Catch Process, Step2: shortCode validation failed");
            return false;
        }

        /************************************************************************
         * Step3: handle crypto aspects, i.e. generating the sphereKey and
         * setting them.
         ************************************************************************/

        if (addCatchCode(inviterShortCode)) {
            LOGGER.info("Catch Process, Step3: crypto procedure complete");
        } else {
            LOGGER.error("Catch Process, Step3: crypto procedure failed");
            return false;
        }

        /************************************************************************
         * Step4: prepare catchRequest to be sent to the device being caught for
         * services[the one providing the catch code]
         ************************************************************************/

        catchRequest = prepareRequest(catcherSphereId, inviterShortCode);
        if (catchRequest != null) {
            LOGGER.info("Catch Process, Step4: catch request preparation complete");
        } else {
            LOGGER.error("Catch Process, Step4: catch request preparation failed");
            return false;
        }

        /************************************************************************
         * Step5: send the request
         ************************************************************************/

        if (comms.sendMessage(catchRequest)) {
            LOGGER.info("Catch Process, Step5: catch request sending complete");
        } else {
            LOGGER.error("Catch Process, Step5: catch request sending failed");
            return false;
        }

        return true;
    }

    /**
     * Process the catch response received from sphere/device being caught
     * 
     * @param catchResponse
     * @return
     */
    public boolean processResponse(CatchResponse catchResponse) {
        if (catchResponse == null) {
            return false;
        }
        String catcherDeviceId = catchResponse.getCatcherDeviceId();

        // catch sphere id which initiated the catching process
        String catcherSphereId = catchResponse.getSphereId();

        // ignore loop back message
        // remove this check[not required, once the response is made
        // unicast]
        if (catchResponse.getInviterSphereDeviceInfo().getDeviceId().equals(upaDeviceInterface.getDeviceId())) {
            LOGGER.debug("Found response initiated by same device, dropping SphereCatchRequest");
            return false;
        }

        // Check if the catch request is initiated by the current device
        if (!catcherDeviceId.equals(upaDeviceInterface.getDeviceId())) {
            LOGGER.debug("Catch request not initiated by the current device");
            return false;
        }

        // Catching : add to the services from sharing place
        if (sphereRegistryWrapper.containsSphere(catcherSphereId)) {

            UhuDeviceInfo inviterUhuDeviceInfo = catchResponse.getInviterSphereDeviceInfo();

            // get device information
            sphereRegistryWrapper.addDevice(inviterUhuDeviceInfo.getDeviceId(),
                    new DeviceInformation(inviterUhuDeviceInfo.getDeviceName(), inviterUhuDeviceInfo.getDeviceType()));

            // add the service name and service id to spheremembership map
            if (sphereRegistryWrapper.addMemberServices(inviterUhuDeviceInfo, catcherSphereId,
                    inviterUhuDeviceInfo.getDeviceId())) {
                for (UhuServiceInfo service : inviterUhuDeviceInfo.getServiceList()) {
                    Sphere sphere = sphereRegistryWrapper.getSphere(catcherSphereId);
                    sphere.addService(inviterUhuDeviceInfo.getDeviceId(), service.getServiceId());
                }
                // catch response for the request is success
                sphereRegistryWrapper.updateListener(Operation.CATCH, Status.SUCCESS, "Catch successful");
                LOGGER.info("Got Catch response. catch process completed");
                return true;
            }
        }

        sphereRegistryWrapper.updateListener(Operation.CATCH, Status.FAILURE, CATCH_FAILURE_MSG);
        return false;
    }

    /**
     * Process catch request received from the catcher device
     * 
     * @param catchRequest
     * @return
     */
    public boolean processRequest(CatchRequest catchRequest) {

        SphereExchangeData sphereExchangeData = null;
        UhuDeviceInfo catcherUhuDeviceInfo = null;
        String inviterShortCode = null;
        String catcherSphereId = null;
        CatchResponse sphereCatchResponse = null;

        /************************************************************************
         * Step1: validate the request
         ************************************************************************/

        if (validateRequest(catchRequest)) {
            LOGGER.info("Catch Request Processing, Step1: request validation complete");
        } else {
            LOGGER.error("Catch Request Processing, Step1: request validation failed");
            return false;
        }

        sphereExchangeData = SphereExchangeData.deserialize(catchRequest.getSphereExchangeData());
        catcherUhuDeviceInfo = catchRequest.getUhuDeviceInfo();
        inviterShortCode = catchRequest.getSphereId();
        catcherSphereId = sphereExchangeData.getSphereID();

        /************************************************************************
         * Step2: store sphere exchange data
         ************************************************************************/

        if (storeData(sphereExchangeData)) {
            LOGGER.info("Catch Request Processing, Step2: storing sphere exchange data complete");
        } else {
            sphereRegistryWrapper.updateListener(Operation.CATCH, Status.FAILURE, CATCH_FAILURE_MSG);
            LOGGER.error("Catch Request Processing, Step2: storing sphere exchange data failed");
            return false;
        }

        /************************************************************************
         * Step3: extract services from the sphere having the shortCode passed &
         * add them to the catcher sphere & create response
         ************************************************************************/

        sphereCatchResponse = prepareResponse(sphereExchangeData, catcherUhuDeviceInfo, inviterShortCode,
                catcherSphereId);
        if (sphereCatchResponse != null) {
            LOGGER.info("Catch Request Processing, Step3: preparing catch response complete");
        } else {
            sphereRegistryWrapper.updateListener(Operation.CATCH, Status.FAILURE, CATCH_FAILURE_MSG);
            LOGGER.error("Catch Request Processing, Step3: preparing catch response failed");
            return false;
        }

        /************************************************************************
         * Step4: send the catch response
         ************************************************************************/

        if (comms.sendMessage(sphereCatchResponse)) {
            LOGGER.info("Catch Request Processing, Step4: catch response sending complete");
        } else {
            sphereRegistryWrapper.updateListener(Operation.CATCH, Status.FAILURE, CATCH_FAILURE_MSG);
            LOGGER.error("Catch Request Processing, Step4: catch response sending failed");
            return false;
        }

        sphereRegistryWrapper.updateListener(Operation.CATCH, Status.SUCCESS, "Catch successful");
        return true;
    }

    /**
     * Store the data
     * 
     * @param sphereExchangeData - has to be non-null
     * @return
     */
    private boolean storeData(SphereExchangeData sphereExchangeData) {

        // add device information
        sphereRegistryWrapper.addDevice(sphereExchangeData.getDeviceID(),
                new DeviceInformation(sphereExchangeData.getDeviceName(), sphereExchangeData.getDeviceType()));

        // add sphere
        HashSet<String> ownerDevices = new HashSet<String>();
        ownerDevices.add(sphereExchangeData.getDeviceID());

        Sphere sphere = new MemberSphere(sphereExchangeData.getSphereName(), sphereExchangeData.getSphereType(),
                ownerDevices, new LinkedHashMap<String, ArrayList<UhuServiceId>>(), false);

        sphereRegistryWrapper.addSphere(sphereExchangeData.getSphereID(), sphere);

        if (sphereExchangeData.isKeysExist()) {
            // add keys
            SphereKeys sphereKeys = new SphereKeys(sphereExchangeData.getSphereKey(),
                    sphereExchangeData.getOwnerPublicKeyBytes());
            crypto.addMemberKeys(sphereExchangeData.getSphereID(), sphereKeys);
        } else {
            // sphere key doesn't exist create one
            // TODO: Check this
            if (!crypto.generateKeys(sphereExchangeData.getSphereID(), true)) {
                LOGGER.error("store data > unable to generated key");
                return false;
            }
            sphereRegistryWrapper.persist();
        }

        return true;
    }

    /**
     * Validates the qrcode string/short code
     * 
     * @param inviterCatchCode
     * @return
     */
    private String validateCodeString(String inviterCatchCode) {
        /**
         * Improvements/Additions <br>
         * if (QRCodeData.checkVersionTag(inviterCatchCode) &&
         * QRCodeData.checkCompatibility(inviterCatchCode)) <br>
         * { // retrieve the catchCode QRCodeData catchData =
         * QRCodeData.deserialize(inviterCatchCode); return
         * catchData.getCatchCode(); }
         */
        if (inviterCatchCode.length() == 7) {
            // TODO: get the test value(7) or validation from another source
            return inviterCatchCode;
        }
        return null;
    }

    /**
     * Add keys for the short code
     * 
     * @param inviterShortCode
     * @return
     */
    private boolean addCatchCode(String inviterShortCode) {
        // create key from the short id
        byte[] sphereKey = crypto.generateKey(inviterShortCode);

        // add key to crypto engine
        if (sphereKey != null) {
            SphereKeys sphereKeys = new SphereKeys(sphereKey, null, null);

            // TODO: Improve persistence knowledge from registry

            // store the shortid as sphere id
            crypto.addMemberKeys(inviterShortCode, sphereKeys);
            // don't persist or remove it later
            return true;
        }
        return false;
    }

    /**
     * Generate the catch request to be sent to the device whose services need
     * to be caught
     * 
     * @param catcherSphereId - has to be non-null. null check done in {@link #processCatchCode(String, String)}
     * @param inviterShortCode - has to be non-null. null check done in {@link #processCatchCode(String, String)}
     * @return - CatchRequest object if catcherSphereId and inviterShortCode are valid.
     */
    private CatchRequest prepareRequest(String catcherSphereId, String inviterShortCode) {
        CatchRequest sphereCatchRequest = null;
        String sphereExchangeData = sphereRegistryWrapper.getShareCodeString(catcherSphereId);
        if (sphereExchangeData == null) {
            LOGGER.error("Catch request not prepared for " + catcherSphereId);
            return sphereCatchRequest;
        }

        // TODO: What if there are no services in the device performing the
        // catch? Fix it.
        // get the catch sphere services
        // Sphere sphere = registry.spheres.get(catchSphereId);
        Sphere sphere = sphereRegistryWrapper.getSphere(catcherSphereId);
        Map<String, ArrayList<UhuServiceId>> deviceServices = sphere.deviceServices;

        if (deviceServices != null && !deviceServices.isEmpty()
                && deviceServices.containsKey(upaDeviceInterface.getDeviceId())) {

            // get device services of sphere
            ArrayList<UhuServiceId> services = deviceServices.get(upaDeviceInterface.getDeviceId());

            if (services != null && !services.isEmpty()) {
                DeviceInformation deviceInformation = sphereRegistryWrapper
                        .getDeviceInformation(upaDeviceInterface.getDeviceId());

                UhuDeviceInfo catcherUhuDeviceInfo = new UhuDeviceInfo(upaDeviceInterface.getDeviceId(),
                        deviceInformation.getDeviceName(), deviceInformation.getDeviceType(), null, false,
                        (List<UhuServiceInfo>) sphereRegistryWrapper.getUhuServiceInfo(services));

                sphereCatchRequest = new CatchRequest(UhuNetworkUtilities.getServiceEndPoint(null), inviterShortCode,
                        catcherSphereId, catcherUhuDeviceInfo, sphereExchangeData);

                return sphereCatchRequest;
            }
        }
        return sphereCatchRequest;
    }

    /**
     * Validate the Catch request: shortCode, sphereExchangeData, uhuDeviceInfo
     * 
     * @param catchRequest
     * @return
     */
    private boolean validateRequest(CatchRequest catchRequest) {
        if (catchRequest == null) {
            LOGGER.error("Sphere catch request received is null");
            return false;
        }

        String sphereExchangeDataString = catchRequest.getSphereExchangeData();
        String inviterShortCode = catchRequest.getSphereId();

        if ((sphereExchangeDataString == null) || (inviterShortCode == null)
                || (!sphereRegistryWrapper.existsSphereIdInKeyMaps(inviterShortCode))) {
            LOGGER.error("Invalid sphere information");
            return false;
        }

        SphereExchangeData sphereExchangeData = SphereExchangeData.deserialize(sphereExchangeDataString);
        UhuDeviceInfo catcherUhuDeviceInfo = catchRequest.getUhuDeviceInfo();

        if (catcherUhuDeviceInfo == null || sphereExchangeData == null) {
            LOGGER.error("Catched device/Sphere Exchange data is not valid");
            return false;
        }

        // sender device is equal to current device ignore the results
        if (catcherUhuDeviceInfo.getDeviceId().equals(upaDeviceInterface.getDeviceId())) {
            LOGGER.debug("Found request initiated by same device, dropping SphereCatchRequest");
            return false;
        }
        return true;
    }
    
    /**
     * Generate the catch response to be sent.
     * 
     * @param sphereExchangeData - has to be non-null
     * @param catcherUhuDeviceInfo - has to be non-null
     * @param inviterShortCode - has to be non-null
     * @param - catcherSphereId
     * @return - CatchResponse object if all parameters are valid.
     *           <br>- Exception if sphereExchangeData or catcherUhuDeviceInfo are null
     *           <br>- null if catcherSphereId is null.
     */

    private CatchResponse prepareResponse(SphereExchangeData sphereExchangeData, UhuDeviceInfo catcherUhuDeviceInfo,
            String inviterShortCode, String catcherSphereId) {

        Sphere catchCodeGeneratorSphere = sphereRegistryWrapper
                .getSphere(sphereRegistryWrapper.getSphereIdFromPasscode(inviterShortCode));

        String catcherDeviceId = catcherUhuDeviceInfo.getDeviceId();

        /**
         * Important note and open point : If we have a owner sphere S [owned by
         * the current device] with local services S1 and S2 and external
         * services S3 and S4[in other devices], when we get S to this function,
         * we currently ensure that only S1 and S2 are added since this device
         * only owns these 2 services
         */

        // retrieve the services from the catchCodeGeneratorSphere
        Map<String, ArrayList<UhuServiceId>> deviceServices = catchCodeGeneratorSphere.deviceServices;

        if (deviceServices != null && !deviceServices.isEmpty()
                && deviceServices.containsKey(upaDeviceInterface.getDeviceId())) {

            ArrayList<UhuServiceId> services = deviceServices.get(upaDeviceInterface.getDeviceId());

            if (services == null || services.isEmpty()) {
                LOGGER.error("No services are at the device, nothing to catch");
                return null;
            }

            // add the local services to catchSphereId
            if (sphereRegistryWrapper.addLocalServicesToSphere(services, catcherSphereId)) {
                LOGGER.debug("Services from sphere with shortCode : " + inviterShortCode + " added to catch sphere id"
                        + catcherSphereId);

                // add remote device services also to new catchSphereId
                // add the catched service name and service id to
                // spheremembership map
                if (sphereRegistryWrapper.addMemberServices(catcherUhuDeviceInfo, catcherSphereId, catcherDeviceId)) {
                    // get all the device info of catched service
                    for (UhuServiceInfo serviceInfoList : catcherUhuDeviceInfo.getServiceList()) {

                        // TODO: Check this implementation, addMemberServices
                        // adds the data to the sphere
                        Sphere sphere = sphereRegistryWrapper.getSphere(catcherSphereId);
                        sphere.setSphereType(sphereExchangeData.getSphereType());
                        sphere.addService(catcherDeviceId, serviceInfoList.getServiceId());
                    }

                    // send local services of temp sharing Sphere Id to catch
                    // sphere via
                    DeviceInformation deviceInformation = sphereRegistryWrapper
                            .getDeviceInformation(upaDeviceInterface.getDeviceId());

                    // create the device info with only local services
                    UhuDeviceInfo inviterSphereDeviceInfo = new UhuDeviceInfo(upaDeviceInterface.getDeviceId(),
                            deviceInformation.getDeviceName(), deviceInformation.getDeviceType(), null, false,
                            (List<UhuServiceInfo>) sphereRegistryWrapper.getUhuServiceInfo(services));

                    // FIXME: send unicast for device
                    CatchResponse response = new CatchResponse(UhuNetworkUtilities.getServiceEndPoint(null),
                            catcherSphereId, catcherDeviceId, inviterSphereDeviceInfo);

                    LOGGER.debug("Response prepared");
                    return response;
                } else {
                    LOGGER.error("Response creation failed at add member services");
                }
            } else {
                LOGGER.error("Response creation failed at add services to sphere");
            }
        }
        LOGGER.error("Response creation failed. clear data base manuallys ");
        return null;
    }

}
