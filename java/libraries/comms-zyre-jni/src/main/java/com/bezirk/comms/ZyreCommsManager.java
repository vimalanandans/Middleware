package com.bezirk.comms;



import com.bezirk.pipe.core.PipeManager;
import com.bezirk.processor.CommsProcessor;
import com.bezirk.sadl.UhuSadlManager;


import java.net.InetAddress;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * vimal : Uhu Communication manager for zyre - jni
 * this extends zyre specific comms all the queue, sockets, receiver threads etc etc
 *
 * */

public class ZyreCommsManager extends CommsProcessor {

    private static final Logger log = LoggerFactory.getLogger(ZyreCommsManager.class);

    private ZyreCommsJni comms = null;


    @Override
    public boolean initComms(CommsProperties commsProperties, InetAddress addr,
                             UhuSadlManager sadl, PipeManager pipe)
    {
        /*init zyre and internals of comms */
        if (comms == null) {

            comms = new ZyreCommsJni(this);

            comms.initZyre();

            return super.initComms(commsProperties,addr,sadl,pipe);
        }

        return false;
    }

    @Override
    public boolean startComms() {

        if(comms != null) {

            comms.startZyre();

            // call the base methods
            return super.startComms();
        }
        return false;
    }

    @Override
    public boolean stopComms() {

        if (comms != null) {
            // close zyre
            comms.stopZyre();
            // close the comms process comms
        }

        return super.stopComms();
    }
    @Override
    public boolean closeComms() {
        if (comms != null) {
            comms.closeComms();

        }
        return super.closeComms();
    }



    /** send to all : Multicast message */
    @Override
    public boolean sendToAll(byte[] msg, boolean isEvent){

        return comms.sendToAllZyre(msg, isEvent);
    }

    /** send to one : Unicast message
     * nodeId = device id
     * */
    @Override
    public boolean sendToOne(byte[] msg, String nodeId, boolean isEvent){
        return comms.sendToAllZyre(msg, isEvent);

    }
    
    /**
     * Create a new Zyre context, required during wifi reset.
     */
    @Override
    public boolean restartComms() {
    	return false;
    }

}
