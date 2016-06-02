package com.bezirk.starter.helper;

import android.content.Intent;

import com.bezirk.commons.BezirkCompManager;
import com.bezirk.comms.BezirkCommunications;
import com.bezirk.messagehandler.StreamStatusMessage;
import com.bezirk.middleware.addressing.RecipientSelector;
import com.bezirk.middleware.addressing.Location;
import com.bezirk.proxy.android.ProxyForZirks;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.proxy.api.impl.BezirkZirkId;
import com.bezirk.proxy.api.impl.SubscribedRole;
import com.bezirk.util.BezirkValidatorUtility;
import com.google.gson.Gson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * This is a helper class to pass the intent to proxy for zirk actions.
 */
public final class BezirkServiceHelper {

    private static final Logger logger = LoggerFactory.getLogger(BezirkServiceHelper.class);

    private final ProxyForZirks proxy;

    public BezirkServiceHelper(ProxyForZirks proxy) {

        this.proxy = proxy;
    }

    /**
     * Sends the subscribe request to proxy.
     *
     * @param intent Intent received
     */
    void subscribeService(Intent intent) {
        logger.info("Received subscription from zirk");
        String serviceIdKEY = "zirkId";
        String subPrtclKEY = "protocol";

        String serviceIdAsString = intent.getStringExtra(serviceIdKEY);
        String protocolRoleAsString = intent.getStringExtra(subPrtclKEY);
        if (BezirkValidatorUtility.checkForString(serviceIdAsString) && BezirkValidatorUtility.checkForString(protocolRoleAsString)) {
            final Gson gson = new Gson();
            final BezirkZirkId serviceId = gson.fromJson(serviceIdAsString, BezirkZirkId.class);
            final SubscribedRole subscribedRole = gson.fromJson(protocolRoleAsString, SubscribedRole.class);
            if (BezirkValidatorUtility.checkBezirkZirkId(serviceId) && BezirkValidatorUtility.checkProtocolRole(subscribedRole)) {
                proxy.subscribeService(serviceId, subscribedRole);

            } else {
                logger.error("trying to subscribe with Null zirkId/ protocolRole");
            }

        } else {
            logger.error("Error in Zirk Subscription. Check for the values being sent");
        }
    }

    /**
     * Sends Register request to proxy
     *
     * @param intent Intent received
     */
    void registerService(Intent intent) {
        String serviceIdKEY = "zirkId";
        String serviceNameKEY = "serviceName";

        final String serviceIdAsString = intent.getStringExtra(serviceIdKEY);
        final String serviceName = intent.getStringExtra(serviceNameKEY);

        logger.info("Zirk registration to Bezirk. Name : " + serviceName + " Id : " + serviceIdAsString);

        if (BezirkValidatorUtility.checkForString(serviceIdAsString) && BezirkValidatorUtility.checkForString(serviceName)) {
            final BezirkZirkId serviceId = new Gson().fromJson(serviceIdAsString, BezirkZirkId.class);
            if (BezirkValidatorUtility.checkBezirkZirkId(serviceId)) {
                proxy.registerService(serviceId, serviceName);
            } else {
                logger.error("Trying to subscribe with null ZirkId");
            }

        } else {
            logger.error("Error in Zirk Registration. Check for the values being sent");
        }
    }

    /**
     * Sends UnSubscribe request to proxy
     *
     * @param intent Intent received
     */
    void unsubscribeService(Intent intent) {
        logger.info("Received unsubscribe from zirk");
        String serviceIdKEY = "zirkId";
        String subPrtclKEY = "protocol";

        final String serviceIdAsString = intent.getStringExtra(serviceIdKEY);
        final String protocolRoleAsString = intent.getStringExtra(subPrtclKEY);
        if (BezirkValidatorUtility.checkForString(serviceIdAsString)) {
            final Gson gson = new Gson();
            final BezirkZirkId serviceId = gson.fromJson(serviceIdAsString, BezirkZirkId.class);
            final SubscribedRole subscribedRole = gson.fromJson(protocolRoleAsString, SubscribedRole.class);
            if (BezirkValidatorUtility.checkBezirkZirkId(serviceId)) {
                if (BezirkValidatorUtility.isObjectNotNull(subscribedRole)) {
                    proxy.unsubscribe(serviceId, subscribedRole);
                } else {
                    proxy.unregister(serviceId);
                }

            } else {
                logger.error("trying to subscribe with Null zirkId/ protocolRole");
            }

        } else {
            logger.error("Error in Zirk Subscription. Check for the values being sent");
        }
    }

