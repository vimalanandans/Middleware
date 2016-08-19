package com.bezirk.streaming;

import com.bezirk.comms.Comms;
import com.bezirk.control.messages.ControlLedger;
import com.bezirk.control.messages.streaming.StreamRequest;
import com.bezirk.control.messages.streaming.StreamResponse;
import com.bezirk.networking.NetworkManager;
import com.bezirk.pubsubbroker.PubSubEventReceiver;
import com.bezirk.sphere.api.SphereSecurity;
import com.bezirk.streaming.control.Objects.StreamRecord;
import com.bezirk.streaming.store.StreamStore;
import com.bezirk.streaming.threads.StreamReceivingThread;
import com.bezirk.util.ValidatorUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * This handler handles the StreamRequests and Responses and places them in
 * appropriate processing threads.
 */
final class BezirkStreamHandler {
    private static final Logger logger = LoggerFactory
            .getLogger(BezirkStreamHandler.class);

    /*private String downloadPath;*/
    private ExecutorService receiveStreamExecutor;
    private StreamManager streamManager = null;
    private final NetworkManager networkManager;

    BezirkStreamHandler(/*String downloadPath,*/ ExecutorService receiveStreamExecutor, StreamManager streamManager, NetworkManager networkManager){
       /* this.downloadPath = downloadPath;*/
        this.receiveStreamExecutor = receiveStreamExecutor;
        this.streamManager = streamManager;
        this.networkManager = networkManager;
    }

    /**
     * The assumption is that the ControlReceiverThread has already checked for
     * validity of StreamRequest.
     */
    boolean handleStreamRequest(final StreamRequest streamRequest, final Comms comms,
                                final PortFactory portFactory, final StreamStore streamStore,
                                final PubSubEventReceiver pubSubReceiver/*, final SphereSecurity sphereSecurity*/) {

        // Check if the request is duplicate
        StreamRecord.StreamRecordStatus status = StreamRecord.StreamRecordStatus.ADDRESSED;
        int assignedPort;

        //fixme this has to be handled by streamiD or the streamKey.. This has to be the uniqueKey with streamId also appended. and make a syncronized block too
        if (streamStore.checkStreamRequestForDuplicate(streamRequest.getUniqueKey())) {
            assignedPort = streamStore.getAssignedPort(streamRequest.getUniqueKey());
        } else {
            assignedPort = portFactory.getPort(streamRequest.getUniqueKey());
            if (-1 == assignedPort) {
                status = StreamRecord.StreamRecordStatus.BUSY;
            } else {
                status = StreamRecord.StreamRecordStatus.READY;

                StreamReceivingThread streamReceivingThread =new StreamReceivingThread(assignedPort, /*downloadPath,*/
                        streamRequest, portFactory, pubSubReceiver, /*sphereSecurity,*/ streamManager);
                Future receiveStreamFuture  = receiveStreamExecutor.submit(new Thread(streamReceivingThread));
                streamManager.addRefToActiveStream(streamRequest.getUniqueKey(), receiveStreamFuture);
            }
        }

        logger.debug("<-Sender->" + streamRequest.getSender().device
                + streamRequest.getSender().zirkId);
        logger.debug("<-recipient->" + streamRequest.getRecipient().device
                + streamRequest.getRecipient().zirkId);

        //send device Ip
        String streamIp = networkManager.getLocalInet().getHostAddress();

        StreamResponse streamResponse = new StreamResponse(
                streamRequest.getRecipient(), streamRequest.getSender(),
                streamRequest.getSphereId(), streamRequest.getUniqueKey(), status, streamIp, assignedPort);
        ControlLedger tcStrmRespMessage = new ControlLedger();
        tcStrmRespMessage.setMessage(streamResponse);
        tcStrmRespMessage.setSphereId(streamRequest.getSphereId());
        tcStrmRespMessage.setSerializedMessage(streamResponse.serialize());

        if (comms != null) {

            comms.sendMessage(tcStrmRespMessage);
        }

        return true;
    }

    boolean handleStreamResponse(final StreamResponse streamResponse,
                                 final MessageQueue streamQueue, final StreamStore streamStore) {
        logger.info("RECEIVED STREAM-RESPONSE");

        StreamRecord streamRecord = streamStore.popStreamRecord(streamResponse
                .getUniqueKey());
        if (null == streamRecord) {
            logger.debug("No StreamRecord for this Response or the StreamDescriptor is already addressed");
            return false;
        }
        streamRecord.setSphereId(streamResponse.getSphereId());
        streamRecord.setStreamRecordStatus(streamResponse.status);

        streamRecord.setRecipientIP(streamResponse.streamIp);

        logger.info("recipient key = " + streamResponse.getUniqueKey() + " rec IP = " + streamRecord.getRecipientIP() + "sender device " + streamResponse.getSender().device);
        //quickfix to test: remove it later.
        /*List quickFix_keys = Arrays.asList(streamResponse.getUniqueKey().split(":"));

        if(quickFix_keys.size() > 0) {
            streamRecord.recipientIP = (String) quickFix_keys.get(0);
            logger.info("recipient IP"+streamResponse.getUniqueKey()+" rec = "+ streamRecord.recipientIP);
        }*/


        streamRecord.setRecipientPort(streamResponse.streamPort);

        if (ValidatorUtility.isObjectNotNull(streamQueue)) {
            streamQueue.addToQueue(streamRecord);
        }

        return true;
    }

}
