package com.bezirk.sphere.impl;

import com.bezirk.devices.DeviceInterface;
import com.bezirk.middleware.objects.BezirkDeviceInfo;
import com.bezirk.middleware.objects.BezirkZirkInfo;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.proxy.api.impl.ZirkId;
import com.bezirk.sphere.api.SphereListener;
import com.bezirk.sphere.api.CryptoInternals;
import com.bezirk.sphere.messages.ShareRequest;
import com.bezirk.sphere.messages.ShareResponse;
import com.bezirk.sphere.security.SphereKeys;
import com.bezrik.network.BezirkNetworkUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Rishabh Gulati
 */
public class ShareProcessor {
    private static final Logger logger = LoggerFactory.getLogger(ShareProcessor.class);
    private static final String SHARE_FAILURE_MSG = "Share Failed";
    private CryptoInternals crypto;
    private DeviceInterface deviceInterface;
    private CommsUtility comms;
    private SphereRegistryWrapper sphereRegistryWrapper;

    public ShareProcessor(CryptoInternals crypto, DeviceInterface deviceInterface, CommsUtility comms,
                          SphereRegistryWrapper sphereRegistryWrapper) {
        this.crypto = crypto;
        this.deviceInterface = deviceInterface;
        this.comms = comms;
        this.sphereRegistryWrapper = sphereRegistryWrapper;
    }

    /**
     * Process the share qr code/short-code request and send the request if the shareCode and
     * sphereId are correct.
     *
     * @param shareCode can be in the form of qr-code or a 7-digit[may change later]
     *                  code
     * @param sphereId  of the sphere whose services are to be shared.
     * @return True if request was sent. False otherwise.
     */
    public boolean processShareCode(String shareCode, String sphereId) {
        // sphereId from which the services need to be shared
        String sharerSphereId;

        /************************************************************************
         * Step1: validate the sharer sphereId passed and set it.
         ************************************************************************/

        if (sphereRegistryWrapper.containsSphere(sphereId)) {
            sharerSphereId = sphereId;
            logger.info("Share Process, Step1: sharerSphereId validation complete");
        } else {
            logger.error("Share Process, Step1: sharerSphereId validation failed");
            return false;
        }

        /************************************************************************
         * Step2: validate request: short code/QR code with version and embedded
         * short code and set the code
         ************************************************************************/

        final String inviterShortCode = validateCodeString(shareCode);
        if (inviterShortCode != null) {
            logger.info("Share Process, Step2: shortCode validation complete");
        } else {
            logger.error("Share Process, Step2: shortCode validation failed");
            return false;
        }

        /************************************************************************
         * Step3: handle crypto aspects, i.e. generating the sphereKey and
         * setting them.
         ************************************************************************/

        if (addCatchCode(inviterShortCode)) {
            logger.info("Share Process, Step3: crypto procedure complete");
        } else {
            logger.error("Share Process, Step3: crypto procedure failed");
            return false;
        }

        /************************************************************************
         * Step4: prepare shareRequest to be sent to the device sharing the
         * sphere
         ************************************************************************/

        final ShareRequest shareRequest = prepareRequest(sharerSphereId, inviterShortCode);
        if (shareRequest != null) {
            logger.info("Share Process, Step4: share request preparation complete");
        } else {
            logger.error("Share Process, Step4: share request preparation failed");
            return false;
        }

        /************************************************************************
         * Step5: send the request
         ************************************************************************/

        if (comms.sendMessage(shareRequest)) {
            logger.info("Share Process, Step5: share request sending complete");
        } else {
            logger.error("Share Process, Step5: share request sending failed");
            return false;
        }

        return true;
    }