    /**
     * Sends discovery request to proxy
     *
     * @param intent Intent received
     */
    void discoverService(Intent intent) {
        logger.info("Received discovery message from zirk");
        String serviceIdKEY = "zirkId";
        String addressKEY = "address";
        String pRoleKEY = "protocolRole";
        String timeoutKEY = "timeout";
        String maxDiscoveredKEY = "maxDiscovered";
        String discoveryIdKEY = "discoveryId";

        final String serviceIdAsString = intent.getStringExtra(serviceIdKEY);
        final String addressAsString = intent.getStringExtra(addressKEY);
        final String pRoleMsg = intent.getStringExtra(pRoleKEY);


        if (BezirkValidatorUtility.checkForString(serviceIdAsString) && BezirkValidatorUtility.checkForString(addressAsString) && BezirkValidatorUtility.checkForString(pRoleMsg)) {
            final Gson gson = new Gson();
            final BezirkZirkId serviceId = gson.fromJson(serviceIdAsString, BezirkZirkId.class);
            final RecipientSelector recipientSelector = gson.fromJson(addressAsString, RecipientSelector.class);
            final SubscribedRole pRole = gson.fromJson(pRoleMsg, SubscribedRole.class);
            final long timeout = intent.getLongExtra(timeoutKEY, 1000);
            final int maxDiscovered = intent.getIntExtra(maxDiscoveredKEY, 1);
            final int discoveryId = intent.getIntExtra(discoveryIdKEY, 1);

            if (BezirkValidatorUtility.checkBezirkZirkId(serviceId)) {
                proxy.discover(serviceId, recipientSelector, pRole, discoveryId, timeout, maxDiscovered);
                logger.info("Discovery Request pass to Proxy");

            } else {
                logger.error("Zirk Id is null, dropping discovery request");
            }

        } else {

            logger.error(" Invalid arguments received to discover");
        }
    }

    /**
     * Sends UnicastStream to proxy
     *
     * @param intent Intent received
     */
    void sendUnicastStream(Intent intent) {
        logger.info("------------ Received message to push the Stream ----------------------");
        boolean isStreamingValid = BezirkCommunications.isStreamingEnabled();
        if (!isStreamingValid) {
            logger.error(" Streaming is not enabled!");
        }
        final String serviceIdAsString = intent.getStringExtra("zirkId");
        final String recipientAsString = intent.getStringExtra("receiverSEP");
        final File file = new File(intent.getStringExtra("filePath"));
        final String streamAsString = intent.getStringExtra("stream");
        final short localStreamId = intent.getShortExtra("localStreamId", (short) -1);

        if (BezirkValidatorUtility.checkForString(serviceIdAsString, recipientAsString, streamAsString) && -1 != localStreamId) {
            final Gson gson = new Gson();
            final BezirkZirkId serviceId = gson.fromJson(serviceIdAsString, BezirkZirkId.class);
            final BezirkZirkEndPoint recipient = gson.fromJson(recipientAsString, BezirkZirkEndPoint.class);

            isStreamingValid = sendStream(file, streamAsString, localStreamId, serviceId, recipient);

        } else {
            logger.error("Invalid arguments received");
            isStreamingValid = false;
        }

        if (!isStreamingValid) {
            StreamStatusMessage streamStatusCallbackMessage = new StreamStatusMessage(new Gson().fromJson(serviceIdAsString, BezirkZirkId.class), 0, localStreamId);
            BezirkCompManager.getplatformSpecificCallback().onStreamStatus(streamStatusCallbackMessage);
        }
    }

    private boolean sendStream(File file, String streamAsString, short localStreamId, BezirkZirkId serviceId, BezirkZirkEndPoint recipient) {
        if (BezirkValidatorUtility.checkBezirkZirkEndPoint(recipient) && BezirkValidatorUtility.checkBezirkZirkId(serviceId)) {
            short sendStreamStatus = proxy.sendStream(serviceId, recipient, streamAsString, file, localStreamId);
            if (-1 == sendStreamStatus) {
                return false;
            }
        } else {
            logger.error("Recipient SEP or BezirkZirkID is not valid ");
            return false;

        }
        return true;
    }

