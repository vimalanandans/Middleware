package com.bezirk.streaming;

import com.bezirk.comms.Comms;
import com.bezirk.comms.MessageQueue;
import com.bezirk.control.messages.ControlLedger;
import com.bezirk.control.messages.streaming.StreamRequest;
import com.bezirk.control.messages.streaming.StreamResponse;
import com.bezirk.pubsubbroker.PubSubEventReceiver;
import com.bezirk.sphere.api.SphereSecurity;
import com.bezirk.streaming.control.Objects.StreamRecord;
import com.bezirk.streaming.store.StreamStore;
import com.bezirk.streaming.threads.StreamReceivingThread;
import com.bezirk.util.ValidatorUtility;
import com.bezrik.network.BezirkNetworkUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This handler handles the StreamRequests and Responses and places them in
 * appropriate processing threads.
 */
final class BezirkStreamHandler {
    private static final Logger logger = LoggerFactory
            .getLogger(BezirkStreamHandler.class);

    String downloadPath;

    BezirkStreamHandler(String downloadPath){
        this.downloadPath = downloadPath;
    }

    /**
     * The assumption is that the ControlReceiverThread has already checked for
     * validity of StreamRequest.
     *
     * @param streamRequest
     * @return
     */
    boolean handleStreamRequest(final StreamRequest streamRequest, final Comms comms,
                                final PortFactory portFactory, final StreamStore streamStore,
                                final PubSubEventReceiver sadlReceiver, final SphereSecurity sphereSecurity) {

        // Check if the request is duplicate
        StreamRecord.StreamingStatus status = StreamRecord.StreamingStatus.ADDRESSED;
        int assignedPort;

        if (streamStore.checkStreamRequestForDuplicate(streamRequest.getUniqueKey())) {
            assignedPort = streamStore.getAssignedPort(streamRequest.getUniqueKey());
        } else {
            assignedPort = portFactory.getPort(streamRequest.getUniqueKey());
            if (-1 == assignedPort) {
                status = StreamRecord.StreamingStatus.BUSY;
            } else {
                status = StreamRecord.StreamingStatus.READY;
                new Thread(new StreamReceivingThread(assignedPort, downloadPath,
                        streamRequest, portFactory, sadlReceiver, sphereSecurity))
                        .start();
            }
        }

        logger.debug("<-Sender->" + streamRequest.getSender().device
                + streamRequest.getSender().zirkId);
        logger.debug("<-recipient->" + streamRequest.getRecipient().device
                + streamRequest.getRecipient().zirkId);

        //send device Ip
        String streamIp = BezirkNetworkUtilities.getLocalInet().getHostAddress();

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
        streamRecord.sphere = streamResponse.getSphereId();
        streamRecord.streamStatus = streamResponse.status;

        streamRecord.recipientIP = streamResponse.streamIp;

        logger.info("recipient key = " + streamResponse.getUniqueKey() + " rec IP = " + streamRecord.recipientIP + "sender device " + streamResponse.getSender().device);
        //quickfix to test: remove it later.
        /*List quickFix_keys = Arrays.asList(streamResponse.getUniqueKey().split(":"));

        if(quickFix_keys.size() > 0) {
            streamRecord.recipientIP = (String) quickFix_keys.get(0);
            logger.info("recipient IP"+streamResponse.getUniqueKey()+" rec = "+ streamRecord.recipientIP);
        }*/


        streamRecord.recipientPort = streamResponse.streamPort;

        if (ValidatorUtility.isObjectNotNull(streamQueue)) {
            streamQueue.addToQueue(streamRecord);
        }

        return true;
    }

}