    /**
     * Process share request received from the device sharing its services and send a response.
     *
     * @param shareRequest - ShareRequest object containing the information about
     *                     the sharer's device and their sphereID.
     * @return - True if response was sent successfully. False otherwise.
     */
    public boolean processRequest(ShareRequest shareRequest) {
        /************************************************************************
         * Step1: validate the request
         ************************************************************************/

        if (validateRequest(shareRequest)) {
            logger.info("Share Request Processing, Step1: request validation complete");
        } else {
            logger.error("Share Request Processing, Step1: request validation failed");
            return false;
        }

        final BezirkDeviceInfo sharerBezirkDeviceInfo = shareRequest.getBezirkDeviceInfo();
        final String inviterShortCode = shareRequest.getSphereId();
        final String inviterSphereId = sphereRegistryWrapper.getSphereIdFromPasscode(inviterShortCode);
        final String sharerSphereId = shareRequest.getSharerSphereId();

        // for sending unicast back to the device sharing its services
        String uniqueKey = shareRequest.getUniqueKey();
        BezirkZirkEndPoint recipient = shareRequest.getSender();

        /************************************************************************
         * Step2: store sphere exchange data
         ************************************************************************/

        if (storeData(inviterSphereId, sharerBezirkDeviceInfo)) {
            logger.info("Share Request Processing, Step2: storing sphere exchange data complete");
        } else {
            sphereRegistryWrapper.updateListener(SphereRegistryWrapper.Operation.SHARE, SphereListener.Status.FAILURE, SHARE_FAILURE_MSG);
            logger.error("Share Request Processing, Step2: storing sphere exchange data failed");
            return false;
        }

        /************************************************************************
         * Step3: extract services from the sphere with the passed shortCode &
         * create response
         ************************************************************************/

        final ShareResponse shareResponse = prepareResponse(inviterShortCode, inviterSphereId, recipient, uniqueKey, sharerSphereId);
        if (shareResponse != null) {
            logger.info("Share Request Processing, Step3: preparing share response complete");
        } else {
            sphereRegistryWrapper.updateListener(SphereRegistryWrapper.Operation.SHARE, SphereListener.Status.FAILURE, SHARE_FAILURE_MSG);
            logger.error("Share Request Processing, Step3: preparing share response failed");
            return false;
        }

        /************************************************************************
         * Step4: send the share response
         ************************************************************************/

        if (comms.sendMessage(shareResponse)) {
            logger.info("Share Request Processing, Step4: share response sending complete");
        } else {
            sphereRegistryWrapper.updateListener(SphereRegistryWrapper.Operation.SHARE, SphereListener.Status.FAILURE, SHARE_FAILURE_MSG);
            logger.error("Share Request Processing, Step4: share response sending failed");
            return false;
        }

        sphereRegistryWrapper.updateListener(SphereRegistryWrapper.Operation.SHARE, SphereListener.Status.SUCCESS, "Share successful");
        return true;
    }

    /**
     * Process the share response
     */
    public boolean processResponse(ShareResponse shareResponse) {

        /************************************************************************
         * Step1: validate the request
         ************************************************************************/

        if (validateResponse(shareResponse)) {
            logger.info("Share Response Processing, Step1: response validation complete");
        } else {
            logger.error("Share Response Processing, Step1: response validation failed");
            return false;
        }

        /************************************************************************
         * Step2: store sphere exchange data
         ************************************************************************/

        if (storeData(SphereExchangeData.deserialize(shareResponse.getSphereExchangeDataString()),
                shareResponse.getBezirkDeviceInfo(), shareResponse.getSharerSphereId())) {
            sphereRegistryWrapper.updateListener(SphereRegistryWrapper.Operation.SHARE, SphereListener.Status.SUCCESS, "Share successful");
            return true;
        } else {
            sphereRegistryWrapper.updateListener(SphereRegistryWrapper.Operation.SHARE, SphereListener.Status.FAILURE, SHARE_FAILURE_MSG);
            return false;
        }
    }

    /**
     * Store the inviter SphereID and sharer Device Info from the received share
     * request.
     *
     * @param inviterSphereId     - has to be valid and non-null
     * @param sharerBezirkDeviceInfo - has to be non-null
     * @return - True if data was added to the registry successfully, else,
     * False.
     */
    private boolean storeData(String inviterSphereId, BezirkDeviceInfo sharerBezirkDeviceInfo) {
        if (sphereRegistryWrapper.addDevice(sharerBezirkDeviceInfo.getDeviceId(),
                new DeviceInformation(sharerBezirkDeviceInfo.getDeviceName(), sharerBezirkDeviceInfo.getDeviceType()))
                && sphereRegistryWrapper.addMemberServices(sharerBezirkDeviceInfo, inviterSphereId,
                sharerBezirkDeviceInfo.getDeviceId())) {
            sphereRegistryWrapper.persist();
            return true;
        }
        return false;
    }

