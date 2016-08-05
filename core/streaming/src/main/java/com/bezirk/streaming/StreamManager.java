package com.bezirk.streaming;

import com.bezirk.actions.SendFileStreamAction;
import com.bezirk.actions.StreamAction;
import com.bezirk.comms.Comms;
import com.bezirk.control.messages.ControlLedger;
import com.bezirk.control.messages.ControlMessage;
import com.bezirk.control.messages.streaming.StreamRequest;
import com.bezirk.networking.NetworkManager;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.pubsubbroker.PubSubEventReceiver;
import com.bezirk.sphere.api.SphereSecurity;
import com.bezirk.streaming.control.Objects.StreamRecord;
import com.bezirk.streaming.port.StreamPortFactory;
import com.bezirk.streaming.store.StreamStore;
import com.bezirk.streaming.threads.StreamQueueProcessor;
import com.bezirk.util.ValidatorUtility;
import com.google.gson.Gson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * StreamManager manages all queues,sockets and threads related to streaming. It also
 * includes the StreamControlReceiver which process the stream request and stream responses.
 */
public class StreamManager implements Streaming, ActiveStream {
    private static final Logger logger = LoggerFactory.getLogger(StreamManager.class);

    /** Streaming specific constants*/

    static int STREAM_START_PORT = 6321;
    static int STREAM_END_PORT = 6330;
    static int STREAM_PARALLEL_MAX = 5;
    static int STREAM_RETRY_COUNT = 5;
    static final int THREAD_SIZE = 10;

    private final StreamCtrlReceiver ctrlReceiver = new StreamCtrlReceiver();
    private MessageQueue streamingMessageQueue = null;
    private StreamQueueProcessor sendStreamQueueProcessor = null;
    private BezirkStreamHandler bezirkStreamHandler = null;
    private PortFactory portFactory;
    private String downloadPath = null;
    private StreamStore streamStore = null;


    /***This has to be dependency injected.**/
    private Comms comms = null;
    private SphereSecurity sphereSecurity = null;
    private PubSubEventReceiver sadlReceiver = null;
    /***************/

    // creates thread pool with one thead
    private ExecutorService streamQueueExecutor = null;

    // ExecutorService for sending stream
    private ExecutorService streamProcessExecutor = null;

    //running stream map is used to end the future task when cient wants to intrupt them.
    private Map<String, Future> activeStreamMap = new HashMap<String, Future>();

    private NetworkManager networkManager = null;


