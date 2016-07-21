package com.bezirk.proxy.android;

import android.content.Intent;

import com.bezirk.proxy.ProxyServer;
import com.bezirk.proxy.messagehandler.MessageHandler;
import com.bezirk.proxy.messagehandler.StreamStatusMessage;
import com.bezirk.middleware.addressing.RecipientSelector;
import com.bezirk.middleware.addressing.Location;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.proxy.api.impl.ZirkId;
import com.bezirk.proxy.api.impl.SubscribedRole;
import com.bezirk.util.ValidatorUtility;
import com.google.gson.Gson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class AndroidProxyServer extends ProxyServer {
    private static final Logger logger = LoggerFactory.getLogger(AndroidProxyServer.class);

    private static final Gson gson = new Gson();
    private MessageHandler messageHandler;

    // TODO: If it makes sense , move it to proxy server
    public void InitProxyServerIntend(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    public void subscribeService(Intent intent) {
        logger.debug("Received subscription from zirk");

        String serviceIdAsString = intent.getStringExtra("zirkId");
        String protocolRoleAsString = intent.getStringExtra("protocol");

        if (ValidatorUtility.checkForString(serviceIdAsString) && ValidatorUtility.checkForString(protocolRoleAsString)) {
            final ZirkId serviceId = gson.fromJson(serviceIdAsString, ZirkId.class);
            final SubscribedRole subscribedRole = gson.fromJson(protocolRoleAsString, SubscribedRole.class);
            if (ValidatorUtility.checkBezirkZirkId(serviceId) && ValidatorUtility.checkProtocolRole(subscribedRole)) {
                super.subscribeService(serviceId, subscribedRole);
            } else {
                logger.error("trying to subscribe with Null zirkId/ protocolRole");
            }
        } else {
            logger.error("Error in Zirk Subscription. Check for the values being sent");
        }
    }

    public void registerService(Intent intent) {
        final String serviceIdAsString = intent.getStringExtra("zirkId");
        final String serviceName = intent.getStringExtra("serviceName");

        logger.debug("Zirk registration to Bezirk. Name : " + serviceName + " Id : " + serviceIdAsString);

        if (ValidatorUtility.checkForString(serviceIdAsString) && ValidatorUtility.checkForString(serviceName)) {
            final ZirkId serviceId = gson.fromJson(serviceIdAsString, ZirkId.class);
            if (ValidatorUtility.checkBezirkZirkId(serviceId)) {
                super.registerService(serviceId, serviceName);
            } else {
                logger.error("Trying to subscribe with null ZirkId");
            }

        } else {
            logger.error("Error in Zirk Registration. Check for the values being sent");
        }
    }

    public void unsubscribeService(Intent intent) {
        logger.debug("Received unsubscribe from zirk");

        final String serviceIdAsString = intent.getStringExtra("zirkId");
        final String protocolRoleAsString = intent.getStringExtra("protocol");
        if (ValidatorUtility.checkForString(serviceIdAsString)) {
            final ZirkId serviceId = gson.fromJson(serviceIdAsString, ZirkId.class);
            final SubscribedRole subscribedRole = gson.fromJson(protocolRoleAsString, SubscribedRole.class);
            if (ValidatorUtility.checkBezirkZirkId(serviceId)) {
                if (ValidatorUtility.isObjectNotNull(subscribedRole)) {
                    super.unsubscribe(serviceId, subscribedRole);
                } else {
                    super.unregister(serviceId);
                }

            } else {
                logger.error("trying to subscribe with Null zirkId/ protocolRole");
            }

        } else {
            logger.error("Error in Zirk Subscription. Check for the values being sent");
        }
    }

    public void sendUnicastStream(Intent intent) {
        logger.debug("------------ Received message to push the StreamDescriptor ----------------------");

        // Use a interface from component manager to find out enabled component to respond back
        final String serviceIdAsString = intent.getStringExtra("zirkId");
        final String recipientAsString = intent.getStringExtra("receiverSEP");
        final File file = new File(intent.getStringExtra("filePath"));
        final String streamAsString = intent.getStringExtra("stream");
        final short localStreamId = intent.getShortExtra("localStreamId", (short) -1);

        boolean isStreamingValid;

        if (ValidatorUtility.checkForString(serviceIdAsString, recipientAsString, streamAsString) && -1 != localStreamId) {
            final ZirkId serviceId = gson.fromJson(serviceIdAsString, ZirkId.class);
            final BezirkZirkEndPoint recipient = gson.fromJson(recipientAsString, BezirkZirkEndPoint.class);

            isStreamingValid = sendStream(file, streamAsString, localStreamId, serviceId, recipient);

        } else {
            logger.error("Invalid arguments received");
            isStreamingValid = false;
        }

        if (!isStreamingValid) {
            StreamStatusMessage streamStatusCallbackMessage = new StreamStatusMessage(gson.fromJson(serviceIdAsString, ZirkId.class), 0, localStreamId);
            messageHandler.onStreamStatus(streamStatusCallbackMessage);
        }
    }

    private boolean sendStream(File file, String streamAsString, short localStreamId, ZirkId serviceId, BezirkZirkEndPoint recipient) {
        if (ValidatorUtility.checkBezirkZirkEndPoint(recipient) && ValidatorUtility.checkBezirkZirkId(serviceId)) {
            short sendStreamStatus = super.sendStream(serviceId, recipient, streamAsString, file, localStreamId);
            if (-1 == sendStreamStatus) {
                return false;
            }
        } else {
            logger.error("Recipient SEP or BezirkZirkID is not valid ");
            return false;

        }
        return true;
    }

    public void sendMulticastEvent(Intent intent) {
        logger.debug("Received multicast message from zirk");

        final String serviceIdAsString = intent.getStringExtra("zirkId");
        final String addressAsString = intent.getStringExtra("address");
        final String mEventMsg = intent.getStringExtra("multicastEvent");
        final String eventTopic = intent.getStringExtra("topic");

        // Validate intent properties
        if (ValidatorUtility.checkForString(serviceIdAsString) &&
                ValidatorUtility.checkForString(addressAsString) &&
                ValidatorUtility.checkForString(mEventMsg)) {

            final ZirkId serviceId = gson.fromJson(serviceIdAsString, ZirkId.class);
            if (ValidatorUtility.checkBezirkZirkId(serviceId)) {
                final RecipientSelector recipientSelector = RecipientSelector.fromJson(addressAsString);
                logger.debug("Sending multicast event from zirk: " + serviceIdAsString);
                super.sendMulticastEvent(serviceId, recipientSelector, mEventMsg, eventTopic);
            } else {
                logger.error("trying to send multicast message with Null zirkId");
            }
        } else {

            logger.error("Invalid arguments received to send multicast Event");
        }
    }

    public void sendUnicastEvent(Intent intent) {
        logger.debug("Received unicast message from zirk");

        final String serviceIdAsString = intent.getStringExtra("zirkId");
        final String sepAsString = intent.getStringExtra("receiverSep");
        final String msg = intent.getStringExtra("eventMsg");
        final String eventTopic = intent.getStringExtra("topic");

        if (ValidatorUtility.checkForString(serviceIdAsString) && ValidatorUtility.checkForString(sepAsString) && ValidatorUtility.checkForString(msg)) {
            final ZirkId serviceId = gson.fromJson(serviceIdAsString, ZirkId.class);
            final BezirkZirkEndPoint serviceEndPoint = gson.fromJson(sepAsString, BezirkZirkEndPoint.class);
            if (ValidatorUtility.checkBezirkZirkId(serviceId) && ValidatorUtility.checkBezirkZirkEndPoint(serviceEndPoint)) {
                super.sendUnicastEvent(serviceId, serviceEndPoint, msg, eventTopic);

            } else {
                logger.error("Check unicast parameters");
            }
        } else {
            logger.error("Invalid arguments received to send Unicast Event");
        }
    }

    public void setLocation(Intent intent) {
        String sid = (String) intent.getExtras().get("zirkId");
        String location = (String) intent.getExtras().get("locationData");
        logger.debug("Received location " + location + " from zirk");

        if (ValidatorUtility.checkForString(sid) && ValidatorUtility.checkForString(location)) {
            ZirkId serviceId = gson.fromJson(sid, ZirkId.class);
            Location loc = gson.fromJson(location, Location.class);
            if (ValidatorUtility.checkBezirkZirkId(serviceId)) {
                super.setLocation(serviceId, loc);
            }
        } else {
            logger.error("Invalid parameters for location");
        }
    }
}