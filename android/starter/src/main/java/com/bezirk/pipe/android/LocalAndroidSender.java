package com.bezirk.pipe.android;

import android.app.Service;

import com.bezirk.commons.BezirkCompManager;
import com.bezirk.control.messages.pipes.PipeHeader;
import com.bezirk.control.messages.pipes.PipeMulticastHeader;
import com.bezirk.control.messages.pipes.PipeUnicastHeader;
import com.bezirk.messagehandler.ZirkMessageHandler;
import com.bezirk.messagehandler.StreamIncomingMessage;
import com.bezirk.pipe.core.LocalBezirkSender;
import com.bezirk.proxy.android.ProxyForZirks;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.proxy.api.impl.BezirkZirkId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class LocalAndroidSender implements LocalBezirkSender {
    private static final Logger logger = LoggerFactory.getLogger(LocalAndroidSender.class);

    // TODO: we need a slightly cleaner way to get an instance of the proxy here (ASW)
    private final Service uhuService = ProxyForZirks.getServiceInstance();
    private final ProxyForZirks proxy = new ProxyForZirks(uhuService);

    @Override
    public void invokeReceive(PipeHeader pipeHeader, String serializedEvent) {
        logger.info("Invoking bezirk receive for event:  " + serializedEvent);

        // TODO: how to set an actual zirk ID??? We're just using the requesting zirk for now :(
        BezirkZirkId serviceId = pipeHeader.getSenderSEP().getBezirkZirkId();

        if (pipeHeader instanceof PipeUnicastHeader) {
            logger.error("Unicast receive not implemented yet");
            PipeUnicastHeader pipeUnicastHeader = (PipeUnicastHeader) pipeHeader;

            // Send the reply directly to the recipient
            proxy.sendUnicastEvent(serviceId, pipeUnicastHeader.getRecipient(), serializedEvent);
        } else if (pipeHeader instanceof PipeMulticastHeader) {
            PipeMulticastHeader pipeMulticastHeader = (PipeMulticastHeader) pipeHeader;

            // This sends to services on this device and local nework
            proxy.sendMulticastEvent(serviceId, pipeMulticastHeader.getAddress(), serializedEvent);
        } else {
            logger.error("unknown header type: " + pipeHeader.getClass().getSimpleName());
        }
    }

    @Override
    public void invokeIncoming(PipeHeader pipeHeader, String serializedStream, String path) {
        logger.info("invokeIncoming called for: " + serializedStream);

        // TODO: we need to pass a unicast header into this method
        PipeMulticastHeader pipeMulticastHeader = (PipeMulticastHeader) pipeHeader;
        logger.info("invokeIncoming called with header: " + pipeMulticastHeader.serialize());

        // TODO: how to set an actual zirk ID??? We're just using the requesting zirk for now :(
        BezirkZirkEndPoint senderSEP = pipeMulticastHeader.getSenderSEP();
        if (senderSEP == null) {
            logger.error("SenderSEP is null. Can't invoke incoming");
            return;
        }
        BezirkZirkId serviceId = senderSEP.getBezirkZirkId();
        // TODO: how to create a stream id??
        short streamid = Short.MAX_VALUE;

        ZirkMessageHandler callback = BezirkCompManager.getplatformSpecificCallback();
        StreamIncomingMessage msg = new StreamIncomingMessage(
                serviceId, pipeHeader.getTopic(), serializedStream,
                new File(path), streamid, senderSEP);

        logger.info("calling callback.onIncomingStream()");
        callback.onIncomingStream(msg);
    }
}
