package com.bezirk.comms;

import com.bezirk.pipe.core.PipeManager;
import com.bezirk.comms.processor.CommsProcessor;
import com.bezirk.pubsubbroker.PubSubBroker;
//import com.bezirk.rest.BezirkRestCommsManager;
import com.bezirk.util.BezirkValidatorUtility;

import java.net.InetAddress;

/**
 * vimal : Bezirk Communication manager for zyre - jni
 * this extends zyre specific comms all the queue, sockets, receiver threads etc etc
 */

public class ZyreCommsManager extends CommsProcessor {

    private ZyreCommsJni comms;

    private boolean delayedInit;

    private String zyreGroup;

    public ZyreCommsManager() {
        //default constructor
    }

    public ZyreCommsManager(String zyreGroup) {
        this.zyreGroup = zyreGroup;
    }

    @Override
    public boolean initComms(CommsProperties commsProperties, InetAddress addr,
                             PubSubBroker broker, PipeManager pipe, com.bezirk.streaming.Streaming streaming) {
        /*init zyre and internals of comms */
        if (comms == null) {
            return super.initComms(commsProperties, addr, broker, pipe, streaming);
        }

        return false;
    }

    @Override
    public boolean startComms() {

        if (BezirkValidatorUtility.isObjectNotNull(zyreGroup)) {
            // you can join the zyre group you specify here..
            comms = new ZyreCommsJni(this, zyreGroup);
        } else {
            comms = new ZyreCommsJni(this);
        }


        if (comms != null) {

            //Initialize a new Zyre context
            comms.initZyre(delayedInit);

            delayedInit = true;

            //Start the Zyre comms thread
            comms.startZyre();

            // removed the architectured refactoring code
            // set the comms u have selected, this will be a bridge for Commons code and Android.
           // BezirkRestCommsManager.getInstance().setBezirkComms(this);

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


    /**
     * send to all : Multicast message
     */
    @Override
    public boolean sendToAll(byte[] msg, boolean isEvent) {

        return comms.sendToAllZyre(msg);
    }

    /**
     * send to one : Unicast message
     * nodeId = device id
     */
    @Override
    public boolean sendToOne(byte[] msg, String nodeId, boolean isEvent) {
        return comms.sendToOneZyre(msg, nodeId);

    }

    /**
     * Create a new Zyre context, required during wifi reset.
     */
    @Override
    public boolean restartComms() {
        //Adding comments for testing
        if (comms != null && comms.getZyre() != null) {
            //stopComms(); // should not be done as it will stop the streaming threads.
            comms.stopZyre();

        }

        comms = new ZyreCommsJni(this);

        //Initialize a new Zyre Context
        comms.initZyre(delayedInit);

        //Start the Zyre comms thread
        comms.startZyre();

        return true;
    }

}
