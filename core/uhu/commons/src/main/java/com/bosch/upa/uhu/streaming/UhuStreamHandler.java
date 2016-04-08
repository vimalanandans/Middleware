package com.bosch.upa.uhu.streaming;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bosch.upa.uhu.comms.IPortFactory;
import com.bosch.upa.uhu.comms.IUhuComms;
import com.bosch.upa.uhu.comms.MessageQueue;
import com.bosch.upa.uhu.streaming.store.StreamStore;
import com.bosch.upa.uhu.streaming.threads.StreamReceivingThread;
import com.bosch.upa.uhu.control.messages.ControlLedger;
import com.bosch.upa.uhu.control.messages.streaming.StreamRequest;
import com.bosch.upa.uhu.control.messages.streaming.StreamResponse;
import com.bosch.upa.uhu.network.UhuNetworkUtilities;
import com.bosch.upa.uhu.sadl.ISadlEventReceiver;
import com.bosch.upa.uhu.sphere.api.IUhuSphereForSadl;
import com.bosch.upa.uhu.streaming.control.Objects.StreamRecord;
import com.bosch.upa.uhu.util.UhuValidatorUtility;

/**
 * This handler handles the StreamRequests and Responses and places them in
 * appropriate processing threads.
 */
final class UhuStreamHandler {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(UhuStreamHandler.class);

    /**
     * The assumption is that the ControlReceiverThread has already checked for
     * validity of StreamRequest.
     * 
     * @param streamRequest
     * @return
     */
    boolean handleStreamRequest(final StreamRequest streamRequest,final IUhuComms comms,
            final IPortFactory portFactory, final StreamStore streamStore,
            final ISadlEventReceiver sadlReceiver,final IUhuSphereForSadl sphereForSadl) {

        // Check if the request is duplicate
        StreamRecord.StreamingStatus status = StreamRecord.StreamingStatus.ADDRESSED;
        int assignedPort = 0;
        if (streamStore.checkStreamRequestForDuplicate(streamRequest
                .getUniqueKey())) {               // its duplicate
        	
        	 assignedPort = streamStore.getAssignedPort(streamRequest
                     .getUniqueKey());
           
        } else {    
        	
        	 assignedPort = portFactory.getPort(streamRequest.getUniqueKey());
             if (-1 == assignedPort) {
                 status = StreamRecord.StreamingStatus.BUSY;
             } else {
                 status = StreamRecord.StreamingStatus.READY;
                 new Thread(new StreamReceivingThread(assignedPort,
                         streamRequest, portFactory, sadlReceiver,sphereForSadl))
                         .start();
             }
           
        }
        LOGGER.debug("<-Sender->" + streamRequest.getSender().device
                + streamRequest.getSender().serviceId);
        LOGGER.debug("<-recipient->" + streamRequest.getRecipient().device
                + streamRequest.getRecipient().serviceId);

        //send device Ip
        String streamIp = UhuNetworkUtilities.getLocalInet().getHostAddress();

        StreamResponse streamResponse = new StreamResponse(
                streamRequest.getRecipient(), streamRequest.getSender(),
                streamRequest.getSphereId(), streamRequest.getUniqueKey(), status, streamIp , assignedPort);
        ControlLedger tcStrmRespMessage = new ControlLedger();
        tcStrmRespMessage.setMessage(streamResponse);
        tcStrmRespMessage.setSphereId(streamRequest.getSphereId());
        tcStrmRespMessage.setSerializedMessage(streamResponse.serialize());

        if (comms != null) {

            comms.sendMessage (tcStrmRespMessage);
        }

        return true;
    }

    boolean handleStreamResponse(final StreamResponse streamResponse,
            final MessageQueue streamQueue, final StreamStore streamStore) {
        LOGGER.info("RECEIVED STREAM-RESPONSE");

        StreamRecord streamRecord = streamStore.popStreamRecord(streamResponse
                .getUniqueKey());
        if (null == streamRecord) {
            LOGGER.debug("No StreamRecord for this Response or the Stream is already addressed");
            return false;
        }
        streamRecord.Sphere = streamResponse.getSphereId();
        streamRecord.streamStatus = streamResponse.status;

        streamRecord.recipientIP = streamResponse.streamIp;

        LOGGER.info("recipient key = "+streamResponse.getUniqueKey()+" rec IP = "+ streamRecord.recipientIP + "sender device "+ streamResponse.getSender().device);
        //quickfix to test: remove it later.
        /*List quickFix_keys = Arrays.asList(streamResponse.getUniqueKey().split(":"));

        if(quickFix_keys.size() > 0) {
            streamRecord.recipientIP = (String) quickFix_keys.get(0);
            LOGGER.info("recipient IP"+streamResponse.getUniqueKey()+" rec = "+ streamRecord.recipientIP);
        }*/


        streamRecord.recipientPort = streamResponse.streamPort;

        if (UhuValidatorUtility.isObjectNotNull(streamQueue)) {
            streamQueue.addToQueue(streamRecord);
        }

        return true;
    }

}
