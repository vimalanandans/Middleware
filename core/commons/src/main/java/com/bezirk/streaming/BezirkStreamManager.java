package com.bezirk.streaming;

import com.bezirk.comms.BezirkComms;
import com.bezirk.comms.BezirkCommunications;
import com.bezirk.comms.CtrlMsgReceiver;
import com.bezirk.comms.PortFactory;
import com.bezirk.comms.Streaming;
import com.bezirk.comms.BezirkMessageDispatcher;
import com.bezirk.comms.MessageQueue;
import com.bezirk.control.messages.ControlMessage;
import com.bezirk.control.messages.Ledger;
import com.bezirk.control.messages.streaming.StreamRequest;
import com.bezirk.control.messages.streaming.StreamResponse;
import com.bezirk.control.messages.streaming.rtc.RTCControlMessage;
import com.bezirk.pubsubbroker.SadlEventReceiver;
import com.bezirk.sphere.api.BezirkSphereForSadl;
import com.bezirk.streaming.control.Objects.StreamRecord;
import com.bezirk.streaming.rtc.Signaling;
import com.bezirk.streaming.rtc.SignalingFactory;
import com.bezirk.streaming.store.StreamStore;
import com.bezirk.streaming.threads.StreamQueueProcessor;
import com.bezirk.util.BezirkValidatorUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * BezirkStreamManager manages all queues,sockets and threads related to streaming. It also
 * includes the StreamControlReceiver which process the stream request and stream responses.
 */
public class BezirkStreamManager implements Streaming {
    private static final Logger logger = LoggerFactory.getLogger(BezirkStreamManager.class);

    private final StreamCtrlReceiver ctrlReceiver = new StreamCtrlReceiver();
    private BezirkSphereForSadl sphereForSadl = null;
    private MessageQueue streamingMessageQueue = null;
    private StreamQueueProcessor streamQueueProcessor = null;
    private Thread sStreamingThread = null;
    private BezirkStreamHandler bezirkStreamHandler = null;
    private PortFactory portFactory;
    private BezirkMessageDispatcher msgDispatcher;
    private BezirkComms comms = null;

    private StreamStore streamStore = null;

    private SadlEventReceiver sadlReceiver = null;

    public BezirkStreamManager(BezirkComms comms,
                               BezirkMessageDispatcher msgDispatcher, SadlEventReceiver sadlReceiver) {

        if (BezirkValidatorUtility.isObjectNotNull(comms)
                && BezirkValidatorUtility.isObjectNotNull(msgDispatcher)
                && BezirkValidatorUtility.isObjectNotNull(sadlReceiver)) {
            this.comms = comms;
            this.msgDispatcher = msgDispatcher;
            this.sadlReceiver = sadlReceiver;
            bezirkStreamHandler = new BezirkStreamHandler();
        } else {
            logger.error("Unable to initialize BezirkStreamManager. Please ensure ControlSenderQueue, " +
                    "BezirkMessageDispatcher and BezirkCallback are initialized.");
        }

    }

    /**
     * This is the message queue for stream requests on the receiver side
     *
     * @return MessageQueue
     */
    public MessageQueue getStreamingMessageQueue() {
        return streamingMessageQueue;
    }

    /**
     * This is the message queue for stream requests on the sender side
     *
     * @param streamingMessageQueue
     */
    public void setStreamingMessageQueue(MessageQueue streamingMessageQueue) {
        this.streamingMessageQueue = streamingMessageQueue;
    }

    /**
     * send the Stream ledger message
     */
    @Override
    public boolean sendStreamMessage(Ledger message) {

        streamingMessageQueue.addToQueue(message);

        return true;
    }

    @Override
    public boolean sendStream(String uniqueKey) {
        StreamRecord tempStreamRecord = streamStore.popStreamRecord(uniqueKey);
        if (null == tempStreamRecord) {
            return false;
        }
        tempStreamRecord.streamStatus = StreamRecord.StreamingStatus.LOCAL;
        return sendStreamMessage(tempStreamRecord);

    }

    @Override
    public boolean registerStreamBook(String key, StreamRecord sRecord) {
        return streamStore.registerStreamBook(key, sRecord);
    }

    @Override
    public PortFactory getPortFactory() {
        return portFactory;
    }

    @Override
    public boolean initStreams() {

        try {

            streamingMessageQueue = new MessageQueue();

            streamStore = new StreamStore();

            streamQueueProcessor = new StreamQueueProcessor(
                    streamingMessageQueue, sadlReceiver);


            portFactory = new com.bezirk.streaming.port.PortFactory(
                    BezirkCommunications.getSTARTING_PORT_FOR_STREAMING(), streamStore);

            if (msgDispatcher == null) {

                logger.error("Unable to register message receivers as MessageDispatcher is not initialized.");
                return false;

            } else {

                msgDispatcher.registerControlMessageReceiver(
                        ControlMessage.Discriminator.StreamRequest,
                        ctrlReceiver);

                msgDispatcher.registerControlMessageReceiver(
                        ControlMessage.Discriminator.StreamResponse,
                        ctrlReceiver);

                msgDispatcher.registerControlMessageReceiver(
                        ControlMessage.Discriminator.RTCControlMessage,
                        ctrlReceiver);
            }

        } catch (Exception e) {

            logger.error(
                    "Exception in initializing the streams in stream manager. ",
                    e);
            return false;
        }

        return true;
    }

    @Override
    public boolean startStreams() {

        sStreamingThread = new Thread(streamQueueProcessor);

        if (sStreamingThread == null) {
            logger.error("unable to start the streaming thread ");
            return false;

        } else {

            sStreamingThread.start();
            return true;
        }
    }

    @Override
    public boolean endStreams() {

        if (sStreamingThread == null) {

            return false;

        } else {
            sStreamingThread.interrupt();
            return true;

        }
    }

    @Override
    public void setSphereForSadl(BezirkSphereForSadl bezirkSphere) {

        this.sphereForSadl = bezirkSphere;
        this.streamQueueProcessor.setSphereForSadl(sphereForSadl);
    }

    class StreamCtrlReceiver implements CtrlMsgReceiver {

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
                    logger.debug("Real Time Stream Message Received");
                    processRTCMessage(serializedMsg);
                    break;
                default:
                    logger.error("Unknown Stream message type.");
                    break;
            }

            return true;
        }

        private void processStreamResponse(String serializedMsg) {
            logger.debug("Stream Response Received");
            try {

                final StreamResponse streamResponse = ControlMessage
                        .deserialize(serializedMsg, StreamResponse.class);
                bezirkStreamHandler.handleStreamResponse(streamResponse,
                        streamingMessageQueue, streamStore);

            } catch (Exception e) {
                logger.error(
                        "Something Wrong in processing Stream Request, Removing Message from Queue",
                        e);
            }
        }

        private void processStreamRequest(String serializedMsg) {
            logger.debug("Stream Request Received");
            try {

                final StreamRequest streamRequest = ControlMessage.deserialize(
                        serializedMsg, StreamRequest.class);
                bezirkStreamHandler.handleStreamRequest(streamRequest,
                        comms, portFactory,
                        streamStore, sadlReceiver, sphereForSadl);

            } catch (Exception e) {
                logger.error(
                        "Something Wrong in processing Stream Request, Removing Message from Queue",
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

}
