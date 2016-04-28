/**
 *
 */
package com.bezirk.comms.udp.threads;

import com.bezirk.comms.IMessageDispatcher;
import com.bezirk.comms.MessageQueue;
import com.bezirk.control.messages.EventLedger;
import com.bezirk.control.messages.Ledger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Mansimar Aneja (mansimar.aneja@us.bosch.com)
 *         <p/>
 *         This class is a thread that is used to process events that are within the Event Receiver Queue
 *         This thread interacts within the Bezirk Internal Components to determine the zirk(s) that are to be invoked (if any)
 */
public class EventReceiverThread implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(EventReceiverThread.class);

    IMessageDispatcher msgDispatcher = null;

    MessageQueue msgQueue = null;
    private Boolean running = false;

    public EventReceiverThread(IMessageDispatcher msgDispatcher, MessageQueue msgQueue) {
        this.msgDispatcher = msgDispatcher;
        this.msgQueue = msgQueue;
    }

    @Override
    public void run() {
        running = true;

        while (running) {
            running = true;
            log.info("Bezirk Receiver Thread has started \n");

            while (running) {
                if (Thread.currentThread().isInterrupted()) {
                    log.info("Bezirk ReceiverThread has Stopped");
                    running = false;
                    continue;
                }
                CopyOnWriteArrayList<Ledger> receiverQueue =
                        new CopyOnWriteArrayList<Ledger>(msgQueue.getQueue());

                //When Receiver Queue is not empty wakeup
                Iterator<Ledger> it = receiverQueue.iterator();
                while (it.hasNext()) {
                    EventLedger eLedger = (EventLedger) it.next();

                    msgDispatcher.dispatchServiceMessages(eLedger);
                    //remove the message
                    msgQueue.removeFromQueue(eLedger);
                }
            }
        }
    }


    public void stop() {
        running = false;
    }
}
