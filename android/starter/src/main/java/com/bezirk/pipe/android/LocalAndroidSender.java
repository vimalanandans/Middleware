package com.bezirk.pipe.android;

import android.app.Service;

import com.bezirk.commons.BezirkCompManager;
import com.bezirk.control.messages.pipes.PipeHeader;
import com.bezirk.control.messages.pipes.PipeMulticastHeader;
import com.bezirk.control.messages.pipes.PipeUnicastHeader;
import com.bezirk.messagehandler.ZirkMessageHandler;
import com.bezirk.messagehandler.StreamIncomingMessage;
import com.bezirk.pipe.core.LocalUhuSender;
import com.bezirk.proxy.android.ProxyforServices;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.proxy.api.impl.BezirkZirkId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Created by wya1pi on 8/19/14.
 */
public class LocalAndroidSender implements LocalUhuSender {


    // TODO: we need a slightly cleaner way to get an instance of the proxy here (ASW)
    private final Service uhuService = ProxyforServices.getServiceInstance();
    private final ProxyforServices proxy = new ProxyforServices(uhuService);

    private static final Logger log = LoggerFactory.getLogger(LocalAndroidSender.class);

    @Override
    public void invokeReceive(PipeHeader pipeHeader, String serializedEvent) {
        log.info("Invoking uhu receive for event:  " + serializedEvent);

        // TODO: how to set an actual zirk ID??? We're just using the requesting zirk for now :(
        BezirkZirkId serviceId = pipeHeader.getSenderSEP().getBezirkZirkId();

        if (pipeHeader instanceof PipeUnicastHeader) {
            log.error("Unicast receive not implemented yet");
            PipeUnicastHeader pipeUnicastHeader = (PipeUnicastHeader) pipeHeader;

            // Send the reply directly to the recipient
            proxy.sendUnicastEvent(serviceId, pipeUnicastHeader.getRecipient(), serializedEvent);
        } else if (pipeHeader instanceof PipeMulticastHeader) {
            PipeMulticastHeader pipeMulticastHeader = (PipeMulticastHeader) pipeHeader;

            // This sends to services on this device and local nework
            proxy.sendMulticastEvent(serviceId, pipeMulticastHeader.getAddress(), serializedEvent);
        } else {
            log.error("unknown header type: " + pipeHeader.getClass().getSimpleName());
        }
    }

    @Override
    public void invokeIncoming(PipeHeader pipeHeader, String serializedStream, String path) {
        log.info("invokeIncoming called for: " + serializedStream);

        // TODO: we need to pass a unicast header into this method
        PipeMulticastHeader pipeMulticastHeader = (PipeMulticastHeader) pipeHeader;
        log.info("invokeIncoming called with header: " + pipeMulticastHeader.serialize());

        // TODO: how to set an actual zirk ID??? We're just using the requesting zirk for now :(
        BezirkZirkEndPoint senderSEP = pipeMulticastHeader.getSenderSEP();
        if (senderSEP == null) {
            log.error("SenderSEP is null. Can't invoke incoming");
            return;
        }
        BezirkZirkId serviceId = senderSEP.getBezirkZirkId();
        // TODO: how to create a stream id??
        short streamid = Short.MAX_VALUE;

        ZirkMessageHandler callback = BezirkCompManager.getplatformSpecificCallback();
        StreamIncomingMessage msg = new StreamIncomingMessage(
                serviceId, pipeHeader.getTopic(), serializedStream,
                new File(path), streamid, senderSEP);

        log.info("calling callback.onIncomingStream()");
        callback.onIncomingStream(msg);
    }
}
