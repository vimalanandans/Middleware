/**
 * The MIT License (MIT)
 * Copyright (c) 2016 Bezirk http://bezirk.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.bezirk.streaming.receiver;

import com.bezirk.middleware.core.comms.Comms;
import com.bezirk.middleware.core.control.messages.ControlLedger;
import com.bezirk.streaming.FileStreamRequest;
import com.bezirk.streaming.StreamBook;
import com.bezirk.streaming.StreamRecord;
import com.bezirk.streaming.portfactory.FileStreamPortFactory;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * StreamAliveObserver is a implementation of <code>StreamEventObserver</code>.
 * #update will be called when the subject StreamBook will be updated with a new entry.
 */

class StreamAliveObserver implements Observer {

    private static final int THREAD_SIZE = 10;
    private static final Logger logger = LoggerFactory.getLogger(StreamAliveObserver.class);

    private final ExecutorService fileStreamReceiverExecutor;
    private final Comms comms;
    private final Gson gson = new Gson();
    private final StreamBook streamBook;
    private final FileStreamPortFactory portFactory;
    private final ZirkMessageHandler zirkMessageHandler;

    StreamAliveObserver(Comms comms, StreamBook streamBook, FileStreamPortFactory portFactory, ZirkMessageHandler zirkMessageHandler){
        this.comms = comms;
        this.fileStreamReceiverExecutor = Executors.newFixedThreadPool(THREAD_SIZE);
        this.streamBook = streamBook;
        this.portFactory = portFactory;
        this.zirkMessageHandler = zirkMessageHandler;
    }

    @Override
    public void update(Observable observable, Object streamRequest) {
        final FileStreamRequest fileStreamRequest = (FileStreamRequest)streamRequest;
        final StreamRecord streamRecord = fileStreamRequest.getStreamRecord();

        if(StreamRecord.StreamRecordStatus.ALIVE == streamRecord.getStreamRecordStatus()){
            streamRecord.setStreamRecordStatus(StreamRecord.StreamRecordStatus.ADDRESSED);
            streamBook.addStreamingRecordToBook(streamRecord);

            //assign a port to stream record from the port factory.
            final Integer assignedPort = portFactory.getActivePort(streamRecord.getStreamId());
            logger.debug("assigned port {} for stream request of file {}",assignedPort, streamRecord.getFile().getName());

            if(assignedPort != -1){
                streamRecord.setRecipientPort(assignedPort);
                streamRecord.setRecipientIp(getIPAddress());

                //start the receiver thread and send a reply to sender.
                final FileStreamReceivingThread streamReceivingThread =new FileStreamReceivingThread(assignedPort, streamRecord.getFile(), portFactory);
                fileStreamReceiverExecutor.execute(streamReceivingThread);

                streamRecord.setStreamRecordStatus(StreamRecord.StreamRecordStatus.ASSIGNED);
                streamBook.updateStreamRecordInBook(streamRecord.getStreamId(), StreamRecord.StreamRecordStatus.ASSIGNED, assignedPort, getIPAddress());
                replyToSender(streamRecord);

            }else{
                //update the stream record to BUSY status and send it to Sender.
                streamRecord.setRecipientPort(assignedPort);
                streamBook.updateStreamRecordInBook(streamRecord.getStreamId(), StreamRecord.StreamRecordStatus.BUSY, null, null);
                replyToSender(streamRecord);
            }

            zirkMessageHandler.callBackToZirk(streamRecord);
        }

    }


    /**
     * After updating the #StreamBook, Send a control message to sender with updated #StreamRecord status.
     * @param streamRecord streamRecord.
     */
    private void replyToSender(@NotNull StreamRecord streamRecord) {
        final ControlLedger controlLedger = new ControlLedger();
        controlLedger.setSphereId("DEFAULT");

        final FileStreamRequest streamResponse = new FileStreamRequest(streamRecord.getSenderServiceEndPoint(), "DEFAULT", streamRecord);
        controlLedger.setMessage(streamResponse);
        controlLedger.setSerializedMessage(gson.toJson(streamResponse));

        logger.debug("sending a reply to sender for stream request {}", streamRecord.getStreamId());
        comms.sendControlLedger(controlLedger);
    }


    /**
     * Get IP address from first non-localhost interface.
     * @return  address or empty string
     */
    private static String getIPAddress() {
        try {
            final List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                final String sAddr = getNetworkAddress(intf);
                if (sAddr != null){
                    return sAddr;
                }else{
                    logger.error("Error while getting IP address of device!");
                }
            }
        } catch (Exception ex) {
            logger.error("Error while getting IP address of device", ex);
            throw new UnsupportedOperationException("Could not get the IP address of the device!.");
        }
        return "";
    }

    /**
     * returns a network address from NetworkInterface
     * @param intf NetworkInterface
     * @return IP address of the device.
     */
    private static String getNetworkAddress(NetworkInterface intf) {
        final List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
        for (InetAddress addr : addrs) {
            if (!addr.isLoopbackAddress()) {
                final String sAddr = addr.getHostAddress();
                boolean isIPv4 = sAddr.indexOf(':')<0;
                if (isIPv4){
                    return sAddr;
                }else{
                    logger.error("could not return a network address from NetworkInterface");
                }
            }
        }
        return null;
    }
}
