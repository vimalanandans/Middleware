package com.bezirk.streaming;

import com.bezirk.comms.Comms;
import com.bezirk.comms.CommsConfigurations;
import com.bezirk.comms.CtrlMsgReceiver;
import com.bezirk.comms.MessageQueue;
import com.bezirk.control.messages.ControlMessage;
import com.bezirk.control.messages.Ledger;
import com.bezirk.control.messages.streaming.StreamRequest;
import com.bezirk.control.messages.streaming.StreamResponse;
import com.bezirk.control.messages.streaming.rtc.RTCControlMessage;
import com.bezirk.pubsubbroker.PubSubEventReceiver;
import com.bezirk.sphere.api.SphereSecurity;
import com.bezirk.streaming.control.Objects.StreamRecord;
import com.bezirk.streaming.port.StreamPortFactory;
import com.bezirk.streaming.rtc.Signaling;
import com.bezirk.streaming.rtc.SignalingFactory;
import com.bezirk.streaming.store.StreamStore;
import com.bezirk.streaming.threads.StreamQueueProcessor;
import com.bezirk.util.ValidatorUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * StreamManager manages all queues,sockets and threads related to streaming. It also
 * includes the StreamControlReceiver which process the stream request and stream responses.
 */
public class StreamManager implements Streaming {
    private static final Logger logger = LoggerFactory.getLogger(StreamManager.class);

    private final StreamCtrlReceiver ctrlReceiver = new StreamCtrlReceiver();
    private SphereSecurity sphereForSadl = null;
    private MessageQueue streamingMessageQueue = null;
    private StreamQueueProcessor streamQueueProcessor = null;
    private Thread sStreamingThread = null;
    private BezirkStreamHandler bezirkStreamHandler = null;
    private PortFactory portFactory;
    //private CommsMessageDispatcher msgDispatcher;
    private Comms comms = null;

    private StreamStore streamStore = null;

    private PubSubEventReceiver sadlReceiver = null;

    public StreamManager(Comms comms, PubSubEventReceiver sadlReceiver) {

        if (ValidatorUtility.isObjectNotNull(comms)

                && ValidatorUtility.isObjectNotNull(sadlReceiver)) {
            this.comms = comms;
            this.sadlReceiver = sadlReceiver;
            bezirkStreamHandler = new BezirkStreamHandler();
        } else {
            logger.error("Unable to initialize StreamManager. Please ensure ControlSenderQueue, " +
                    "CommsMessageDispatcher and BezirkCallback are initialized.");
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

  /*  @Override
    public PortFactory getPortFactory() {
        return portFactory;
    }
*/
    @Override
    public boolean initStreams(Comms comms) {
        try {

            streamingMessageQueue = new MessageQueue();

            streamStore = new StreamStore();

            streamQueueProcessor = new StreamQueueProcessor(
                    streamingMessageQueue, sadlReceiver);


            portFactory = new StreamPortFactory(
                    CommsConfigurations.getSTARTING_PORT_FOR_STREAMING(), streamStore);

            if (comms == null) {

                logger.error("Unable to register message receivers as comms is not initialized.");
                return false;

            } else {

                comms.registerControlMessageReceiver(
                        ControlMessage.Discriminator.StreamRequest,
                        ctrlReceiver);

                comms.registerControlMessageReceiver(
                        ControlMessage.Discriminator.StreamResponse,
                        ctrlReceiver);

                comms.registerControlMessageReceiver(
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
    public void setSphereForSadl(SphereSecurity sphereSecurity) {

        this.sphereForSadl = sphereSecurity;
        this.streamQueueProcessor.setSphereSecurity(sphereForSadl);
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
