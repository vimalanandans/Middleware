package com.bosch.upa.uhu.streaming;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bosch.upa.uhu.comms.ICtrlMsgReceiver;
import com.bosch.upa.uhu.comms.IPortFactory;
import com.bosch.upa.uhu.comms.IStreaming;
import com.bosch.upa.uhu.comms.IUhuComms;
import com.bosch.upa.uhu.comms.MessageDispatcher;
import com.bosch.upa.uhu.comms.MessageQueue;
import com.bosch.upa.uhu.comms.UhuComms;
import com.bosch.upa.uhu.streaming.port.PortFactory;
import com.bosch.upa.uhu.streaming.store.StreamStore;
import com.bosch.upa.uhu.streaming.threads.StreamQueueProcessor;
import com.bosch.upa.uhu.control.messages.ControlMessage;
import com.bosch.upa.uhu.control.messages.Ledger;
import com.bosch.upa.uhu.control.messages.streaming.StreamRequest;
import com.bosch.upa.uhu.control.messages.streaming.StreamResponse;
import com.bosch.upa.uhu.control.messages.streaming.rtc.RTCControlMessage;
import com.bosch.upa.uhu.sadl.ISadlEventReceiver;
import com.bosch.upa.uhu.sphere.api.IUhuSphereForSadl;
import com.bosch.upa.uhu.streaming.control.Objects.StreamRecord;
import com.bosch.upa.uhu.streaming.rtc.ISignaling;
import com.bosch.upa.uhu.streaming.rtc.SignalingFactory;
import com.bosch.upa.uhu.util.UhuValidatorUtility;

/**
 * @author ajc6kor
 * 
 *         UhuStreamManager manages all queues,sockets and threads related to
 *         streaming. It also includes the StreamControlReceiver which process
 *         the stream request and stream responses.
 * 
 */
public class UhuStreamManager implements IStreaming {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(UhuStreamManager.class);

    private IUhuSphereForSadl sphereForSadl = null;

    private MessageQueue streamingMessageQueue = null;

    private StreamQueueProcessor streamQueueProcessor = null;

    private Thread sStreamingThread = null;

    private UhuStreamHandler uhuStreamHandler = null;

    private IPortFactory portFactory;

    private MessageDispatcher msgDispatcher;

    private final StreamCtrlReceiver ctrlReceiver = new StreamCtrlReceiver();

    private IUhuComms comms = null;

    private StreamStore streamStore = null;

    private ISadlEventReceiver sadlReceiver = null;

    class StreamCtrlReceiver implements ICtrlMsgReceiver {

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
                LOGGER.debug("Real Time Stream Message Received");
                processRTCMessage(serializedMsg);
                break;
            default:
                LOGGER.error("Unknown Stream message type.");
                break;
            }

            return true;
        }

        private void processStreamResponse(String serializedMsg) {
            LOGGER.debug("Stream Response Received");
            try {

                final StreamResponse streamResponse = ControlMessage
                        .deserialize(serializedMsg, StreamResponse.class);
                uhuStreamHandler.handleStreamResponse(streamResponse,
                        streamingMessageQueue, streamStore);

            } catch (Exception e) {
                LOGGER.error(
                        "Something Wrong in processing Stream Request, Removing Message from Queue",
                        e);
            }
        }

        private void processStreamRequest(String serializedMsg) {
            LOGGER.debug("Stream Request Received");
            try {

                final StreamRequest streamRequest = ControlMessage.deserialize(
                        serializedMsg, StreamRequest.class);
                uhuStreamHandler.handleStreamRequest(streamRequest,
                        comms, portFactory,
                        streamStore, sadlReceiver,sphereForSadl);

            } catch (Exception e) {
                LOGGER.error(
                        "Something Wrong in processing Stream Request, Removing Message from Queue",
                        e);
            }
        }

        private void processRTCMessage(String serializedMsg) {
            ISignaling signaling = null;
            if (SignalingFactory.getSignalingInstance() instanceof ISignaling) {
                signaling = (ISignaling) SignalingFactory
                        .getSignalingInstance();
            }
            if (signaling == null) {

                LOGGER.error("Feature not enabled.");
            } else {
                final RTCControlMessage rtcCtrlMsg = ControlMessage
                        .deserialize(serializedMsg, RTCControlMessage.class);
                signaling.receiveControlMessage(rtcCtrlMsg);
            }
        }
    }

    public UhuStreamManager(IUhuComms comms,
                            MessageDispatcher msgDispatcher, ISadlEventReceiver sadlReceiver) {

        if (UhuValidatorUtility.isObjectNotNull(comms)
                && UhuValidatorUtility.isObjectNotNull(msgDispatcher)
                && UhuValidatorUtility.isObjectNotNull(sadlReceiver)) {
            this.comms = comms;
            this.msgDispatcher = msgDispatcher;
            this.sadlReceiver = sadlReceiver;
            uhuStreamHandler = new UhuStreamHandler();
        } else {

            LOGGER.error("Unable to initialize UhuStreamManager. Please ensure ControlSenderQueue, MessageDispatcher and UhuCallback are initialized.");
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

    /** send the Stream ledger message */
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
    public IPortFactory getPortFactory() {
        return portFactory;
    }

    @Override
    public boolean initStreams() {

        try {

            streamingMessageQueue = new MessageQueue();

            streamStore = new StreamStore();

            streamQueueProcessor = new StreamQueueProcessor(
                    streamingMessageQueue, sadlReceiver);



            portFactory = new PortFactory(
                    UhuComms.getSTARTING_PORT_FOR_STREAMING(), streamStore);

            if (msgDispatcher == null) {

                LOGGER.error("Unable to register message receivers as messagedispatcher is not initialized.");
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

            LOGGER.error(
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
            LOGGER.error("unable to start the streaming thread ");
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
    public void setSphereForSadl(IUhuSphereForSadl uhuSphere) {

        this.sphereForSadl = uhuSphere;
        this.streamQueueProcessor.setSphereForSadl(sphereForSadl);
    }

}
