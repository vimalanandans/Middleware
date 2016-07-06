package com.bezirk.callback.pc;

import com.bezirk.messagehandler.DiscoveryIncomingMessage;
import com.bezirk.messagehandler.EventIncomingMessage;
import com.bezirk.messagehandler.PipeRequestIncomingMessage;
import com.bezirk.messagehandler.ZirkMessageHandler;
import com.bezirk.messagehandler.StreamIncomingMessage;
import com.bezirk.messagehandler.StreamStatusMessage;
import com.bezirk.util.BezirkValidatorUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PC specific callback implementation that is used to give the callback to the ProxyForServices.
 */
public final class CBkForZirkPC implements ZirkMessageHandler {
    private static final Logger logger = LoggerFactory.getLogger(CBkForZirkPC.class);

    private static final String RECEIVER_NULL_ERROR = "Broadcast Receiver For Zirk is null, cannot give callback";
    private final BroadcastReceiver brForService;

    public CBkForZirkPC(BroadcastReceiver brForService) {
        this.brForService = brForService;
    }

    @Override
    public void onIncomingEvent(EventIncomingMessage eventIncomingMessage) {
        if (BezirkValidatorUtility.isObjectNotNull(brForService)) {
            brForService.onReceive(eventIncomingMessage);
        } else {
            logger.debug(RECEIVER_NULL_ERROR);
        }
    }


    @Override
    public void onIncomingStream(StreamIncomingMessage streamIncomingMessage) {
        if (BezirkValidatorUtility.isObjectNotNull(brForService)) {
            brForService.onReceive(streamIncomingMessage);
        } else {

            logger.debug(RECEIVER_NULL_ERROR);
        }
    }

/*  Define   
 * @Override
    public void fireMulticastStream(
            MulticastCallbackMessage multicastStreamCallbackMessage) {

        if (SignalingFactory
                .checkClassExists("com.bosch.upa.uhu.rtc.streaming.VideoChat")) {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    SignalingFactory
                            .getUIChatInstance("com.bosch.upa.uhu.rtc.streaming.VideoChat");
                }
            });
        }
    }*/

    @Override
    public void onStreamStatus(StreamStatusMessage streamStatusMessage) {
        if (BezirkValidatorUtility.isObjectNotNull(brForService)) {
            brForService.onReceive(streamStatusMessage);
        } else {
            logger.debug(RECEIVER_NULL_ERROR);
        }
    }

    @Override
    public void onDiscoveryIncomingMessage(
            DiscoveryIncomingMessage discoveryCallback) {
        if (BezirkValidatorUtility.isObjectNotNull(brForService)) {
            brForService.onReceive(discoveryCallback);
        } else {
            logger.debug(RECEIVER_NULL_ERROR);
        }

    }

    @Override
    public void onPipeApprovedMessage(PipeRequestIncomingMessage pipeMsg) {
        throw new RuntimeException(
                "Method not implemented on PC yet: CBkForZirkPC.firePipeApprovedCallBack() ");
    }


}