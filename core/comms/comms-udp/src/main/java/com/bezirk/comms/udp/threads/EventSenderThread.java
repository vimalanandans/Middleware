/**
 *
 */
package com.bezirk.comms.udp.threads;


import com.bezirk.commons.UhuCompManager;
import com.bezirk.comms.IUhuCommsLegacy;
import com.bezirk.comms.MessageQueue;
import com.bezirk.comms.udp.sender.UhuCommsSend;
import com.bezirk.control.messages.EventLedger;
import com.bezirk.control.messages.Header;
import com.bezirk.control.messages.Ledger;
import com.bezirk.control.messages.MulticastHeader;
import com.bezirk.pipe.core.PipeManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * @author Mansimar Aneja (mansimar.aneja@us.bosch.com)
 */
public class EventSenderThread implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(EventSenderThread.class);
    private final static int timeBetweenRetransmits = 100; // time in milliseconds
    private final static int NumOfRetries = 10;
    private final PipeManager pipeManager;
    private final IUhuCommsLegacy uhuComms;
    MessageQueue msgQueue = null;
    private Boolean running = false;

    public EventSenderThread(IUhuCommsLegacy uhuComms, MessageQueue msgQueue, PipeManager pipeManager) {
        this.uhuComms = uhuComms;
        this.msgQueue = msgQueue;
        this.pipeManager = pipeManager;
    }

    @Override
    public void run() {
        running = true;
        log.info("Uhu Sender Thread has started \n");

        while (running) {
            if (Thread.currentThread().isInterrupted()) {
                running = false;
                log.info("SenderThread has stopped \n");
                continue;
            }

            //Wait on SendingMessageQueue
            CopyOnWriteArrayList<Ledger> senderQueue =
                    new CopyOnWriteArrayList<Ledger>(msgQueue.getQueue());

            Iterator<Ledger> it = senderQueue.iterator();
            while (it.hasNext()) {
                EventLedger eLedger = (EventLedger) it.next();
                //Check Message Validity
                if (!isMessageValid(eLedger)) {
                    msgQueue.removeFromQueue(eLedger);
                    continue;
                }
                //Check if too soon to transmit
                if (new Date().getTime() - eLedger.getLastSent() < timeBetweenRetransmits) {
                    continue;
                }

                final Boolean spherePassed;
                //If message is sent first time go through the stack until sphere
                //Otherwise, just invoke UhuComms
                if (eLedger.getNumOfSends() == 0) {
                    log.debug("First time through the stack");
                    // If this is a Multicast message, check to see if we also need to send it to a pipe
                    Header header = eLedger.getHeader();
                    if (header instanceof MulticastHeader) {
                        processPipes(eLedger);
                    } else {
                        log.debug("Not a multicast message: " + eLedger.getSerializedMessage());
                    }
                    spherePassed = goThroSadlAndSpheres(eLedger);
                } else {
                    //sphere layer has already been passed and message is encrypted
                    spherePassed = true;
                }

                if (spherePassed) {
                    sendToComms(eLedger);
                } else {
                    msgQueue.removeFromQueue(eLedger);
                }
            }
        }    //running
    }

    private Boolean goThroSadlAndSpheres(EventLedger eLedger) {
        //CHECK THIS ENCRYPTION CODE
        if (updateLocalMessageQueues(eLedger)) {
            byte[] encryptMsg = UhuCompManager.getSphereForSadl().encryptSphereContent(eLedger.getHeader().getSphereName(), eLedger.getSerializedMessage());
            if (null != encryptMsg) {
                eLedger.setEncryptedMessage(encryptMsg);
                log.trace("Sphere passed: set encrypted message");
                return true;
            }
            return false;
        }
        log.info("SADL failed to send the Msg, May be the msg is local");
        return false;
    }

    public boolean updateLocalMessageQueues(EventLedger eMessage) {
        //the message is generated for the first time &&
        //the message is being sent for first time
        //--> establish a bridge to the local device receiver side
        if (eMessage.getIsMulticast() && eMessage.getNumOfSends() == 0) {
            //add to the receiver queue
            //MessageQueueManager.getReceiverMessageQueue().addToQueue(eMessage);
            uhuComms.addToQueue(IUhuCommsLegacy.COMM_QUEUE_TYPE.EVENT_RECEIVE_QUEUE, eMessage);
            return true;
        }
        //the message is a local unicast
        //--> establish a bridge to the local device receiver side
        else if (eMessage.getIsLocal()) {
            //add to the receiver queue
            //MessageQueueManager.getReceiverMessageQueue().addToQueue(eMessage);
            uhuComms.addToQueue(IUhuCommsLegacy.COMM_QUEUE_TYPE.EVENT_RECEIVE_QUEUE, eMessage);
            //must remove from Sending messageQueue
            //MessageQueueManager.getSendingMessageQueue().removeFromQueue(eMessage);
            uhuComms.removeFromQueue(IUhuCommsLegacy.COMM_QUEUE_TYPE.EVENT_SEND_QUEUE, eMessage);
            return false;
        }
        //must go through uHu as message is a unicast and not local
        return true;
        /**

         if(ecMessage.getIsMulticast()){
         MessageQueueManager.getReceiverMessageQueue().addToQueue(ecMessage);
         return true;
         }
         else if(ecMessage.isLocal()){
         MessageQueueManager.getReceiverMessageQueue().addToQueue(ecMessage);
         MessageQueueManager.getSendingMessageQueue().removeFromQueue(ecMessage);
         return false;
         }
         return true;*/
    }

    private void sendToComms(EventLedger eLedger) {
        //send to comms
        if (UhuCommsSend.send(eLedger)) {
            log.info(" <<<< Message sent . No of Send " + String.valueOf(eLedger.getNumOfSends()) +
                    " Msg Id " + eLedger.getHeader().getUniqueMsgId());
        } else {
            log.info("Message not sent, maybe senderAddress=localAddress");
        }
    }

    private Boolean isMessageValid(EventLedger eventLedger) {
        //Null checker ==> if message is null just continue iteration
        if (eventLedger == null) {
            log.error(" Null message in SenderQueue");
            return false;
        }
        //Check number of times message has been sent
        if (eventLedger.getNumOfSends().equals(NumOfRetries)) {
            return false;
        }
        return true;
    }

    private void processPipes(EventLedger eLedger) {
        if (pipeManager != null) {
            String serializedEvent = eLedger.getSerializedMessage();
            pipeManager.processRemoteSend(eLedger.getHeader(), serializedEvent);
        } else {
            log.debug("pipeManager is null; not checking if message should be sent to a pipe");
        }
    }

    public void stop() {
        log.info("Sender Thread has stopped");
        running = false;
    }

}