    /**
     * Sends Multicast stream to proxy
     *
     * @param intent Intent received
     */
    void sendMulticastStream(Intent intent) {
        logger.info("------------ Received message to push the Stream ----------------------");
        boolean isStreamingValid = true;
        if (!BezirkCommunications.isStreamingEnabled()) {
            logger.error(" Streaming is not enabled!");
            isStreamingValid = false;
        }
        final String serviceIdAsString = intent.getStringExtra("zirkId");
        final String recipientAsString = intent.getStringExtra("receiverSEP");
        final String streamAsString = intent.getStringExtra("stream");
        final short localStreamId = intent.getShortExtra("localStreamId", (short) -1);


        try {
            final Gson gson = new Gson();
            final BezirkZirkId serviceId = gson.fromJson(serviceIdAsString, BezirkZirkId.class);
            final BezirkZirkEndPoint recipient = gson.fromJson(recipientAsString, BezirkZirkEndPoint.class);

            if (BezirkValidatorUtility.checkRTCStreamRequest(serviceId, recipient)) {

                if (-1 == proxy.sendStream(serviceId, recipient, streamAsString, localStreamId)) {
                    isStreamingValid = false;
                }

            } else {
                logger.error("Invalid arguments received");
                isStreamingValid = false;
            }
        } catch (Exception e) {
            logger.error("Invalid arguments received", e);
            isStreamingValid = false;
        }

        if (!isStreamingValid) {
            StreamStatusMessage streamStatusCallbackMessage = new StreamStatusMessage(new Gson().fromJson(serviceIdAsString, BezirkZirkId.class), 0, localStreamId);
            BezirkCompManager.getplatformSpecificCallback().onStreamStatus(streamStatusCallbackMessage);
        }
    }

    /**
     * Sends MulticastEvent to proxy
     *
     * @param intent Intent received
     */
    void sendMulticastEvent(Intent intent) {
        logger.info("Received multicast message from zirk");
        String serviceIdKEY = "zirkId";
        String addressKEY = "address";
        String mEventMsgKEY = "multicastEvent";

        final String serviceIdAsString = intent.getStringExtra(serviceIdKEY);
        final String addressAsString = intent.getStringExtra(addressKEY);
        final String mEventMsg = intent.getStringExtra(mEventMsgKEY);

        // Validate intent properties
        if (BezirkValidatorUtility.checkForString(serviceIdAsString) && BezirkValidatorUtility.checkForString(addressAsString) && BezirkValidatorUtility.checkForString(mEventMsg)) {
            final Gson gson = new Gson();
            final BezirkZirkId serviceId = gson.fromJson(serviceIdAsString, BezirkZirkId.class);
            if (BezirkValidatorUtility.checkBezirkZirkId(serviceId)) {
                final RecipientSelector recipientSelector = RecipientSelector.fromJson(addressAsString);
                logger.debug("Sending multicast event from zirk: " + serviceIdAsString);
                proxy.sendMulticastEvent(serviceId, recipientSelector, mEventMsg);
            } else {
                logger.error("trying to send multicast message with Null zirkId");

            }
        } else {

            logger.error(" Invalid arguments received to send multicast Event");
        }
    }

    /**
     * Sends UnicastEvent to proxy
     *
     * @param intent Intent received
     */
    void sendUnicastEvent(Intent intent) {
        logger.info("Received unicast message from zirk");
        String serviceIdKEY = "zirkId";
        String sepKEY = "receiverSep";
        String uEventMsgKEY = "eventMsg";

        final String serviceIdAsString = intent.getStringExtra(serviceIdKEY);
        final String sepAsString = intent.getStringExtra(sepKEY);
        final String msg = intent.getStringExtra(uEventMsgKEY);
        if (BezirkValidatorUtility.checkForString(serviceIdAsString) && BezirkValidatorUtility.checkForString(sepAsString) && BezirkValidatorUtility.checkForString(msg)) {
            final Gson gson = new Gson();
            final BezirkZirkId serviceId = gson.fromJson(serviceIdAsString, BezirkZirkId.class);
            final BezirkZirkEndPoint serviceEndPoint = gson.fromJson(sepAsString, BezirkZirkEndPoint.class);
            if (BezirkValidatorUtility.checkBezirkZirkId(serviceId) && BezirkValidatorUtility.checkBezirkZirkEndPoint(serviceEndPoint)) {
                proxy.sendUnicastEvent(serviceId, serviceEndPoint, msg);
            } else {
                logger.error("Check unicast parameters");
            }
        } else {
            logger.error(" Invalid arguments received to send Unicast Event");

        }
    }

    /**
     * Sets zirk location via proxy
     *
     * @param intent Intent received
     */
    void setLocation(Intent intent) {
        String sid = (String) intent.getExtras().get("zirkId");
        String location = (String) intent.getExtras().get("locationData");
        logger.info("Received location " + location + " from zirk");

        if (BezirkValidatorUtility.checkForString(sid) && BezirkValidatorUtility.checkForString(location)) {
            BezirkZirkId serviceId = new Gson().fromJson(sid, BezirkZirkId.class);
            Location loc = new Gson().fromJson(location, Location.class);
            if (BezirkValidatorUtility.checkBezirkZirkId(serviceId)) {
                proxy.setLocation(serviceId, loc);
            }
        } else {
            logger.error("Invalid parameters for location");
        }
    }


}