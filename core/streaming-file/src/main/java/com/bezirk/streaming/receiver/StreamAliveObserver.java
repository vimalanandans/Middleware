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
 * Update will be called when the subject StreamBook will be updated with a new entry.
 */

class StreamAliveObserver implements Observer {

    //executor which handles the file stream receiving thread.
    private ExecutorService fileStreamReceiverExecutor;
    //size of thread size
    private static final int THREAD_SIZE = 10;
    //comms injected.
    private Comms comms = null;
    private final Gson gson = new Gson();
    //streamBook and Portfactory also needs to be dependency injected.
    private StreamBook streamBook;
    private FileStreamPortFactory portFactory;
    private ZirkMessageHandler zirkMessageHandler;

    //logger instance
    private static final Logger logger = LoggerFactory
            .getLogger(StreamAliveObserver.class);


    StreamAliveObserver(Comms comms, StreamBook streamBook, FileStreamPortFactory portFactory, ZirkMessageHandler zirkMessageHandler){
        this.comms = comms;
        this.fileStreamReceiverExecutor = Executors.newFixedThreadPool(THREAD_SIZE);
        this.streamBook = streamBook;
        this.portFactory = portFactory;
        this.zirkMessageHandler = zirkMessageHandler;
    }

    @Override
    public void update(Observable observable, Object streamRequest) {
        FileStreamRequest fileStreamRequest = (FileStreamRequest)streamRequest;
        StreamRecord streamRecord = fileStreamRequest.getStreamRecord();

        if(StreamRecord.StreamRecordStatus.ALIVE == streamRecord.getStreamRecordStatus()){
            //when the status is alive
            streamRecord.setStreamRecordStatus(StreamRecord.StreamRecordStatus.ADDRESSED);

            //add the stream record to stream book.
            streamBook.addStreamingRecordToBook(streamRecord);

            //assign a port to stream record from the port factory.
            Integer assignedPort = portFactory.getActivePort(streamRecord.getStreamId());
            logger.debug("assigned port {} for stream request of file {}",assignedPort, streamRecord.getFile().getName());

            if(assignedPort != -1){
                streamRecord.setRecipientPort(assignedPort);
                streamRecord.setRecipientIp(getIPAddress());

                //start the receiver thread and send a reply to sender.
                FileStreamReceivingThread streamReceivingThread =new FileStreamReceivingThread(assignedPort, streamRecord.getFile(), portFactory);
                fileStreamReceiverExecutor.execute(streamReceivingThread);

                streamRecord.setStreamRecordStatus(StreamRecord.StreamRecordStatus.ASSIGNED);

                //update stream book.
                streamBook.updateStreamRecordInBook(streamRecord.getStreamId(), StreamRecord.StreamRecordStatus.ASSIGNED, assignedPort, getIPAddress());
                //reply to the sender with status
                replyToSender(streamRecord);

            }else{
                //update the stream record to busy status and send it to .
                streamRecord.setRecipientPort(assignedPort);
                streamBook.updateStreamRecordInBook(streamRecord.getStreamId(), StreamRecord.StreamRecordStatus.BUSY, null, null);

                //reply to the sender with status
                replyToSender(streamRecord);
            }

            //give a callback status to zirk of updated status.
            zirkMessageHandler.callBackToZirk(streamRecord);
        }

    }



    /**
     * reply to sender with the given stream staus.
     * @param streamRecord streamRecord.
     */
    private void replyToSender(StreamRecord streamRecord) {
        //send a ControlMessage(StreamResponse) back to sender with updated information
        ControlLedger controlLedger = new ControlLedger();

        //sphere will be DEFAULT as of now
        controlLedger.setSphereId("DEFAULT");

        FileStreamRequest streamResponse = new FileStreamRequest(streamRecord.getSenderServiceEndPoint(), "DEFAULT", streamRecord);
        controlLedger.setMessage(streamResponse);
        controlLedger.setSerializedMessage(gson.toJson(streamResponse));

        logger.debug("sending a reply to sender for stream request {}", streamRecord.getStreamId());
        comms.sendControlLedger(controlLedger);
    }


    /**
     * Get IP address from first non-localhost interface, This has to be removed!!!!!!!!!!!!!!
     * @return  address or empty string
     */
    private static String getIPAddress() {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                String sAddr = getNetworkAddress(intf);
                if (sAddr != null){
                    return sAddr;
                }
            }
        } catch (Exception ex) {
            logger.error("Error while getting IP address of device", ex);
        } // for now eat exceptions
        return "";
    }

    /**
     * returns a network address from NetworkInterface
     * @param intf NetworkInterface
     * @return IP address of the device.
     */
    private static String getNetworkAddress(NetworkInterface intf) {
        List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
        for (InetAddress addr : addrs) {
            if (!addr.isLoopbackAddress()) {
                String sAddr = addr.getHostAddress();
                boolean isIPv4 = sAddr.indexOf(':')<0;
                if (isIPv4){
                    return sAddr;
                }
            }
        }
        return null;
    }
}
