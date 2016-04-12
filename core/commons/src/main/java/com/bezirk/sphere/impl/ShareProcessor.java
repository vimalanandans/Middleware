package com.bezirk.sphere.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bezirk.sphere.api.IUhuSphereListener;
import com.bezirk.sphere.security.SphereKeys;
import com.bezirk.devices.UPADeviceInterface;
import com.bezirk.api.objects.UhuDeviceInfo;
import com.bezirk.api.objects.UhuServiceInfo;
import com.bezrik.network.UhuNetworkUtilities;
import com.bezirk.proxy.api.impl.UhuServiceEndPoint;
import com.bezirk.proxy.api.impl.UhuServiceId;
import com.bezirk.sphere.api.ICryptoInternals;
import com.bezirk.sphere.messages.ShareRequest;
import com.bezirk.sphere.messages.ShareResponse;

/**
 * @author Rishabh Gulati
 * 
 */
public class ShareProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShareProcessor.class);
    private ICryptoInternals crypto;
    private UPADeviceInterface upaDeviceInterface;
    private CommsUtility comms;
    private SphereRegistryWrapper sphereRegistryWrapper;
    private static final String SHARE_FAILURE_MSG = "Share Failed";

    /**
     * 
     * @param sphereUtils
     * @param crypto
     * @param upaDeviceInterface
     * @param uhuComms
     * @param sphereRegistryWrapper
     */
    public ShareProcessor(ICryptoInternals crypto, UPADeviceInterface upaDeviceInterface, CommsUtility comms,
            SphereRegistryWrapper sphereRegistryWrapper) {
        this.crypto = crypto;
        this.upaDeviceInterface = upaDeviceInterface;
        this.comms = comms;
        this.sphereRegistryWrapper = sphereRegistryWrapper;
    }

    /**
     * Process the share qr code/short-code request and send the request if the shareCode and
     * sphereId are correct.
     * 
     * @param shareCode
     *            can be in the form of qr-code or a 7-digit[may change later]
     *            code
     * @param sphereId of the sphere whose services are to be shared.
     * 
     * @return True if request was sent. False otherwise.
     */
    public boolean processShareCode(String shareCode, String sphereId) {

        String inviterShortCode = null;
        // sphereId from which the services need to be shared
        String sharerSphereId = null;
        ShareRequest shareRequest = null;

        /************************************************************************
         * Step1: validate the sharer sphereId passed and set it.
         ************************************************************************/

        if (sphereRegistryWrapper.containsSphere(sphereId)) {
            sharerSphereId = sphereId;
            LOGGER.info("Share Process, Step1: sharerSphereId validation complete");
        } else {
            LOGGER.error("Share Process, Step1: sharerSphereId validation failed");
            return false;
        }

        /************************************************************************
         * Step2: validate request: short code/QR code with version and embedded
         * short code and set the code
         ************************************************************************/

        inviterShortCode = validateCodeString(shareCode);
        if (inviterShortCode != null) {
            LOGGER.info("Share Process, Step2: shortCode validation complete");
        } else {
            LOGGER.error("Share Process, Step2: shortCode validation failed");
            return false;
        }

        /************************************************************************
         * Step3: handle crypto aspects, i.e. generating the sphereKey and
         * setting them.
         ************************************************************************/

        if (addCatchCode(inviterShortCode)) {
            LOGGER.info("Share Process, Step3: crypto procedure complete");
        } else {
            LOGGER.error("Share Process, Step3: crypto procedure failed");
            return false;
        }

        /************************************************************************
         * Step4: prepare shareRequest to be sent to the device sharing the
         * sphere
         ************************************************************************/

        shareRequest = prepareRequest(sharerSphereId, inviterShortCode);
        if (shareRequest != null) {
            LOGGER.info("Share Process, Step4: share request preparation complete");
        } else {
            LOGGER.error("Share Process, Step4: share request preparation failed");
            return false;
        }

        /************************************************************************
         * Step5: send the request
         ************************************************************************/

        if (comms.sendMessage(shareRequest)) {
            LOGGER.info("Share Process, Step5: share request sending complete");
        } else {
            LOGGER.error("Share Process, Step5: share request sending failed");
            return false;
        }

        return true;
    }

    /**
     * Process share request received from the device sharing its services and send a response.
     * 
     * @param shareRequest - ShareRequest object containing the information about 
     * the sharer's device and their sphereID.
     * 
     * @return - True if response was sent successfully. False otherwise.
     */
    public boolean processRequest(ShareRequest shareRequest) {

        UhuDeviceInfo sharerUhuDeviceInfo = null;
        String inviterShortCode = null;
        String inviterSphereId = null;
        String sharerSphereId = null;
        ShareResponse shareResponse = null;

        /************************************************************************
         * Step1: validate the request
         ************************************************************************/

        if (validateRequest(shareRequest)) {
            LOGGER.info("Share Request Processing, Step1: request validation complete");
        } else {
            LOGGER.error("Share Request Processing, Step1: request validation failed");
            return false;
        }

        sharerUhuDeviceInfo = shareRequest.getUhuDeviceInfo();
        inviterShortCode = shareRequest.getSphereId();
        inviterSphereId = sphereRegistryWrapper.getSphereIdFromPasscode(inviterShortCode);
        sharerSphereId = shareRequest.getSharerSphereId();

        // for sending unicast back to the device sharing its services
        String uniqueKey = shareRequest.getUniqueKey();
        UhuServiceEndPoint recipient = shareRequest.getSender();

        /************************************************************************
         * Step2: store sphere exchange data
         ************************************************************************/

        if (storeData(inviterSphereId, sharerUhuDeviceInfo)) {
            LOGGER.info("Share Request Processing, Step2: storing sphere exchange data complete");
        } else {
            sphereRegistryWrapper.updateListener(SphereRegistryWrapper.Operation.SHARE, IUhuSphereListener.Status.FAILURE, SHARE_FAILURE_MSG);
            LOGGER.error("Share Request Processing, Step2: storing sphere exchange data failed");
            return false;
        }

        /************************************************************************
         * Step3: extract services from the sphere with the passed shortCode &
         * create response
         ************************************************************************/

        shareResponse = prepareResponse(inviterShortCode, inviterSphereId, recipient, uniqueKey, sharerSphereId);
        if (shareResponse != null) {
            LOGGER.info("Share Request Processing, Step3: preparing share response complete");
        } else {
            sphereRegistryWrapper.updateListener(SphereRegistryWrapper.Operation.SHARE, IUhuSphereListener.Status.FAILURE, SHARE_FAILURE_MSG);
            LOGGER.error("Share Request Processing, Step3: preparing share response failed");
            return false;
        }

        /************************************************************************
         * Step4: send the share response
         ************************************************************************/

        if (comms.sendMessage(shareResponse)) {
            LOGGER.info("Share Request Processing, Step4: share response sending complete");
        } else {
            sphereRegistryWrapper.updateListener(SphereRegistryWrapper.Operation.SHARE, IUhuSphereListener.Status.FAILURE, SHARE_FAILURE_MSG);
            LOGGER.error("Share Request Processing, Step4: share response sending failed");
            return false;
        }

        sphereRegistryWrapper.updateListener(SphereRegistryWrapper.Operation.SHARE, IUhuSphereListener.Status.SUCCESS, "Share successful");
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
            LOGGER.info("Share Response Processing, Step1: response validation complete");
        } else {
            LOGGER.error("Share Response Processing, Step1: response validation failed");
            return false;
        }

        /************************************************************************
         * Step2: store sphere exchange data
         ************************************************************************/

        if (storeData(SphereExchangeData.deserialize(shareResponse.getSphereExchangeDataString()),
                shareResponse.getUhuDeviceInfo(), shareResponse.getSharerSphereId())) {
            sphereRegistryWrapper.updateListener(SphereRegistryWrapper.Operation.SHARE, IUhuSphereListener.Status.SUCCESS, "Share successful");
            return true;
        } else {
            sphereRegistryWrapper.updateListener(SphereRegistryWrapper.Operation.SHARE, IUhuSphereListener.Status.FAILURE, SHARE_FAILURE_MSG);
            return false;
        }
    }

    /**
     * Store the inviter SphereID and sharer Device Info from the received share
     * request.
     * 
     * @param inviterSphereId
     *            - has to be valid and non-null
     * @param sharerUhuDeviceInfo
     *            - has to be non-null
     * @return - True if data was added to the registry successfully, else,
     *         False.
     */
    private boolean storeData(String inviterSphereId, UhuDeviceInfo sharerUhuDeviceInfo) {
        if (sphereRegistryWrapper.addDevice(sharerUhuDeviceInfo.getDeviceId(),
                new DeviceInformation(sharerUhuDeviceInfo.getDeviceName(), sharerUhuDeviceInfo.getDeviceType()))
                && sphereRegistryWrapper.addMemberServices(sharerUhuDeviceInfo, inviterSphereId,
                        sharerUhuDeviceInfo.getDeviceId())) {
            sphereRegistryWrapper.persist();
            return true;
        }
        return false;
    }

    /**
     * Store the data
     * 
     * @param sphereExchangeData
     *            - has to be non-null
     * @param inviterUhuDeviceInfo
     *            - has to be non-null
     * @param sharerSphereId
     *            - has to be non-null
     * @return - True if data was stored successfully in the registry, else
     *         False. <br>
     *         - NullPointerException is thrown if SphereExchangeData obj is
     *         null.
     */
    private boolean storeData(SphereExchangeData sphereExchangeData, UhuDeviceInfo inviterUhuDeviceInfo,
            String sharerSphereId) {

        LOGGER.debug("Sphere Exchange data:\n" + sphereExchangeData.toString());
        LOGGER.debug("Uhu Device Info:\n" + inviterUhuDeviceInfo.toString());
        LOGGER.debug("Sharer sphere Id: " + sharerSphereId);
        // add device information
        sphereRegistryWrapper.addDevice(sphereExchangeData.getDeviceID(),
                new DeviceInformation(sphereExchangeData.getDeviceName(), sphereExchangeData.getDeviceType()));

        // add sphere
        HashSet<String> ownerDevices = new HashSet<String>();
        ownerDevices.add(sphereExchangeData.getDeviceID());

        Sphere sphere = new MemberSphere(sphereExchangeData.getSphereName(), sphereExchangeData.getSphereType(),
                ownerDevices, new LinkedHashMap<String, ArrayList<UhuServiceId>>(), false);

        sphereRegistryWrapper.addSphere(sphereExchangeData.getSphereID(), sphere);

        // add received services
        sphereRegistryWrapper.addMemberServices(inviterUhuDeviceInfo, sphereExchangeData.getSphereID(),
                sphereExchangeData.getDeviceID());

        // add services from the sharer Sphere
        Sphere shareSphere = sphereRegistryWrapper.getSphere(sharerSphereId);
        Map<String, ArrayList<UhuServiceId>> deviceServices = shareSphere.deviceServices;
        if (deviceServices != null && !deviceServices.isEmpty()
                && deviceServices.containsKey(upaDeviceInterface.getDeviceId())) {

            ArrayList<UhuServiceId> services = deviceServices.get(upaDeviceInterface.getDeviceId());
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
     * @param inviterShareCode
     * @return
     */
    private String validateCodeString(String inviterShareCode) {
        /**
         * Improvements/Additions <br>
         * if (QRCodeData.checkVersionTag(inviterShareCode) &&
         * QRCodeData.checkCompatibility(inviterShareCode)) <br>
         * { // retrieve the catchCode QRCodeData catchData =
         * QRCodeData.deserialize(inviterShareCode); return
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
     * @param inviterShortCode
     *            - has to be non-null
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
        Map<String, ArrayList<UhuServiceId>> deviceServices = sphere.deviceServices;

        if (deviceServices != null && !deviceServices.isEmpty()
                && deviceServices.containsKey(upaDeviceInterface.getDeviceId())) {

            // get device services of sphere
            ArrayList<UhuServiceId> services = deviceServices.get(upaDeviceInterface.getDeviceId());

            if (services != null && !services.isEmpty()) {
                DeviceInformation deviceInformation = sphereRegistryWrapper
                        .getDeviceInformation(upaDeviceInterface.getDeviceId());

                UhuDeviceInfo uhuDeviceInfo = new UhuDeviceInfo(upaDeviceInterface.getDeviceId(),
                        deviceInformation.getDeviceName(), deviceInformation.getDeviceType(), null, false,
                        (List<UhuServiceInfo>) sphereRegistryWrapper.getUhuServiceInfo(services));

                shareRequest = new ShareRequest(inviterShortCode, uhuDeviceInfo,
                        UhuNetworkUtilities.getServiceEndPoint(null), sharerSphereId);
                return shareRequest;
            }
        }
        return shareRequest;
    }

    /**
     * Validate the Share request: shortCode, sphereExchangeData, uhuDeviceInfo
     * 
     * @param sphereCatchRequest
     * @return
     */
    private boolean validateRequest(ShareRequest shareRequest) {
        if (shareRequest == null) {
            LOGGER.error("Sphere share request received is null");
            return false;
        }

        String inviterShortCode = shareRequest.getSphereId();
        UhuDeviceInfo sharerUhuDeviceInfo = shareRequest.getUhuDeviceInfo();
        String inviterSphereId = sphereRegistryWrapper.getSphereIdFromPasscode(inviterShortCode);
        String sharerSphereId = shareRequest.getSharerSphereId();

        if (sharerUhuDeviceInfo == null) {
            LOGGER.error("Catched device/Sphere Exchange data is not valid");
            return false;
        }

        // sender device is equal to current device ignore the results
        if (sharerUhuDeviceInfo.getDeviceId().equals(upaDeviceInterface.getDeviceId())) {
            LOGGER.debug("Found request initiated by same device, dropping ShareRequest");
            return false;
        }

        if ((inviterShortCode == null) || (inviterSphereId == null) || (sharerSphereId == null)
                || (!sphereRegistryWrapper.existsSphereIdInKeyMaps(inviterShortCode))) {
            LOGGER.error("Invalid sphere information");
            return false;
        }

        //check if you are the owner of the sphere for which response is received
        Sphere sphere = sphereRegistryWrapper.getSphere(inviterSphereId);
        if(!(sphere instanceof OwnerSphere)){
            LOGGER.debug("Response received for a known sphere. This device does not own the sphere");
            return false;
        }
        
        return true;
    }

    /**
     * Validate the Share response: SphereExchangeData, UhuDeviceInfo,
     * sharerSphereId
     * 
     * @param -
     *            shareResponse
     * @return - True if response if valid, else, False.
     */
    private boolean validateResponse(ShareResponse shareResponse) {
        if (shareResponse == null) {
            LOGGER.error("Share response received is null");
            return false;
        }

        SphereExchangeData sphereExchangeData = SphereExchangeData
                .deserialize(shareResponse.getSphereExchangeDataString());
        UhuDeviceInfo uhuDeviceInfo = shareResponse.getUhuDeviceInfo();
        String sharerSphereId = shareResponse.getSharerSphereId();
                
        if (sphereExchangeData == null) {
            LOGGER.error("sphereExchangeData is not valid");
            return false;
        }

        if (uhuDeviceInfo == null) {
            LOGGER.error("uhuDeviceInfo is not valid");
            return false;
        }

        if (sharerSphereId == null || !sphereRegistryWrapper.containsSphere(sharerSphereId)) {
            LOGGER.error("sharerSphereId is not valid");
            return false;
        }
                
        return true;
    }

    /**
     * 
     * @param uhuDeviceInfo
     * @param inviterShortCode
     * @param inviterSphereId
     *            sphereId pertaining to the sphere which generated the short
     *            code.
     * @param sharer
     *            - has to be non-null
     * @param uniqueKey
     * @return
     */
    private ShareResponse prepareResponse(String inviterShortCode, String inviterSphereId, UhuServiceEndPoint sharer,
            String uniqueKey, String sharerSphereId) {
        ShareResponse shareResponse = null;
        String sphereExchangeData = sphereRegistryWrapper.getShareCodeString(inviterSphereId);
        if (sphereExchangeData == null) {
            LOGGER.error("Share response not prepared for sphere : " + inviterSphereId);
            return shareResponse;
        }

        Sphere sphere = sphereRegistryWrapper.getSphere(inviterSphereId);
        Map<String, ArrayList<UhuServiceId>> deviceServices = sphere.deviceServices;
        if (deviceServices != null && !deviceServices.isEmpty()
                && deviceServices.containsKey(upaDeviceInterface.getDeviceId())) {

            // get device services of sphere
            ArrayList<UhuServiceId> services = deviceServices.get(upaDeviceInterface.getDeviceId());

            if (services != null && !services.isEmpty()) {
                DeviceInformation deviceInformation = sphereRegistryWrapper
                        .getDeviceInformation(upaDeviceInterface.getDeviceId());

                UhuDeviceInfo uhuDeviceInfoToSend = new UhuDeviceInfo(upaDeviceInterface.getDeviceId(),
                        deviceInformation.getDeviceName(), deviceInformation.getDeviceType(), null, false,
                        (List<UhuServiceInfo>) sphereRegistryWrapper.getUhuServiceInfo(services));
                shareResponse = new ShareResponse(UhuNetworkUtilities.getServiceEndPoint(null), sharer, uniqueKey,
                        inviterShortCode, uhuDeviceInfoToSend, sphereExchangeData, sharerSphereId);
                return shareResponse;
            }
        }
        return shareResponse;
    }

}
