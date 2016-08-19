package com.bezirk.streaming;

import com.bezirk.comms.Comms;
import com.bezirk.comms.CtrlMsgReceiver;
import com.bezirk.control.messages.ControlMessage;
import com.bezirk.control.messages.streaming.StreamRequest;
import com.bezirk.control.messages.streaming.StreamResponse;
import com.bezirk.control.messages.streaming.rtc.RTCControlMessage;
import com.bezirk.pubsubbroker.PubSubEventReceiver;
import com.bezirk.streaming.rtc.Signaling;
import com.bezirk.streaming.rtc.SignalingFactory;
import com.bezirk.streaming.store.StreamStore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by PIK6KOR on 7/26/2016.
 */

public class StreamCtrlReceiver implements CtrlMsgReceiver {

    private static final Logger logger = LoggerFactory.getLogger(StreamCtrlReceiver.class);

    private BezirkStreamHandler bezirkStreamHandler = null;
    private PortFactory portFactory;
    private Comms comms = null;
    private StreamStore streamStore = null;
    private PubSubEventReceiver pubSubReceiver = null;
    /*private SphereSecurity sphereSecurity = null;*/
    private MessageQueue streamingMessageQueue = null;


    /**
     * Initialize the stream ctrl receivers.
     */
    public void initStreamCtrlReceiver(BezirkStreamHandler bezirkStreamHandler, PortFactory portFactory, Comms comms,
                                       StreamStore streamStore, PubSubEventReceiver pubSubReceiver, /*SphereSecurity sphereSecurity,*/ MessageQueue streamingMessageQueue ){
        this.bezirkStreamHandler = bezirkStreamHandler;
        this.portFactory = portFactory;
        this.comms = comms;
        this.streamStore = streamStore;
        this.pubSubReceiver = pubSubReceiver;
        /*this.sphereSecurity = sphereSecurity;*/
        this.streamingMessageQueue = streamingMessageQueue;
    }

    @Override
    public boolean processControlMessage(ControlMessage.Discriminator id,
                                         String serializedMsg) {

        switch (id) {
            case StreamRequest:
                processStreamRequest(serializedMsg);
                break;
            case StreamResponse:
                processStreamResponse(serializedMsg);
                break;
            case RTCControlMessage:
                logger.debug("Real Time StreamDescriptor Message Received");
                processRTCMessage(serializedMsg);
                break;
            default:
                logger.error("Unknown StreamDescriptor message type.");
                break;
        }

        return true;
    }

    private void processStreamResponse(String serializedMsg) {
        logger.debug("StreamDescriptor Response Received");
        try {

            final StreamResponse streamResponse = ControlMessage
                    .deserialize(serializedMsg, StreamResponse.class);
            bezirkStreamHandler.handleStreamResponse(streamResponse,
                    streamingMessageQueue, streamStore);

        } catch (Exception e) {
            logger.error(
                    "Something Wrong in processing StreamDescriptor Request, Removing Message from Queue",
                    e);
        }
    }

    private void processStreamRequest(String serializedMsg) {
        logger.debug("StreamDescriptor Request Received");
        try {

            final StreamRequest streamRequest = ControlMessage.deserialize(
                    serializedMsg, StreamRequest.class);
            bezirkStreamHandler.handleStreamRequest(streamRequest,
                    comms, portFactory,
                    streamStore, pubSubReceiver/*, sphereSecurity*/);

        } catch (Exception e) {
            logger.error(
                    "Something Wrong in processing StreamDescriptor Request, Removing Message from Queue",
                    e);
        }
    }

    private void processRTCMessage(String serializedMsg) {
        Signaling signaling = null;
        if (SignalingFactory.getSignalingInstance() instanceof Signaling) {
            signaling = (Signaling) SignalingFactory
                    .getSignalingInstance();
        }
        if (signaling == null) {

            logger.error("Feature not enabled.");
        } else {
            final RTCControlMessage rtcCtrlMsg = ControlMessage
                    .deserialize(serializedMsg, RTCControlMessage.class);
            signaling.receiveControlMessage(rtcCtrlMsg);
        }
    }
}
