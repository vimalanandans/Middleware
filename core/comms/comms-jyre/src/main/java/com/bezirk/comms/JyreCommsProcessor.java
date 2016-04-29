package com.bezirk.comms;

import com.bezirk.comms.thread.JyreReceiverThread;
import com.bezirk.pipe.core.PipeManager;
import com.bezirk.processor.CommsProcessor;
import com.bezirk.sadl.BezirkSadlManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;

public class JyreCommsProcessor extends CommsProcessor {
    private static final Logger logger = LoggerFactory.getLogger(JyreCommsProcessor.class);

    private JyreReceiverThread comms = null;
    private JyreCommsSend commsSender = null;
    private String zreGroup = null;

    public JyreCommsProcessor() {
        logger.info("***** construct JyreCommsProcessor *****");
    }

    /**
     * initialize the comms
     */
    @Override
    public boolean initComms(CommsProperties commsProperties, InetAddress addr,
                             BezirkSadlManager sadl, PipeManager pipe) {
        /*init jyre reciever thread  and internals of comms */
        if (comms == null) {
            this.zreGroup = "BEZIRK_GROUP";
            comms = new JyreReceiverThread(this, zreGroup);
            this.commsSender = new JyreCommsSend(zreGroup);

            comms.initJyre();

            return super.initComms(commsProperties, addr, sadl, pipe);
        }

        return false;
    }

    /**
     * Start the comms. starts the Jyre receiver thread
     */
    @Override
    public boolean startComms() {

        if (comms != null) {
            // start the Jyre receiver thread
            comms.startJyre();

            // call the base methods
            return super.startComms();
        }
        return false;
    }

    /**
     * stop the Jyre receiver thread and internals of the comms
     */
    @Override
    public boolean stopComms() {

        if (comms != null) {
            // close Jyre
            comms.stopJyre();
            // close the comms process comms
        }

        return super.stopComms();
    }

    /**
     * close the comms
     */
    @Override
    public boolean closeComms() {
        if (comms != null) {
            comms.closeComms();

        }
        return super.closeComms();
    }


    @Override
    public boolean sendToAll(byte[] msg, boolean isEvent) {

        return commsSender.sendMessage(msg, isEvent);
    }

    @Override
    public boolean sendToOne(byte[] msg, String nodeId, boolean isEvent) {

        return commsSender.sendMessage(msg, isEvent);
    }

    @Override
    public boolean restartComms() {
        // TODO Auto-generated method stub
        return false;
    }


}
