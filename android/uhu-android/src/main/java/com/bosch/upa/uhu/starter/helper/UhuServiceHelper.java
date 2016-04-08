package com.bosch.upa.uhu.starter.helper;

import android.content.Intent;

import com.bezirk.api.addressing.Address;
import com.bezirk.api.addressing.Location;
import com.bezirk.api.serialization.AddressSerializer;
import com.bosch.upa.uhu.commons.UhuCompManager;
import com.bosch.upa.uhu.comms.UhuComms;
import com.bosch.upa.uhu.messagehandler.StreamStatusMessage;
import com.bosch.upa.uhu.proxy.android.ProxyforServices;
import com.bosch.upa.uhu.proxy.api.impl.SubscribedRole;
import com.bosch.upa.uhu.proxy.api.impl.UhuServiceEndPoint;
import com.bosch.upa.uhu.proxy.api.impl.UhuServiceId;
import com.bosch.upa.uhu.util.UhuValidatorUtility;
import com.google.gson.Gson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a helper class to pass the intent to proxy for service actions.
 *
 *
 * Created by AJC6KOR on 9/2/2015.
 */
public final class UhuServiceHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(UhuServiceHelper.class);

    private  final ProxyforServices proxy;

    public UhuServiceHelper(ProxyforServices proxy){

        this.proxy = proxy;
    }

    /**
     * Sends the subscribe request to proxy.
     * @param intent Intent received
     */
    void subscribeService(Intent intent) {
        LOGGER.info("Received subscription from service");
        String serviceIdKEY = "serviceId";
        String subPrtclKEY = "protocol";

        String serviceIdAsString = intent.getStringExtra(serviceIdKEY);
        String protocolRoleAsString = intent.getStringExtra(subPrtclKEY);
        if (UhuValidatorUtility.checkForString(serviceIdAsString) && UhuValidatorUtility.checkForString(protocolRoleAsString)) {
            final Gson gson = new Gson();
            final UhuServiceId serviceId = gson.fromJson(serviceIdAsString, UhuServiceId.class);
            final SubscribedRole subscribedRole = gson.fromJson(protocolRoleAsString, SubscribedRole.class);
            if (UhuValidatorUtility.checkUhuServiceId(serviceId) && UhuValidatorUtility.checkProtocolRole(subscribedRole)) {
                proxy.subscribeService(serviceId, subscribedRole);

            } else {
                LOGGER.error("trying to subscribe with Null serviceId/ pRole");
            }

        } else {
            LOGGER.error("Error in Service Subscription. Check for the values being sent");
        }
    }

    /**
     * Sends Register request to proxy
     * @param intent Intent received
     */
    void registerService(Intent intent) {
        String serviceIdKEY = "serviceId";
        String serviceNameKEY = "serviceName";

        final String serviceIdAsString = intent.getStringExtra(serviceIdKEY);
        final String serviceName = intent.getStringExtra(serviceNameKEY);

        LOGGER.info("Service registration to Uhu. Name : " + serviceName + " Id : " + serviceIdAsString);

        if (UhuValidatorUtility.checkForString(serviceIdAsString) && UhuValidatorUtility.checkForString(serviceName)) {
            final UhuServiceId serviceId = new Gson().fromJson(serviceIdAsString, UhuServiceId.class);
            if (UhuValidatorUtility.checkUhuServiceId(serviceId)) {
                proxy.registerService(serviceId, serviceName);
            } else {
                LOGGER.error("Trying to subscribe with null ServiceId");
            }

        } else {
            LOGGER.error("Error in Service Registration. Check for the values being sent");
        }
    }

    /**
     * Sends UnSubscribe request to proxy
     * @param intent Intent received
     */
    void unsubscribeService(Intent intent) {
        LOGGER.info("Received unsubscribe from service");
        String serviceIdKEY = "serviceId";
        String subPrtclKEY = "protocol";

        final String serviceIdAsString = intent.getStringExtra(serviceIdKEY);
        final String protocolRoleAsString = intent.getStringExtra(subPrtclKEY);
        if (UhuValidatorUtility.checkForString(serviceIdAsString)) {
            final Gson gson = new Gson();
            final UhuServiceId serviceId = gson.fromJson(serviceIdAsString, UhuServiceId.class);
            final SubscribedRole subscribedRole = gson.fromJson(protocolRoleAsString, SubscribedRole.class);
            if (UhuValidatorUtility.checkUhuServiceId(serviceId)) {
                if (UhuValidatorUtility.isObjectNotNull(subscribedRole)) {
                    proxy.unsubscribe(serviceId, subscribedRole);
                } else {
                    proxy.unregister(serviceId);
                }

            } else {
                LOGGER.error("trying to subscribe with Null serviceId/ pRole");
            }

        } else {
            LOGGER.error("Error in Service Subscription. Check for the values being sent");
        }
    }

    /**
     * Sends discovery request to proxy
     * @param intent Intent received
     */
    void discoverService(Intent intent) {
        LOGGER.info("Received discovery message from service");
        String serviceIdKEY = "serviceId";
        String addressKEY = "address";
        String pRoleKEY = "pRole";
        String timeoutKEY = "timeout";
        String maxDiscoveredKEY = "maxDiscovered";
        String discoveryIdKEY = "discoveryId";

        final String serviceIdAsString = intent.getStringExtra(serviceIdKEY);
        final String addressAsString = intent.getStringExtra(addressKEY);
        final String pRoleMsg = intent.getStringExtra(pRoleKEY);


        if (UhuValidatorUtility.checkForString(serviceIdAsString) && UhuValidatorUtility.checkForString(addressAsString) && UhuValidatorUtility.checkForString(pRoleMsg)) {
            final Gson gson = new Gson();
            final UhuServiceId serviceId = gson.fromJson(serviceIdAsString, UhuServiceId.class);
            final Address address = gson.fromJson(addressAsString, Address.class);
            final SubscribedRole pRole = gson.fromJson(pRoleMsg, SubscribedRole.class);
            final long timeout = intent.getLongExtra(timeoutKEY, 1000);
            final int maxDiscovered = intent.getIntExtra(maxDiscoveredKEY, 1);
            final int discoveryId = intent.getIntExtra(discoveryIdKEY, 1);

            if (UhuValidatorUtility.checkUhuServiceId(serviceId)) {
                proxy.discover(serviceId, address, pRole, discoveryId, timeout, maxDiscovered);
                LOGGER.info("Discovery Request pass to Proxy");

            } else {
                LOGGER.error("Service Id is null, dropping discovery request");            }

        } else {

            LOGGER.error(" Invalid arguments received to discover");
        }
    }

    /**
     * Sends UnicastStream to proxy
     * @param intent Intent received
     */
    void sendUnicastStream(Intent intent) {
        LOGGER.info("------------ Received message to push the Stream ----------------------");
        boolean isStreamingValid = UhuComms.isStreamingEnabled();
        if (!isStreamingValid) {
            LOGGER.error(" Streaming is not enabled!");
        }
        final String serviceIdAsString = intent.getStringExtra("serviceId");
        final String recipientAsString = intent.getStringExtra("receiverSEP");
        final String filePath = intent.getStringExtra("filePath");
        final String streamAsString = intent.getStringExtra("stream");
        final short localStreamId = intent.getShortExtra("localStreamId", (short) -1);

        if (UhuValidatorUtility.checkForString(serviceIdAsString,recipientAsString,filePath,streamAsString) && -1 != localStreamId) {
            final Gson gson = new Gson();
            final UhuServiceId serviceId = gson.fromJson(serviceIdAsString, UhuServiceId.class);
            final UhuServiceEndPoint recipient = gson.fromJson(recipientAsString, UhuServiceEndPoint.class);

            isStreamingValid = sendStream(filePath, streamAsString, localStreamId, serviceId, recipient);

        } else {
            LOGGER.error("Invalid arguments received");
            isStreamingValid = false;
        }

        if (!isStreamingValid) {
            StreamStatusMessage streamStatusCallbackMessage = new StreamStatusMessage(new Gson().fromJson(serviceIdAsString, UhuServiceId.class), 0, localStreamId);
            UhuCompManager.getplatformSpecificCallback().onStreamStatus(streamStatusCallbackMessage);
        }
    }

    private boolean sendStream(String filePath, String streamAsString, short localStreamId, UhuServiceId serviceId, UhuServiceEndPoint recipient) {
        if (UhuValidatorUtility.checkUhuServiceEndPoint(recipient) && UhuValidatorUtility.checkUhuServiceId(serviceId)) {
            short sendStreamStatus = proxy.sendStream(serviceId, recipient, streamAsString, filePath, localStreamId);
            if (-1 == sendStreamStatus) {
                return false;
            }
        } else {
            LOGGER.error("Recipient SEP or UhuServiceID is not valid ");
            return false;

        }
        return true;
    }

    /**
     * Sends Multicast stream to proxy
     * @param intent Intent received
     */
    void sendMulticastStream(Intent intent) {
        LOGGER.info("------------ Received message to push the Stream ----------------------");
        boolean isStreamingValid = true;
        if (!UhuComms.isStreamingEnabled()) {
            LOGGER.error(" Streaming is not enabled!");
            isStreamingValid = false;
        }
        final String serviceIdAsString = intent.getStringExtra("serviceId");
        final String recipientAsString = intent.getStringExtra("receiverSEP");
        final String streamAsString = intent.getStringExtra("stream");
        final short localStreamId = intent.getShortExtra("localStreamId", (short) -1);


        try{
            final Gson gson = new Gson();
            final UhuServiceId serviceId = gson.fromJson(serviceIdAsString, UhuServiceId.class);
            final UhuServiceEndPoint recipient = gson.fromJson(recipientAsString, UhuServiceEndPoint.class);

            if(UhuValidatorUtility.checkRTCStreamRequest(serviceId, recipient)){

                if (-1 == proxy.sendStream(serviceId, recipient, streamAsString, localStreamId)) {
                    isStreamingValid = false;
                }

            }else{
                LOGGER.error("Invalid arguments received");
                isStreamingValid = false;
            }
        }catch(Exception e){
            LOGGER.error("Invalid arguments received",e);
            isStreamingValid = false;
        }

        if (!isStreamingValid) {
            StreamStatusMessage streamStatusCallbackMessage = new StreamStatusMessage(new Gson().fromJson(serviceIdAsString, UhuServiceId.class), 0, localStreamId);
            UhuCompManager.getplatformSpecificCallback().onStreamStatus(streamStatusCallbackMessage);
        }
    }

    /**
     * Sends MulticastEvent to proxy
     * @param intent Intent received
     */
    void sendMulticastEvent(Intent intent) {
        LOGGER.info("Received multicast message from service");
        String serviceIdKEY = "serviceId";
        String addressKEY = "address";
        String mEventMsgKEY = "multicastEvent";

        final String serviceIdAsString = intent.getStringExtra(serviceIdKEY);
        final String addressAsString = intent.getStringExtra(addressKEY);
        final String mEventMsg = intent.getStringExtra(mEventMsgKEY);

        // Validate intent properties
        if (UhuValidatorUtility.checkForString(serviceIdAsString) && UhuValidatorUtility.checkForString(addressAsString) && UhuValidatorUtility.checkForString(mEventMsg)) {
            final Gson gson = new Gson();
            final UhuServiceId serviceId = gson.fromJson(serviceIdAsString, UhuServiceId.class);
            if (UhuValidatorUtility.checkUhuServiceId(serviceId)) {
                final Address address = new AddressSerializer().fromJson(addressAsString);
                LOGGER.debug("Sending multicast event from service: " + serviceIdAsString);
                proxy.sendMulticastEvent(serviceId, address, mEventMsg);
            } else {
                LOGGER.error("trying to send multicast message with Null serviceId");

            }
        } else {

            LOGGER.error(" Invalid arguments received to send multicast Event");
        }
    }

    /**
     * Sends UnicastEvent to proxy
     * @param intent Intent received
     */
    void sendUnicastEvent(Intent intent) {
        LOGGER.info("Received unicast message from service");
        String serviceIdKEY = "serviceId";
        String sepKEY = "receiverSep";
        String uEventMsgKEY = "eventMsg";

        final String serviceIdAsString = intent.getStringExtra(serviceIdKEY);
        final String sepAsString = intent.getStringExtra(sepKEY);
        final String msg = intent.getStringExtra(uEventMsgKEY);
        if (UhuValidatorUtility.checkForString(serviceIdAsString) && UhuValidatorUtility.checkForString(sepAsString) && UhuValidatorUtility.checkForString(msg)) {
            final Gson gson = new Gson();
            final UhuServiceId serviceId = gson.fromJson(serviceIdAsString, UhuServiceId.class);
            final UhuServiceEndPoint serviceEndPoint = gson.fromJson(sepAsString, UhuServiceEndPoint.class);
            if (UhuValidatorUtility.checkUhuServiceId(serviceId) && UhuValidatorUtility.checkUhuServiceEndPoint(serviceEndPoint)) {
                proxy.sendUnicastEvent(serviceId, serviceEndPoint, msg);
            } else {
                LOGGER.error("Check unicast parameters");
            }
        } else {
            LOGGER.error(" Invalid arguments received to send Unicast Event");

        }
    }

    /**
     * Sets service location via proxy
     * @param intent Intent received
     */
    void setLocation(Intent intent) {
        String sid = (String) intent.getExtras().get("serviceId");
        String location = (String) intent.getExtras().get("locationData");
        LOGGER.info("Received location " + location + " from service");

        if (UhuValidatorUtility.checkForString(sid) && UhuValidatorUtility.checkForString(location)) {
            UhuServiceId serviceId = new Gson().fromJson(sid, UhuServiceId.class);
            Location loc = new Gson().fromJson(location, Location.class);
            if (UhuValidatorUtility.checkUhuServiceId(serviceId)) {
                proxy.setLocation(serviceId, loc);
            }
        } else {
            LOGGER.error("Invalid parameters for location");
        }
    }


}