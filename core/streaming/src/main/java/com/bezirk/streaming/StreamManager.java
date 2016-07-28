package com.bezirk.streaming;

import com.bezirk.comms.Comms;
import com.bezirk.comms.CtrlMsgReceiver;
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

    /** Streaming specific constants*/

    static int STREAM_START_PORT = 6321;
    static int STREAM_END_PORT = 6330;
    static int STREAM_PARALLEL_MAX = 5;
    static int STREAM_RETRY_COUNT = 5;

    private final StreamCtrlReceiver ctrlReceiver = new StreamCtrlReceiver();
    private MessageQueue streamingMessageQueue = null;
    private StreamQueueProcessor streamQueueProcessor = null;
    private Thread sStreamingThread = null;
    private BezirkStreamHandler bezirkStreamHandler = null;
    private PortFactory portFactory;
    private String downloadPath = null;
    private StreamStore streamStore = null;


    /***This has to be dependency injected.**/
    private Comms comms = null;
    private SphereSecurity sphereForSadl = null;
    private PubSubEventReceiver sadlReceiver = null;
    /***************/


    public StreamManager(Comms comms, PubSubEventReceiver sadlReceiver, String downloadPath) {

        if (ValidatorUtility.isObjectNotNull(comms)
                && ValidatorUtility.isObjectNotNull(sadlReceiver)) {
            this.comms = comms;
            this.sadlReceiver = sadlReceiver;
            bezirkStreamHandler = new BezirkStreamHandler(downloadPath);
        } else {
            logger.error("Unable to initialize StreamManager. Please ensure ControlSenderQueue, " +
                    "CommsMessageDispatcher and BezirkCallback are initialized.");
        }

        this.downloadPath = downloadPath;
    }

    @Override
    public boolean sendStream(String streamId) {
        StreamRecord tempStreamRecord = streamStore.popStreamRecord(streamId);
        if (null == tempStreamRecord) {
            return false;
        }
        tempStreamRecord.setStreamStatus(StreamRecord.StreamingStatus.LOCAL);
        streamingMessageQueue.addToQueue(tempStreamRecord);
        return true;

    }

    @Override
    public boolean addStreamRecordToStreamStore(String streamId, StreamRecord sRecord) {
        return streamStore.registerStreamBook(streamId, sRecord);
    }

    @Override
    public boolean startStreams() {

        try {

            streamingMessageQueue = new MessageQueue();

            streamStore = new StreamStore();

            streamQueueProcessor = new StreamQueueProcessor(
                    streamingMessageQueue, sadlReceiver);


            portFactory = new StreamPortFactory(
                    STREAM_START_PORT, streamStore,STREAM_PARALLEL_MAX);

            if (comms == null) {

                logger.error("Unable to register message receivers as comms is not initialized.");
                return false;

            } else {

                ctrlReceiver.initStreamCtrlReceiver();

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


            //This has to be changed to Executors.. With a a thread submit with future.
            sStreamingThread = new Thread(streamQueueProcessor);

            if (sStreamingThread == null) {
                logger.error("unable to start the streaming thread ");
                return false;

            } else {
                sStreamingThread.start();
            }

        } catch (Exception e) {
            logger.error("Exception in initializing the streams in stream manager. ", e);
            return false;
        }
        return true;

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

    /**
     * We can here interrupt a single streaming thread...
     * @param streamId
     * @return
     */
    @Override
    public boolean interruptStream(String streamId) {
        return false;
    }

    @Override
    public void setSphereSecurityForEncryption(SphereSecurity sphereSecurity) {

        this.sphereForSadl = sphereSecurity;
        this.streamQueueProcessor.setSphereSecurity(sphereForSadl);
    }


}
