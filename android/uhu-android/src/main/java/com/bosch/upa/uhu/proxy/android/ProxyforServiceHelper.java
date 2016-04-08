package com.bosch.upa.uhu.proxy.android;

import com.bezirk.api.messages.Stream;
import com.bosch.upa.uhu.commons.UhuCompManager;
import com.bosch.upa.uhu.comms.IUhuComms;
import com.bosch.upa.uhu.control.messages.ControlLedger;
import com.bosch.upa.uhu.control.messages.streaming.StreamRequest;
import com.bosch.upa.uhu.proxy.api.impl.UhuServiceEndPoint;
import com.bosch.upa.uhu.streaming.control.Objects.StreamRecord;
import com.bosch.upa.uhu.util.UhuValidatorUtility;
import com.google.gson.Gson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Iterator;

/**
 * Created by AJC6KOR on 1/11/2016.
 */
class ProxyforServiceHelper {
    private static final Logger log = LoggerFactory.getLogger(ProxyforServiceHelper.class);

    private ControlLedger prepareMessage(Iterator<String> sphereIterator,String streamRequestKey,StreamRecord streamRecord, File tempFile) {
        final String sphereName = sphereIterator.next();
        final ControlLedger tcMessage = new ControlLedger();
        tcMessage.setSphereId(sphereName);
        UhuServiceEndPoint senderSEP = streamRecord.senderSEP;
        UhuServiceEndPoint receiver = streamRecord.recipientSEP;
        String serializedStream = streamRecord.serializedStream;
        String streamTopic = streamRecord.streamTopic;
        short streamId = streamRecord.localStreamId;
        final StreamRequest request = new StreamRequest(senderSEP,receiver,sphereName,streamRequestKey,null, serializedStream,streamTopic,tempFile.getName(),
                streamRecord.isSecure,streamRecord.isIncremental,streamRecord.allowDrops,streamId);
        tcMessage.setSphereId(sphereName);
        tcMessage.setMessage(request);
        tcMessage.setSerializedMessage(new Gson().toJson(request));
        return tcMessage;
    }

    StreamRecord prepareStreamRecord(UhuServiceEndPoint receiver, String serializedStream, String filePath, short streamId, UhuServiceEndPoint senderSEP, Stream stream) {
        final StreamRecord streamRecord = new StreamRecord();
        streamRecord.localStreamId = streamId;
        streamRecord.senderSEP = senderSEP;
        streamRecord.allowDrops = false;
        streamRecord.isIncremental = false;
        streamRecord.isSecure = stream.isSecure();
        streamRecord.Sphere = null;
        streamRecord.streamStatus = StreamRecord.StreamingStatus.PENDING;
        streamRecord.recipientIP = receiver.device;
        streamRecord.recipientPort = 0;
        streamRecord.filePath = filePath;
        streamRecord.pipedInputStream = null;
        streamRecord.recipientSEP = receiver;
        streamRecord.serializedStream = serializedStream;
        streamRecord.streamTopic = stream.topic;
        return streamRecord;
    }

    String getSphereId(UhuServiceEndPoint receiver, Iterator<String> sphereIterator) {
        String sphereId = null;
        while (sphereIterator.hasNext()) {
            sphereId = sphereIterator.next();
            if(UhuCompManager.getSphereForSadl().isServiceInSphere(receiver.getUhuServiceId(),sphereId)){
                log.debug("Found the sphere:"+sphereId);
                break;
            }
        }
        return sphereId;
    }

    void sendStreamToSpheres(Iterator<String> sphereIterator, String streamRequestKey, StreamRecord streamRecord, File tempFile,IUhuComms comms) {
        while(sphereIterator.hasNext()) {
            final ControlLedger tcMessage = prepareMessage(sphereIterator, streamRequestKey, streamRecord,tempFile);
            if(UhuValidatorUtility.isObjectNotNull(comms))
            {
                comms.sendMessage(tcMessage);
            }
            else{
                log.error("Comms manager not initialized");
            }
        }
    }

}