    public StreamManager(Comms comms, PubSubEventReceiver sadlReceiver, String downloadPath, NetworkManager networkManager) {

        if (ValidatorUtility.isObjectNotNull(comms)
                && ValidatorUtility.isObjectNotNull(sadlReceiver)) {
            this.comms = comms;
            this.sadlReceiver = sadlReceiver;
            this.networkManager = networkManager;

            // ExecutorService for processing the straem massage queue.
            streamQueueExecutor = Executors.newSingleThreadExecutor();

            // ExecutorService for sending stream
            streamProcessExecutor = Executors.newFixedThreadPool(THREAD_SIZE);
            bezirkStreamHandler = new BezirkStreamHandler(downloadPath, streamProcessExecutor,this, networkManager);
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
        tempStreamRecord.setStreamRecordStatus(StreamRecord.StreamRecordStatus.LOCAL);
        streamingMessageQueue.addToQueue(tempStreamRecord);
        return true;

    }

    @Override
    public boolean processStreamRecord(SendFileStreamAction streamAction, Iterable<String> sphereList) {

        try {
            //prepare StreamRecord is the object which is saved in the streamStore of Streaming module and sent to receiver as a Control Messgae
            final StreamRecord streamRecord = prepareStreamRecord(streamAction);

            //store the StreamRecord in the StreamStore.
            boolean streamStoreStatus = streamStore.storeStreamRecord(streamRecord);
            if (!streamStoreStatus) {
                logger.error("Cannot Register StreamDescriptor, CtrlMsgId is already present in StreamBook");
                return false;
            }

            //Send the send the message to receiver
            sendStreamMessageToReceivers(sphereList, streamRecord);
        } catch (Exception e) {
            logger.error("Cant get the SEP of the sender", e);
            return false;
        }

        return true;
    }

    StreamRecord prepareStreamRecord(SendFileStreamAction streamAction) {
        final StreamRecord streamRecord = new StreamRecord();
        final BezirkZirkEndPoint receiver = (BezirkZirkEndPoint) streamAction.getRecipient();
        final BezirkZirkEndPoint senderSEP = networkManager.getServiceEndPoint(streamAction.getZirkId());
        
        //// FIXME: 8/4/2016 Punith.. device and ZirkID. is it required ??
        final String streamRequestKey = senderSEP.device + ":" + senderSEP.getBezirkZirkId().getZirkId() + ":"+ streamAction.getStreamId();

        streamRecord.setSenderSEP(senderSEP);
        streamRecord.setEncryptedStream(streamAction.getDescriptor().isEncrypted());
        streamRecord.setStreamRecordStatus(StreamRecord.StreamRecordStatus.PENDING);
        streamRecord.setRecipientIP(receiver.device);
        streamRecord.setRecipientPort(0);
        streamRecord.setFile(streamAction.getFile());
        streamRecord.setRecipientSEP(receiver);
        streamRecord.setSerializedStream(streamAction.getDescriptor().toJson());
        streamRecord.setStreamRequestKey(streamRequestKey);
        //streamRecord.setStreamId(streamAction.getStreamId());
        return streamRecord;
    }

    /**
     * sends the stream message to the receivers based on the sphere list and stream record recipient endpoint
     * @param listOfSphere
     * @param streamRecord
     */
    void sendStreamMessageToReceivers(Iterable<String> listOfSphere, StreamRecord streamRecord) {
        final Iterator<String> sphereIterator = listOfSphere.iterator();
        while (sphereIterator.hasNext()) {
            final String sphereId = sphereIterator.next();
            final ControlLedger tcMessage = prepareMessage(sphereId, streamRecord);
            if (ValidatorUtility.isObjectNotNull(comms)) {

                // FIXME: 8/4/2016 This has to return the status... fix this.
                comms.sendMessage(tcMessage);
            } else {
                logger.error("Comms manager not initialized");
            }
        }
    }

    /**
     * prepare the control ledger message for the streaming.
     * @param sphereId
     * @param streamRecord
     * @return
     */
    private ControlLedger prepareMessage(String sphereId, StreamRecord streamRecord) {
        final ControlLedger tcMessage = new ControlLedger();
        tcMessage.setSphereId(sphereId);

        final StreamRequest request = new StreamRequest(sphereId, streamRecord, null);
        tcMessage.setMessage(request);
        tcMessage.setSerializedMessage(new Gson().toJson(request));

        return tcMessage;
    }

    @Override
    public boolean startStreams() {

        try {

            streamingMessageQueue = new MessageQueue();

            streamStore = new StreamStore();

            sendStreamQueueProcessor = new StreamQueueProcessor(
                    streamingMessageQueue, sadlReceiver, streamProcessExecutor, this);


            portFactory = new StreamPortFactory(
                    STREAM_START_PORT, streamStore,STREAM_PARALLEL_MAX);

            if (comms == null) {

                logger.error("Unable to register message receivers as comms is not initialized.");
                return false;

            } else {

                ctrlReceiver.initStreamCtrlReceiver(bezirkStreamHandler, portFactory, comms,
                        streamStore, sadlReceiver, sphereSecurity, streamingMessageQueue);

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

            streamQueueExecutor.execute(sendStreamQueueProcessor);

        } catch (Exception e) {
            logger.error("Exception in initializing the streams in stream manager. ", e);
            return false;
        }
        return true;

    }

    @Override
    public boolean endStreams() {

        boolean endStatus = false;
        if (streamQueueExecutor == null) {
            endStatus  =  false;
        } else {
            if(!streamQueueExecutor.isTerminated()) {
                streamQueueExecutor.shutdownNow();
            }

            if(!streamProcessExecutor.isTerminated()) {
                streamProcessExecutor.shutdownNow();
            }

            endStatus = true;
        }

        return endStatus;
    }

    /**
     * We can here interrupt a single streaming thread...
     * @param streamKey
     * @return
     */
    @Override
    public boolean interruptStream(String streamKey) {
        Future futureTask = activeStreamMap.get(streamKey);
        if(futureTask != null && (!futureTask.isCancelled() || futureTask.isDone())){
            futureTask.cancel(true);
        }
        return true;
    }

    @Override
    public void setSphereSecurityForEncryption(SphereSecurity sphereSecurity) {

        this.sphereSecurity = sphereSecurity;
        this.sendStreamQueueProcessor.setSphereSecurity(this.sphereSecurity);
    }


    @Override
    public boolean addRefToActiveStream(String streamRequestKey, Future streamFutureTaskRef) {
        activeStreamMap.put(streamRequestKey, streamFutureTaskRef);
        return true;
    }

    @Override
    public boolean removeRefFromActiveStream(String streamRequestKey) {
        activeStreamMap.remove(streamRequestKey);
        return true;
    }

}

/**
 * This will be a package protected interface to add a reference of future
 *
 * Created by PIK6KOR on 7/28/2016.
 */
interface ActiveStream{

    boolean addRefToActiveStream(String streamRequestKey, Future streamFutureTaskRef);

    boolean removeRefFromActiveStream(String streamRequestKey);

}
