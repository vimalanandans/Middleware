package com.bezirk.comms;

import android.util.Log;

import com.bosch.upa.uhu.processor.CommsProcessor;
import com.bosch.upa.uhu.util.UhuValidatorUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zyre.Zyre;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/***
 * ZYRE COMMS JNI IMPLEMENTATION
 */

public class ZyreCommsJni extends Thread {

    public static final Logger log = LoggerFactory.getLogger(ZyreCommsJni.class);
    public static final String TAG = ZyreCommsJni.class.getSimpleName();

    private Zyre zyre;

    private final ConcurrentMap<String, List<String>> peers = new ConcurrentHashMap<>();

    public static final String BEZIRK_GROUP = "BEZIRK_GROUP";

    private String group;

    private boolean listenToEventsFlag;

    CommsProcessor commsProcessor;

    int delayedInitTime = 5000; //in ms

    private final ZyreCommsHelper zyreCommsHelper;

    private static boolean isZyreReady;

    private ExecutorService eventExecutor;

    private final int numberOfEventThreads = 100;

    public ZyreCommsJni(CommsProcessor commsProcessor) {
        this.commsProcessor = commsProcessor;
        //setting to the default group
        this.group = BEZIRK_GROUP;

        zyreCommsHelper = new ZyreCommsHelper(peers, commsProcessor);
    }

    public ZyreCommsJni(CommsProcessor commsProcessor, String zyreGroup) {
        this.commsProcessor = commsProcessor;
        this.group = zyreGroup;
        zyreCommsHelper = new ZyreCommsHelper(peers, commsProcessor);

    }
    /**
     * initialize the zyre
     */
    public boolean initZyre(boolean delayedInit) {

        try {
            //before initialization is ready, set flag as false
            isZyreReady = false;

            //init a new zyre context..
            zyre = new Zyre();
            log.debug("Zyre is initialized but not yet ready..!!!");

            //initialize the executor.
            if (eventExecutor == null || eventExecutor.isShutdown() || eventExecutor.isTerminated()) {
                eventExecutor = Executors.newFixedThreadPool(numberOfEventThreads);
            }

            // delaying since zyre for android doesn't connect as fast as wifi available
            if (delayedInit == true) {
                delayZyreCreation();
            } else {
                isZyreReady = true;
            }

        } catch (UnsatisfiedLinkError e) {

            log.error("Unable to load zyre comms. ", e);
            return false;
        }
        // create the zyre
        zyre.create();

        return true;
    }

    private void delayZyreCreation() {
        //adding the sleep as this will take time till new Zyre context is init
        try {
            log.info("zyre init : waiting for " + delayedInitTime + " before init");
            Thread.sleep(delayedInitTime + 1000L);
            isZyreReady = true;
            log.debug("Zyre Initialization is Complete..!!!");
        } catch (InterruptedException e) {
            log.error("Thread Interupted whicle initZyre");
        }
    }

    public boolean closeComms() {

        if (UhuValidatorUtility.isObjectNotNull(zyre))
            zyre.destroy();

        // what else do to close comes
        // IS IT OK TO DO THE BELOW?
        zyre = null;

        return false;
    }

    /**
     * start the zyre
     */
    public boolean startZyre() {

        if (UhuValidatorUtility.isObjectNotNull(zyre)) {

            // join the group
            zyre.join(getGroup());

            //update flag
            listenToEventsFlag = true;

            try {
                // start the receiver
                this.start();
            } catch (Exception e) {
                log.error("Exception while starting the thread", e);
                return false;
            }


        } else {
            log.error("zyre not initialized");
        }
        return true;

    }

    /**
     * stop the zyre
     */
    public boolean stopZyre() {
        try {
            //stop the executor servcie
            eventExecutor.shutdown();
            while (!eventExecutor.awaitTermination(5, TimeUnit.SECONDS)) ;
        } catch (InterruptedException e) {
            Log.e(TAG,"Error in stopping zyre.",e);
        }


        //update flag stop the thread
        listenToEventsFlag = false;
        interrupt();
        if (UhuValidatorUtility.isObjectNotNull(zyre))
            zyre.destroy();
        return true;
    }

    // send zyre whisper
    public boolean sendToAllZyre(byte[] msg) {
        // in zyre we are sending ctrl and event in same. isEvent is ignored
        final String data = new String(msg);

        if (UhuValidatorUtility.isObjectNotNull(zyre)) {

            //creating a new thread as we have a delayed ms wait if zyre was not initialized and this wait cannot happen on the main thread..
            Runnable eventThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (!isZyreReady) {
                            // if the zyre was re-initialized but is in middle of delayed ms wake up time..
                            Thread.sleep(delayedInitTime + 1000L);
                            log.debug("Sleeping for few seconds as Zyre is not yet initialized!!!!");
                        }
                        zyre.shout(getGroup(), data);
                        log.debug("Shouted message to group : >> " + getGroup());
                        log.debug("Multi-cast size : >> " + data.length() );
                    } catch (Exception e) {
                        log.error(TAG,"An Error has occurred during Zyre Shout!!!",e);
                    }
                }
            });
            if (!eventExecutor.isShutdown()) {
                eventExecutor.execute(eventThread);
            }
            return true;

        }else{
            log.error("zyre not initialized");
        }

        return false;
    }

    // send zyre whisper
    public boolean sendToOneZyre(byte[] msg, final String nodeId) {

        // in zyre we are sending ctrl and event in same. isEvent is ignored
        final String data = new String(msg);
        if (UhuValidatorUtility.isObjectNotNull(zyre)) {

            /*Each event will be sent in a new thread.
            creating a new thread as we have a delayed ms wait if zyre was not initialized and this wait cannot happen on the main thread..*/
            Runnable eventThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (!isZyreReady) {
                            // if the zyre was re-initialized but is in middle of delayed ms wake up time..
                            Thread.sleep(delayedInitTime);
                            log.debug("Sleeping for few seconds as Zyre is not yet initialized!!!!");
                        }
                        //send to the specific node
                        zyre.whisper(nodeId, data);

                        log.debug("Unicast size : >> " + data.length() + " data >> " + data);
                    } catch (Exception e) {
                        log.error(TAG,"An Error has occurred during Zyre Shout!!!",e);
                    }
                }
            });
            if (!eventExecutor.isShutdown()) {
                eventExecutor.execute(eventThread);
            }
        } else {
            log.error("zyre not initialized");
        }
        return false;
    }

    @Override
    public void run() {
        if (group == null) {
            log.error("group not set");
            return;
        }
        if (zyre == null) {
            log.error("Zyre not set");
            return;
        }

        while (listenToEventsFlag) {

            Map<String, String> eventMap = zyreCommsHelper.receive(zyre);

            if (eventMap.isEmpty())
                return;

            String eventType = eventMap.get("event");
            String peer = eventMap.get("peer");
            String peerGroup = eventMap.get("group");
            String payload = eventMap.get("message");
            zyreCommsHelper.processEvent(eventType, peer, peerGroup, payload);


        }
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public Zyre getZyre() {
        return zyre;
    }

}

