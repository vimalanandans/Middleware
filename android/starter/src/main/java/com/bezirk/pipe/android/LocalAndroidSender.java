package com.bezirk.pipe.android;

import android.app.Service;

import com.bezirk.commons.UhuCompManager;
import com.bezirk.control.messages.pipes.PipeHeader;
import com.bezirk.control.messages.pipes.PipeMulticastHeader;
import com.bezirk.control.messages.pipes.PipeUnicastHeader;
import com.bezirk.messagehandler.ServiceMessageHandler;
import com.bezirk.messagehandler.StreamIncomingMessage;
import com.bezirk.pipe.core.LocalUhuSender;
import com.bezirk.proxy.android.ProxyforServices;
import com.bezirk.proxy.api.impl.UhuServiceEndPoint;
import com.bezirk.proxy.api.impl.UhuServiceId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by wya1pi on 8/19/14.
 */
public class LocalAndroidSender implements LocalUhuSender {


    // TODO: we need a slightly cleaner way to get an instance of the proxy here (ASW)
    private final Service uhuService = ProxyforServices.getServiceInstance();
    private final ProxyforServices proxy = new ProxyforServices(uhuService);

    private final Logger log = LoggerFactory.getLogger(LocalAndroidSender.class);

    @Override
    public void invokeReceive(PipeHeader pipeHeader, String serializedEvent) {
        log.info("Invoking uhu receive for event:  " + serializedEvent);

        // TODO: how to set an actual service ID??? We're just using the requesting service for now :(
        UhuServiceId serviceId = pipeHeader.getSenderSEP().getUhuServiceId();

        if (pipeHeader instanceof PipeUnicastHeader) {
            log.error("Unicast receive not implemented yet");
            PipeUnicastHeader pipeUnicastHeader = (PipeUnicastHeader) pipeHeader;

            // Send the reply directly to the recipient
            proxy.sendUnicastEvent(serviceId, pipeUnicastHeader.getRecipient(), serializedEvent);
        }
        else if (pipeHeader instanceof PipeMulticastHeader) {
            PipeMulticastHeader pipeMulticastHeader = (PipeMulticastHeader) pipeHeader;

            // This sends to services on this device and local nework
            proxy.sendMulticastEvent(serviceId, pipeMulticastHeader.getAddress(), serializedEvent);
        }
        else {
            log.error("unknown header type: " + pipeHeader.getClass().getSimpleName());
        }
    }

    @Override
    public void invokeIncoming(PipeHeader pipeHeader, String serializedStream, String path) {
        log.info("invokeIncoming called for: " + serializedStream);

        // TODO: we need to pass a unicast header into this method
        PipeMulticastHeader pipeMulticastHeader = (PipeMulticastHeader) pipeHeader;
        log.info("invokeIncoming called with header: " + pipeMulticastHeader.serialize());

        // TODO: how to set an actual service ID??? We're just using the requesting service for now :(
        UhuServiceEndPoint senderSEP = pipeMulticastHeader.getSenderSEP();
        if (senderSEP == null) {
            log.error("SenderSEP is null. Can't invoke incoming");
            return;
        }
        UhuServiceId serviceId = senderSEP.getUhuServiceId();
        // TODO: how to create a stream id??
        short streamid = Short.MAX_VALUE;

        ServiceMessageHandler callback = UhuCompManager.getplatformSpecificCallback();
        StreamIncomingMessage msg = new StreamIncomingMessage(
                serviceId, pipeHeader.getTopic(), serializedStream,
                path, streamid, senderSEP);

        log.info("calling callback.onIncomingStream()");
        callback.onIncomingStream(msg);
    }
}