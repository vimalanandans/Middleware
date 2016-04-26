package com.bezirk.comms.udp.threads;

import com.bezirk.comms.IUhuCommsLegacy;
import com.bezirk.comms.MessageQueue;
import com.bezirk.comms.udp.sender.UhuCommsSend;
import com.bezirk.control.messages.ControlLedger;
import com.bezirk.control.messages.ControlMessage;
import com.bezirk.control.messages.Ledger;
import com.bezirk.control.messages.MulticastControlMessage;
import com.bezirk.control.messages.UnicastControlMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;


public class ControlSenderThread implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ControlSenderThread.class);

    private final static int timeBetweenRetransmits = 500; // time in milliseconds
    private final static int MIN_NO_OF_RECEIVERS = 5;
    private final static int SLEEP_TIME = 50; // time in milliseconds

    private MessageQueue msgQueue = null;
    private int numOfRetries = 5;
    // Sadl Instance in charge of sending
    private IUhuCommsLegacy uhuComms;

    public ControlSenderThread(IUhuCommsLegacy uhuComms, MessageQueue msgQueue) {
        this.uhuComms = uhuComms;
        this.msgQueue = msgQueue;
    }

    @Override
    public void run() {
        boolean running = true;
        logger.info("Control Sender Thread has started");
        while (running) {
            if (Thread.currentThread().isInterrupted()) {
                running = false;
                logger.info("Control SenderThread has stopped");
                continue;
            }
            //Wait on SendingMessageQueue
            CopyOnWriteArrayList<Ledger> senderctrlQueue =
                    new CopyOnWriteArrayList<Ledger>(msgQueue.getQueue());
            //Get the Sending Queue and Start Sending messages
            Iterator<Ledger> it = senderctrlQueue.iterator();
            while (it.hasNext()) {
                ControlLedger cMessage = (ControlLedger) it.next();

                if (!isMessageProcessable(cMessage)) {
                    if (senderctrlQueue.size() < MIN_NO_OF_RECEIVERS) {
                        try {
                            Thread.sleep(SLEEP_TIME);
                        } catch (InterruptedException e) {
                            logger.error("Waiting for sender control queue interrupted");
                        }
                    }
                    continue;
                }
                //if(this.sadlControlSender.sendControlMessage(cMessage)){
                if (sendControlMessage(cMessage)) {
                    if (UhuCommsSend.sendctrl(cMessage)) {
                        logger.info("Ctrl message- " + cMessage.getMessage().getDiscriminator() + ":" + cMessage.getMessage().getMessageId() + " sent");
                    } else {
                        logger.info("Send Failed| sphere Failed: Could not send msg");
                        removeMessage(cMessage);
                    }
                } else {
                    logger.info("Sadl Ctrl Send Failed for " + cMessage.getMessage().getDiscriminator() + ". Message is local");
                    removeMessage(cMessage);
                }
            }
        }
    }

    public boolean sendControlMessage(ControlLedger cLedger) {


        boolean gotoNextLayer = false;

        if (cLedger.getMessage() instanceof MulticastControlMessage) {
            this.bridgeControlMessage(cLedger);
            gotoNextLayer = true;
        } else if (cLedger.getMessage() instanceof UnicastControlMessage) {
            UnicastControlMessage uCtrlMsg = (UnicastControlMessage) cLedger.getMessage();
            if (uCtrlMsg.getIsLocal()) { //message is local
                bridgeControlMessage(cLedger);
                gotoNextLayer = false;
            } else {
                gotoNextLayer = true;
            }
        }

        return gotoNextLayer;

    }


    /**
     * Bridges the request locally to ControlSenderQueue or StreamingQueue
     *
     * @param tcMessage - TransControlMessage that will be bridged
     */
    private void bridgeControlMessage(final ControlLedger tcMessage) {
//		if(!isStreamRequest){
//			MessageQueueManager.getControlReceiverQueue().addToQueue(tcMessage);
//			return;
//		}
//		MessageQueueManager.getStreamingMessageQueue().addToQueue(tcMessage);

        if (ControlMessage.Discriminator.StreamRequest == tcMessage.getMessage().getDiscriminator()) {
            uhuComms.sendStream(tcMessage.getMessage().getUniqueKey());
            /*StreamRecord tempStreamRecord = StreamStore.popStreamRecord(tcMessage.getMessage().getUniqueKey());
			if(null == tempStreamRecord){
				return;
			}
			tempStreamRecord.streamStatus = StreamingStatus.LOCAL;
			//MessageQueueManager.getStreamingMessageQueue().addToQueue(tempStreamRecord);
			uhuComms.sendStreamMessage(tempStreamRecord);*/
        } else {
            //MessageQueueManager.getControlReceiverQueue().addToQueue(tcMessage);
            uhuComms.addToQueue(IUhuCommsLegacy.COMM_QUEUE_TYPE.CONTROL_RECEIVE_QUEUE, tcMessage);
        }
    }

    private boolean isMessageProcessable(ControlLedger cMessage) {
        //Sanity check
        if (cMessage == null) {
            removeMessage(cMessage);
            return false;
        }
        //Check number of times message has been sent
        if (cMessage.getNumOfSends() == numOfRetries) { // && !tcMessage.getMessage().getMessageId().equals(-1)){
            //remove from Send Queue and wait for Acks queue
            removeMessage(cMessage);
            return false;
        } else if (cMessage.getNumOfSends() == 1 && !cMessage.getMessage().getRetransmit()) {
            removeMessage(cMessage);
            return false;
        }

        if (new Date().getTime() - cMessage.getLastSent() < timeBetweenRetransmits) {
            //to soon to re-send this message, wait until diff is 1 min
            return false;
        }
        return true;

    }

    private void removeMessage(ControlLedger tcMessage) {
        msgQueue.removeFromQueue(tcMessage);
    }

}
