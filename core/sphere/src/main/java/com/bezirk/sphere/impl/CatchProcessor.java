package com.bezirk.sphere.impl;

import com.bezirk.device.Device;
import com.bezirk.middleware.objects.BezirkDeviceInfo;
import com.bezirk.middleware.objects.BezirkZirkInfo;
import com.bezirk.networking.NetworkManager;
import com.bezirk.proxy.api.impl.ZirkId;
import com.bezirk.sphere.api.CryptoInternals;
import com.bezirk.sphere.api.SphereListener;
import com.bezirk.sphere.messages.CatchRequest;
import com.bezirk.sphere.messages.CatchResponse;
import com.bezirk.sphere.security.SphereKeys;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author rishabh
 */
public class CatchProcessor {
    private static final Logger logger = LoggerFactory.getLogger(CatchProcessor.class);

    private static final String CATCH_FAILURE_MSG = "Catch Failed";
    private final SphereRegistryWrapper sphereRegistryWrapper;
    private final CryptoInternals crypto;
    private final Device device;
    private final CommsUtility comms;
    private final NetworkManager networkManager;

    public CatchProcessor(CryptoInternals crypto, Device device,
                          CommsUtility comms, SphereRegistryWrapper sphereRegistryWrapper, NetworkManager networkManager) {
        this.crypto = crypto;
        this.device = device;
        this.comms = comms;
        this.sphereRegistryWrapper = sphereRegistryWrapper;
        this.networkManager = networkManager;
    }

    public boolean processShortCode(String shortCode) {
        return processCatchCode(shortCode, sphereRegistryWrapper.getDefaultSphereId());
    }

