package com.bezirk.proxy.android;

import com.bezirk.BezirkCompManager;
import com.bezirk.comms.Comms;
import com.bezirk.control.messages.ControlLedger;
import com.bezirk.control.messages.streaming.StreamRequest;
import com.bezirk.middleware.messages.Stream;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.streaming.control.Objects.StreamRecord;
import com.bezirk.util.ValidatorUtility;
import com.google.gson.Gson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Iterator;

class ProxyForZirksHelper {
    private static final Logger logger = LoggerFactory.getLogger(ProxyForZirksHelper.class);

    private ControlLedger prepareMessage(Iterator<String> sphereIterator, String streamRequestKey, StreamRecord streamRecord, File tempFile) {
        final String sphereName = sphereIterator.next();
        final ControlLedger tcMessage = new ControlLedger();
        tcMessage.setSphereId(sphereName);
        BezirkZirkEndPoint senderSEP = streamRecord.senderSEP;
        BezirkZirkEndPoint receiver = streamRecord.recipientSEP;
        String serializedStream = streamRecord.serializedStream;
        String streamTopic = streamRecord.streamTopic;
        short streamId = streamRecord.localStreamId;
        final StreamRequest request = new StreamRequest(senderSEP, receiver, sphereName, streamRequestKey, null, serializedStream, streamTopic, tempFile.getName(),
                streamRecord.isEncrypted, streamRecord.isIncremental, streamRecord.isReliable, streamId);
        tcMessage.setSphereId(sphereName);
        tcMessage.setMessage(request);
        tcMessage.setSerializedMessage(new Gson().toJson(request));
        return tcMessage;
    }

    StreamRecord prepareStreamRecord(BezirkZirkEndPoint receiver, String serializedStream, File file, short streamId, BezirkZirkEndPoint senderSEP, Stream stream) {
        final StreamRecord streamRecord = new StreamRecord();
        streamRecord.localStreamId = streamId;
        streamRecord.senderSEP = senderSEP;
        streamRecord.isReliable = false;
        streamRecord.isIncremental = false;
        streamRecord.isEncrypted = stream.isEncrypted();
        streamRecord.sphere = null;
        streamRecord.streamStatus = StreamRecord.StreamingStatus.PENDING;
        streamRecord.recipientIP = receiver.device;
        streamRecord.recipientPort = 0;
        streamRecord.file = file;
        streamRecord.pipedInputStream = null;
        streamRecord.recipientSEP = receiver;
        streamRecord.serializedStream = serializedStream;
        streamRecord.streamTopic = stream.topic;
        return streamRecord;
    }

    String getSphereId(BezirkZirkEndPoint receiver, Iterator<String> sphereIterator) {
        String sphereId = null;
        while (sphereIterator.hasNext()) {
            sphereId = sphereIterator.next();
            if (BezirkCompManager.getSphereForPubSubBroker().isZirkInSphere(receiver.getBezirkZirkId(), sphereId)) {
                logger.debug("Found the sphere:" + sphereId);
                break;
            }
        }
        return sphereId;
    }

    void sendStreamToSpheres(Iterator<String> sphereIterator, String streamRequestKey, StreamRecord streamRecord, File tempFile, Comms comms) {
        while (sphereIterator.hasNext()) {
            final ControlLedger tcMessage = prepareMessage(sphereIterator, streamRequestKey, streamRecord, tempFile);
            if (ValidatorUtility.isObjectNotNull(comms)) {
                comms.sendMessage(tcMessage);
            } else {
                logger.error("Comms manager not initialized");
            }
        }
    }

}

