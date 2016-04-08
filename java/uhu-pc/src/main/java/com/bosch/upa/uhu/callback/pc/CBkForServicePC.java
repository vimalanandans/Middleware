package com.bosch.upa.uhu.callback.pc;

import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bosch.upa.uhu.messagehandler.DiscoveryIncomingMessage;
import com.bosch.upa.uhu.messagehandler.EventIncomingMessage;
import com.bosch.upa.uhu.messagehandler.PipeRequestIncomingMessage;
import com.bosch.upa.uhu.messagehandler.ServiceMessageHandler;
import com.bosch.upa.uhu.messagehandler.StreamIncomingMessage;
import com.bosch.upa.uhu.messagehandler.StreamStatusMessage;
import com.bosch.upa.uhu.streaming.rtc.SignalingFactory;
import com.bosch.upa.uhu.util.UhuValidatorUtility;

/**
 *    PC specific callback implementation that is used to give the callback to the ProxyForServices.
 */
public final class CBkForServicePC implements ServiceMessageHandler {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(CBkForServicePC.class);
    private final IBoradcastReceiver brForService;
    private static final String RECEIVERNULLERROR = "Broadcast Receiver For Service is null, cannot give callback";

    public CBkForServicePC(IBoradcastReceiver brForService) {
        this.brForService = brForService;
    }

    @Override
	public void onIncomingEvent(EventIncomingMessage eventIncomingMessage) {
        if (UhuValidatorUtility.isObjectNotNull(brForService)) {
            brForService.onReceive(eventIncomingMessage);
        } else {

            LOGGER.debug(RECEIVERNULLERROR);
        }
    }


	@Override
	public void onIncomingStream(StreamIncomingMessage streamIncomingMessage) {
        if (UhuValidatorUtility.isObjectNotNull(brForService)) {
            brForService.onReceive(streamIncomingMessage);
        } else {

            LOGGER.debug(RECEIVERNULLERROR);
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
        if (UhuValidatorUtility.isObjectNotNull(brForService)) {
            brForService.onReceive(streamStatusMessage);
        } else {

            LOGGER.debug(RECEIVERNULLERROR);
        }
    }

    @Override
	public void onDiscoveryIncomingMessage(
			DiscoveryIncomingMessage discoveryCallback) {
        if (UhuValidatorUtility.isObjectNotNull(brForService)) {
            brForService.onReceive(discoveryCallback);
        } else {

            LOGGER.debug(RECEIVERNULLERROR);
        }

    }

	@Override
	public void onPipeApprovedMessage(PipeRequestIncomingMessage pipeMsg) {
        throw new RuntimeException(
                "Method not implemented on PC yet: CBkForServicePC.firePipeApprovedCallBack() ");
    }

	

	

}