    /**
     * Process the catch qr code/short-code request
     *
     * @param catchCode can be in the form of qr-code or a 7-digit[may change later]
     *                  code
     * @param sphereId
     * @return
     */
    public boolean processCatchCode(String catchCode, String sphereId) {
        // sphereId in which the services need to be caught
        String catcherSphereId;

        /************************************************************************
         * Step1: validate the catcher sphereId passed and set it.
         ************************************************************************/

        if (sphereRegistryWrapper.containsSphere(sphereId)) {
            catcherSphereId = sphereId;
            logger.info("Catch Process, Step1: catcherSphereId validation complete");
        } else {
            logger.error("Catch Process, Step1: catcherSphereId validation failed");
            return false;
        }

        /************************************************************************
         * Step2: validate request: short code/QR code with version and embedded
         * short code and set the code
         ************************************************************************/

        String inviterShortCode = validateCodeString(catchCode);
        if (inviterShortCode != null) {
            logger.info("Catch Process, Step2: shortCode validation complete");
        } else {
            logger.error("Catch Process, Step2: shortCode validation failed");
            return false;
        }

        /************************************************************************
         * Step3: handle crypto aspects, i.e. generating the sphereKey and
         * setting them.
         ************************************************************************/

        if (addCatchCode(inviterShortCode)) {
            logger.info("Catch Process, Step3: crypto procedure complete");
        } else {
            logger.error("Catch Process, Step3: crypto procedure failed");
            return false;
        }

        /************************************************************************
         * Step4: prepare catchRequest to be sent to the device being caught for
         * services[the one providing the catch code]
         ************************************************************************/

        CatchRequest catchRequest = prepareRequest(catcherSphereId, inviterShortCode);
        if (catchRequest != null) {
            logger.info("Catch Process, Step4: catch request preparation complete");
        } else {
            logger.error("Catch Process, Step4: catch request preparation failed");
            return false;
        }

        /************************************************************************
         * Step5: send the request
         ************************************************************************/

        if (comms.sendMessage(catchRequest)) {
            logger.info("Catch Process, Step5: catch request sending complete");
        } else {
            logger.error("Catch Process, Step5: catch request sending failed");
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
        if (catchResponse.getInviterSphereDeviceInfo().getDeviceId().equals(device.getDeviceId())) {
            logger.debug("Found response initiated by same device, dropping SphereCatchRequest");
            return false;
        }

        // Check if the catch request is initiated by the current device
        if (!catcherDeviceId.equals(device.getDeviceId())) {
            logger.debug("Catch request not initiated by the current device");
            return false;
        }

        // Catching : add to the services from sharing place
        if (sphereRegistryWrapper.containsSphere(catcherSphereId)) {

            BezirkDeviceInfo inviterBezirkDeviceInfo = catchResponse.getInviterSphereDeviceInfo();

            // get device information
            sphereRegistryWrapper.addDevice(inviterBezirkDeviceInfo.getDeviceId(),
                    new DeviceInformation(inviterBezirkDeviceInfo.getDeviceName(), inviterBezirkDeviceInfo.getDeviceType()));

            // add the zirk name and zirk id to sphereMembership map
            if (sphereRegistryWrapper.addMemberServices(inviterBezirkDeviceInfo, catcherSphereId,
                    inviterBezirkDeviceInfo.getDeviceId())) {
                for (BezirkZirkInfo service : inviterBezirkDeviceInfo.getZirkList()) {
                    Sphere sphere = sphereRegistryWrapper.getSphere(catcherSphereId);
                    sphere.addService(inviterBezirkDeviceInfo.getDeviceId(), service.getZirkId());
                }
                // catch response for the request is success
                sphereRegistryWrapper.updateListener(SphereRegistryWrapper.Operation.CATCH, SphereListener.Status.SUCCESS, "Catch successful");
                logger.info("Got Catch response. catch process completed");
                return true;
            }
        }

        sphereRegistryWrapper.updateListener(SphereRegistryWrapper.Operation.CATCH, SphereListener.Status.FAILURE, CATCH_FAILURE_MSG);
        return false;
    }

    /**
     * Process catch request received from the catcher device
     *
     * @param catchRequest
     * @return
     */
    public boolean processRequest(CatchRequest catchRequest) {
        /************************************************************************
         * Step1: validate the request
         ************************************************************************/

        if (validateRequest(catchRequest)) {
            logger.info("Catch Request Processing, Step1: request validation complete");
        } else {
            logger.error("Catch Request Processing, Step1: request validation failed");
            return false;
        }

        SphereExchangeData sphereExchangeData = SphereExchangeData.deserialize(catchRequest.getSphereExchangeData());
        BezirkDeviceInfo catcherBezirkDeviceInfo = catchRequest.getBezirkDeviceInfo();
        String inviterShortCode = catchRequest.getSphereId();
        String catcherSphereId = sphereExchangeData.getSphereID();

        /************************************************************************
         * Step2: store sphere exchange data
         ************************************************************************/

        if (storeData(sphereExchangeData)) {
            logger.info("Catch Request Processing, Step2: storing sphere exchange data complete");
        } else {
            sphereRegistryWrapper.updateListener(SphereRegistryWrapper.Operation.CATCH, SphereListener.Status.FAILURE, CATCH_FAILURE_MSG);
            logger.error("Catch Request Processing, Step2: storing sphere exchange data failed");
            return false;
        }

        /************************************************************************
         * Step3: extract services from the sphere having the shortCode passed &
         * add them to the catcher sphere & create response
         ************************************************************************/

        CatchResponse sphereCatchResponse = prepareResponse(sphereExchangeData, catcherBezirkDeviceInfo, inviterShortCode,
                catcherSphereId);
        if (sphereCatchResponse != null) {
            logger.info("Catch Request Processing, Step3: preparing catch response complete");
        } else {
            sphereRegistryWrapper.updateListener(SphereRegistryWrapper.Operation.CATCH, SphereListener.Status.FAILURE, CATCH_FAILURE_MSG);
            logger.error("Catch Request Processing, Step3: preparing catch response failed");
            return false;
        }

        /************************************************************************
         * Step4: send the catch response
         ************************************************************************/

        if (comms.sendMessage(sphereCatchResponse)) {
            logger.info("Catch Request Processing, Step4: catch response sending complete");
        } else {
            sphereRegistryWrapper.updateListener(SphereRegistryWrapper.Operation.CATCH, SphereListener.Status.FAILURE, CATCH_FAILURE_MSG);
            logger.error("Catch Request Processing, Step4: catch response sending failed");
            return false;
        }

        sphereRegistryWrapper.updateListener(SphereRegistryWrapper.Operation.CATCH, SphereListener.Status.SUCCESS, "Catch successful");
        return true;
    }

    private boolean storeData(SphereExchangeData sphereExchangeData) {
        // add device information
        sphereRegistryWrapper.addDevice(sphereExchangeData.getDeviceID(),
                new DeviceInformation(sphereExchangeData.getDeviceName(), sphereExchangeData.getDeviceType()));

        // add sphere
        HashSet<String> ownerDevices = new HashSet<>();
        ownerDevices.add(sphereExchangeData.getDeviceID());

        Sphere sphere = new MemberSphere(sphereExchangeData.getSphereName(), sphereExchangeData.getSphereType(),
                ownerDevices, new LinkedHashMap<String, ArrayList<ZirkId>>(), false);

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
                logger.error("store data > unable to generated key");
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
         * QRCodeData.fromJson(inviterCatchCode); return
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

            // store the shortId as sphere id
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
     * @param catcherSphereId  - has to be non-null. null check done in {@link #processCatchCode(String, String)}
     * @param inviterShortCode - has to be non-null. null check done in {@link #processCatchCode(String, String)}
     * @return - CatchRequest object if catcherSphereId and inviterShortCode are valid.
     */
    private CatchRequest prepareRequest(String catcherSphereId, String inviterShortCode) {
        String sphereExchangeData = sphereRegistryWrapper.getShareCodeString(catcherSphereId);
        if (sphereExchangeData == null) {
            logger.error("Catch request not prepared for " + catcherSphereId);
            return null;
        }

        // TODO: What if there are no services in the device performing the
        // catch? Fix it.
        // get the catch sphere services
        // sphere sphere = registry.spheres.get(catchSphereId);
        Sphere sphere = sphereRegistryWrapper.getSphere(catcherSphereId);
        Map<String, ArrayList<ZirkId>> deviceServices = sphere.deviceServices;

        if (deviceServices != null && !deviceServices.isEmpty()
                && deviceServices.containsKey(device.getDeviceId())) {

            // get device services of sphere
            ArrayList<ZirkId> services = deviceServices.get(device.getDeviceId());

            if (services != null && !services.isEmpty()) {
                DeviceInformation deviceInformation = sphereRegistryWrapper
                        .getDeviceInformation(device.getDeviceId());

                BezirkDeviceInfo catcherBezirkDeviceInfo = new BezirkDeviceInfo(device.getDeviceId(),
                        deviceInformation.getDeviceName(), deviceInformation.getDeviceType(), null, false,
                        (List<BezirkZirkInfo>) sphereRegistryWrapper.getBezirkServiceInfo(services));

                return new CatchRequest(networkManager.getServiceEndPoint(null), inviterShortCode,
                        catcherSphereId, catcherBezirkDeviceInfo, sphereExchangeData);
            }
        }
        return null;
    }

    /**
     * Validate the Catch request: shortCode, sphereExchangeData, bezirkDeviceInfo
     *
     * @param catchRequest
     * @return
     */
    private boolean validateRequest(CatchRequest catchRequest) {
        if (catchRequest == null) {
            logger.error("sphere catch request received is null");
            return false;
        }

        String sphereExchangeDataString = catchRequest.getSphereExchangeData();
        String inviterShortCode = catchRequest.getSphereId();

        if ((sphereExchangeDataString == null) || (inviterShortCode == null)
                || (!sphereRegistryWrapper.existsSphereIdInKeyMaps(inviterShortCode))) {
            logger.error("Invalid sphere information");
            return false;
        }

        SphereExchangeData sphereExchangeData = SphereExchangeData.deserialize(sphereExchangeDataString);
        BezirkDeviceInfo catcherBezirkDeviceInfo = catchRequest.getBezirkDeviceInfo();

        if (catcherBezirkDeviceInfo == null || sphereExchangeData == null) {
            logger.error("Caught device/sphere Exchange data is not valid");
            return false;
        }

        // sender device is equal to current device ignore the results
        if (catcherBezirkDeviceInfo.getDeviceId().equals(device.getDeviceId())) {
            logger.debug("Found request initiated by same device, dropping SphereCatchRequest");
            return false;
        }
        return true;
    }

    /**
     * Generate the catch response to be sent.
     *
     * @param sphereExchangeData      - has to be non-null
     * @param catcherBezirkDeviceInfo - has to be non-null
     * @param inviterShortCode        - has to be non-null
     * @param -                       catcherSphereId
     * @return - CatchResponse object if all parameters are valid.
     * <br>- Exception if sphereExchangeData or catcherBezirkDeviceInfo are null
     * <br>- null if catcherSphereId is null.
     */

    private CatchResponse prepareResponse(SphereExchangeData sphereExchangeData, BezirkDeviceInfo catcherBezirkDeviceInfo,
                                          String inviterShortCode, String catcherSphereId) {

        Sphere catchCodeGeneratorSphere = sphereRegistryWrapper
                .getSphere(sphereRegistryWrapper.getSphereIdFromPasscode(inviterShortCode));

        String catcherDeviceId = catcherBezirkDeviceInfo.getDeviceId();

        /**
         * Important note and open point : If we have a owner sphere S [owned by
         * the current device] with local services S1 and S2 and external
         * services S3 and S4[in other devices], when we get S to this function,
         * we currently ensure that only S1 and S2 are added since this device
         * only owns these 2 services
         */

        // retrieve the services from the catchCodeGeneratorSphere
        Map<String, ArrayList<ZirkId>> deviceServices = catchCodeGeneratorSphere.deviceServices;

        if (deviceServices != null && !deviceServices.isEmpty()
                && deviceServices.containsKey(device.getDeviceId())) {

            ArrayList<ZirkId> services = deviceServices.get(device.getDeviceId());

            if (services == null || services.isEmpty()) {
                logger.error("No services are at the device, nothing to catch");
                return null;
            }

            // add the local services to catchSphereId
            if (sphereRegistryWrapper.addLocalServicesToSphere(services, catcherSphereId)) {
                logger.debug("Services from sphere with shortCode : " + inviterShortCode + " added to catch sphere id"
                        + catcherSphereId);

                // add remote device services also to new catchSphereId
                // add the caught zirk name and zirk id to
                // sphereMembership map
                if (sphereRegistryWrapper.addMemberServices(catcherBezirkDeviceInfo, catcherSphereId, catcherDeviceId)) {
                    // get all the device info of caught zirk
                    for (BezirkZirkInfo serviceInfoList : catcherBezirkDeviceInfo.getZirkList()) {

                        // TODO: Check this implementation, addMemberServices
                        // adds the data to the sphere
                        Sphere sphere = sphereRegistryWrapper.getSphere(catcherSphereId);
                        sphere.setSphereType(sphereExchangeData.getSphereType());
                        sphere.addService(catcherDeviceId, serviceInfoList.getZirkId());
                    }

                    // send local services of temp sharing sphere Id to catch
                    // sphere via
                    DeviceInformation deviceInformation = sphereRegistryWrapper
                            .getDeviceInformation(device.getDeviceId());

                    // create the device info with only local services
                    BezirkDeviceInfo inviterSphereDeviceInfo = new BezirkDeviceInfo(device.getDeviceId(),
                            deviceInformation.getDeviceName(), deviceInformation.getDeviceType(), null, false,
                            (List<BezirkZirkInfo>) sphereRegistryWrapper.getBezirkServiceInfo(services));

                    // FIXME: send unicast for device
                    CatchResponse response = new CatchResponse(networkManager.getServiceEndPoint(null),
                            catcherSphereId, catcherDeviceId, inviterSphereDeviceInfo);

                    logger.debug("Response prepared");
                    return response;
                } else {
                    logger.error("Response creation failed at add member services");
                }
            } else {
                logger.error("Response creation failed at add services to sphere");
            }
        }
        logger.error("Response creation failed. clear data base manually ");
        return null;
    }

}
