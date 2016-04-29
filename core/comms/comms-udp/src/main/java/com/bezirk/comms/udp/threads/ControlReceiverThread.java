package com.bezirk.comms.udp.threads;


import com.bezirk.comms.MessageDispatcher;
import com.bezirk.comms.MessageQueue;
import com.bezirk.control.messages.ControlLedger;
import com.bezirk.control.messages.Ledger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * This class is a thread that is used to process Control messages that are within the Control Receiver Queue
 * The thread interacts within the Bezirk Internal Components in order to process the Control message and invoke services (if necessary)
 */
public class ControlReceiverThread implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ControlReceiverThread.class);

    private MessageQueue msgQueue = null;

    private MessageDispatcher msgDispatcher = null;


    public ControlReceiverThread(MessageDispatcher msgDispatcher, MessageQueue msgQueue) {
        this.msgDispatcher = msgDispatcher;
        this.msgQueue = msgQueue;
    }


    @Override
    public void run() {
        Boolean running = true;
        logger.info("Control Receiver Thread has started");

        while (running) {
            if (Thread.currentThread().isInterrupted()) {
                logger.info("Control Receiver Thread has Stopped");
                running = false;
                continue;
            }
            CopyOnWriteArrayList<Ledger> receiverQueue =
                    new CopyOnWriteArrayList<Ledger>(msgQueue.getQueue());

            //When Receiver Queue is not empty wakeup
            Iterator<Ledger> it = receiverQueue.iterator();
            while (it.hasNext()) {
                ControlLedger tcMessage = (ControlLedger) it.next();

                msgDispatcher.dispatchControlMessages(tcMessage);

                msgQueue.removeFromQueue(tcMessage);

            }

        }
    }


}
