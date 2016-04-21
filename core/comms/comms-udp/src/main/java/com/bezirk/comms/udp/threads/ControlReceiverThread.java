package com.bezirk.comms.udp.threads;


import com.bezirk.comms.IMessageDispatcher;
import com.bezirk.comms.MessageQueue;
import com.bezirk.control.messages.ControlLedger;
import com.bezirk.control.messages.Ledger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * @author Mansimar Aneja (mansimar.aneja@us.bosch.com)
 *         <p/>
 *         This class is a thread that is used to process Control messages that are within the Control Receiver Queue
 *         The thread interacts within the Uhu Internal Components in order to process the Control message and invoke services (if necessary)
 */
public class ControlReceiverThread implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(ControlReceiverThread.class);

    private Boolean running = false;

    private MessageQueue msgQueue = null;

    private IMessageDispatcher msgDispatcher = null;


    public ControlReceiverThread(IMessageDispatcher msgDispatcher, MessageQueue msgQueue) {
        this.msgDispatcher = msgDispatcher;
        this.msgQueue = msgQueue;
    }


    @Override
    public void run() {
        running = true;
        log.info("Control Receiver Thread has started");

        while (running) {
            if (Thread.currentThread().isInterrupted()) {
                log.info("Control Receiver Thread has Stopped");
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