    /**
     * Store the data
     *
     * @param sphereExchangeData   - has to be non-null
     * @param inviterBezirkDeviceInfo - has to be non-null
     * @param sharerSphereId       - has to be non-null
     * @return - True if data was stored successfully in the registry, else
     * False. <br>
     * - NullPointerException is thrown if SphereExchangeData obj is
     * null.
     */
    private boolean storeData(SphereExchangeData sphereExchangeData, BezirkDeviceInfo inviterBezirkDeviceInfo,
                              String sharerSphereId) {

        logger.debug("sphere Exchange data:\n" + sphereExchangeData.toString());
        logger.debug("Bezirk Device Info:\n" + inviterBezirkDeviceInfo.toString());
        logger.debug("Sharer sphere Id: " + sharerSphereId);
        // add device information
        sphereRegistryWrapper.addDevice(sphereExchangeData.getDeviceID(),
                new DeviceInformation(sphereExchangeData.getDeviceName(), sphereExchangeData.getDeviceType()));

        // add sphere
        HashSet<String> ownerDevices = new HashSet<String>();
        ownerDevices.add(sphereExchangeData.getDeviceID());

        Sphere sphere = new MemberSphere(sphereExchangeData.getSphereName(), sphereExchangeData.getSphereType(),
                ownerDevices, new LinkedHashMap<String, ArrayList<ZirkId>>(), false);

        sphereRegistryWrapper.addSphere(sphereExchangeData.getSphereID(), sphere);

        // add received services
        sphereRegistryWrapper.addMemberServices(inviterBezirkDeviceInfo, sphereExchangeData.getSphereID(),
                sphereExchangeData.getDeviceID());

        // add services from the sharer sphere
        Sphere shareSphere = sphereRegistryWrapper.getSphere(sharerSphereId);
        Map<String, ArrayList<ZirkId>> deviceServices = shareSphere.deviceServices;
        if (deviceServices != null && !deviceServices.isEmpty()
                && deviceServices.containsKey(deviceInterface.getDeviceId())) {

            ArrayList<ZirkId> services = deviceServices.get(deviceInterface.getDeviceId());
            sphereRegistryWrapper.addLocalServicesToSphere(services, sphereExchangeData.getSphereID());
        }

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
     * @param inviterShareCode
     * @return
     */
    private String validateCodeString(String inviterShareCode) {
        /**
         * Improvements/Additions <br>
         * if (QRCodeData.checkVersionTag(inviterShareCode) &&
         * QRCodeData.checkCompatibility(inviterShareCode)) <br>
         * { // retrieve the catchCode QRCodeData catchData =
         * QRCodeData.fromJson(inviterShareCode); return
         * catchData.getCatchCode(); }
         */
        if (inviterShareCode.length() == 7) {
            // TODO: get the test value(7) or validation from another source
            return inviterShareCode;
        }
        return null;
    }

    /**
     * Add keys for the short code
     *
     * @param inviterShortCode - has to be non-null
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
     * Generate the share request to be sent to the device sharing its sphere
     *
     * @param sharerSphereId
     * @param inviterShortCode
     * @return
     */
    private ShareRequest prepareRequest(String sharerSphereId, String inviterShortCode) {
        ShareRequest shareRequest = null;

        Sphere sphere = sphereRegistryWrapper.getSphere(sharerSphereId);
        Map<String, ArrayList<ZirkId>> deviceServices = sphere.deviceServices;

        if (deviceServices != null && !deviceServices.isEmpty()
                && deviceServices.containsKey(deviceInterface.getDeviceId())) {

            // get device services of sphere
            ArrayList<ZirkId> services = deviceServices.get(deviceInterface.getDeviceId());

            if (services != null && !services.isEmpty()) {
                DeviceInformation deviceInformation = sphereRegistryWrapper
                        .getDeviceInformation(deviceInterface.getDeviceId());

                BezirkDeviceInfo bezirkDeviceInfo = new BezirkDeviceInfo(deviceInterface.getDeviceId(),
                        deviceInformation.getDeviceName(), deviceInformation.getDeviceType(), null, false,
                        (List<BezirkZirkInfo>) sphereRegistryWrapper.getBezirkServiceInfo(services));

                shareRequest = new ShareRequest(inviterShortCode, bezirkDeviceInfo,
                        BezirkNetworkUtilities.getServiceEndPoint(null), sharerSphereId);
                return shareRequest;
            }
        }
        return shareRequest;
    }

    /**
     * Validate the Share request: shortCode, sphereExchangeData, bezirkDeviceInfo
     *
     * @param shareRequest
     * @return
     */
    private boolean validateRequest(ShareRequest shareRequest) {
        if (shareRequest == null) {
            logger.error("sphere share request received is null");
            return false;
        }

        String inviterShortCode = shareRequest.getSphereId();
        BezirkDeviceInfo sharerBezirkDeviceInfo = shareRequest.getBezirkDeviceInfo();
        String inviterSphereId = sphereRegistryWrapper.getSphereIdFromPasscode(inviterShortCode);
        String sharerSphereId = shareRequest.getSharerSphereId();

        if (sharerBezirkDeviceInfo == null) {
            logger.error("Caught device/sphere Exchange data is not valid");
            return false;
        }

        // sender device is equal to current device ignore the results
        if (sharerBezirkDeviceInfo.getDeviceId().equals(deviceInterface.getDeviceId())) {
            logger.debug("Found request initiated by same device, dropping ShareRequest");
            return false;
        }

        if ((inviterShortCode == null) || (inviterSphereId == null) || (sharerSphereId == null)
                || (!sphereRegistryWrapper.existsSphereIdInKeyMaps(inviterShortCode))) {
            logger.error("Invalid sphere information");
            return false;
        }

        //check if you are the owner of the sphere for which response is received
        Sphere sphere = sphereRegistryWrapper.getSphere(inviterSphereId);
        if (!(sphere instanceof OwnerSphere)) {
            logger.debug("Response received for a known sphere. This device does not own the sphere");
            return false;
        }

        return true;
    }

    /**
     * Validate the Share response: SphereExchangeData, BezirkDeviceInfo,
     * sharerSphereId
     *
     * @param - shareResponse
     * @return - True if response if valid, else, False.
     */
    private boolean validateResponse(ShareResponse shareResponse) {
        if (shareResponse == null) {
            logger.error("Share response received is null");
            return false;
        }

        SphereExchangeData sphereExchangeData = SphereExchangeData
                .deserialize(shareResponse.getSphereExchangeDataString());
        BezirkDeviceInfo bezirkDeviceInfo = shareResponse.getBezirkDeviceInfo();
        String sharerSphereId = shareResponse.getSharerSphereId();

        if (sphereExchangeData == null) {
            logger.error("sphereExchangeData is not valid");
            return false;
        }

        if (bezirkDeviceInfo == null) {
            logger.error("bezirkDeviceInfo is not valid");
            return false;
        }

        if (sharerSphereId == null || !sphereRegistryWrapper.containsSphere(sharerSphereId)) {
            logger.error("sharerSphereId is not valid");
            return false;
        }

        return true;
    }

    private ShareResponse prepareResponse(String inviterShortCode, String inviterSphereId, BezirkZirkEndPoint sharer,
                                          String uniqueKey, String sharerSphereId) {
        ShareResponse shareResponse = null;
        String sphereExchangeData = sphereRegistryWrapper.getShareCodeString(inviterSphereId);
        if (sphereExchangeData == null) {
            logger.error("Share response not prepared for sphere : " + inviterSphereId);
            return shareResponse;
        }

        Sphere sphere = sphereRegistryWrapper.getSphere(inviterSphereId);
        Map<String, ArrayList<ZirkId>> deviceServices = sphere.deviceServices;
        if (deviceServices != null && !deviceServices.isEmpty()
                && deviceServices.containsKey(deviceInterface.getDeviceId())) {

            // get device services of sphere
            ArrayList<ZirkId> services = deviceServices.get(deviceInterface.getDeviceId());

            if (services != null && !services.isEmpty()) {
                DeviceInformation deviceInformation = sphereRegistryWrapper
                        .getDeviceInformation(deviceInterface.getDeviceId());

                BezirkDeviceInfo bezirkDeviceInfoToSend = new BezirkDeviceInfo(deviceInterface.getDeviceId(),
                        deviceInformation.getDeviceName(), deviceInformation.getDeviceType(), null, false,
                        (List<BezirkZirkInfo>) sphereRegistryWrapper.getBezirkServiceInfo(services));
                shareResponse = new ShareResponse(BezirkNetworkUtilities.getServiceEndPoint(null), sharer, uniqueKey,
                        inviterShortCode, bezirkDeviceInfoToSend, sphereExchangeData, sharerSphereId);
                return shareResponse;
            }
        }
        return shareResponse;
    }

}
