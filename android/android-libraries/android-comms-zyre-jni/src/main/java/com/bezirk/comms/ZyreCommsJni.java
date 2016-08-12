package com.bezirk.comms;

import android.util.Log;

import com.bezirk.comms.processor.CommsProcessor;
import com.bezirk.util.ValidatorUtility;

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
    public static final Logger logger = LoggerFactory.getLogger(ZyreCommsJni.class);

    public static final String TAG = ZyreCommsJni.class.getSimpleName();
    public static final String BEZIRK_GROUP = "BEZIRK_GROUP";
    private static boolean isZyreReady;
    private final ConcurrentMap<String, List<String>> peers = new ConcurrentHashMap<>();
    private final ZyreCommsHelper zyreCommsHelper;
    private static final int NUMBER_OF_EVENT_THREADS = 100;
    final CommsProcessor commsProcessor;

    final int delayedInitTime = 5000; //in ms
    private Zyre zyre;
    private String group = BEZIRK_GROUP;
    private boolean listenToEventsFlag;
    private ExecutorService eventExecutor;

    public ZyreCommsJni(CommsProcessor commsProcessor) {
        this.commsProcessor = commsProcessor;

        zyreCommsHelper = new ZyreCommsHelper(peers, commsProcessor);
    }

    public ZyreCommsJni(CommsProcessor commsProcessor, String zyreGroup) {
        this.commsProcessor = commsProcessor;

        if (zyreGroup != null) {// on valid group name replace the default group name

            this.group = zyreGroup;
        }

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
            logger.debug("Zyre is initialized but not yet ready..!!!");

            //initialize the executor.
            if (eventExecutor == null || eventExecutor.isShutdown() || eventExecutor.isTerminated()) {
                logger.debug("initialize the executor");
                eventExecutor = Executors.newFixedThreadPool(NUMBER_OF_EVENT_THREADS);
            }

            // delaying since zyre for android doesn't connect as fast as wifi available
            if (delayedInit) {
                delayZyreCreation();
            } else {
                isZyreReady = true;
            }

        } catch (UnsatisfiedLinkError e) {

            logger.error("Unable to load zyre comms. ", e);
            return false;
        }
        // create the zyre
        zyre.create();

        return true;
    }

    private void delayZyreCreation() {
        //adding the sleep as this will take time till new Zyre context is init
        try {
            logger.debug("zyre init : waiting for " + delayedInitTime + " before init");
            Thread.sleep(delayedInitTime + 1000L);
            isZyreReady = true;
            logger.debug("Zyre Initialization is Complete..!!!");
        } catch (InterruptedException e) {
            logger.error("Thread Interupted whicle initZyre");
        }
    }

    public boolean closeComms() {

        if (ValidatorUtility.isObjectNotNull(zyre))
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
        logger.debug("start Zyre");
        if (ValidatorUtility.isObjectNotNull(zyre)) {
            ZyreCommsJni zyreCommsJni = new ZyreCommsJni(commsProcessor,"New Grouppp");
            logger.debug("this.group val is "+this.group);
            // join the group

            //zyre.join("New Grouppp");
            System.out.println("group: " + getGroup());
            zyre.join(getGroup());

            //update flag
            listenToEventsFlag = true;

            try {
                // start the receiver
                logger.debug("start the receiver");
                this.start();
            } catch (Exception e) {
                logger.error("Exception while starting the thread", e);
                return false;
            }


        } else {
            logger.error("zyre not initialized");
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
            Log.e(TAG, "Error in stopping zyre.", e);
        }


        //update flag stop the thread
        listenToEventsFlag = false;
        interrupt();
        if (ValidatorUtility.isObjectNotNull(zyre))
            zyre.destroy();
        return true;
    }

    // send zyre whisper
    public boolean sendToAllZyre(byte[] msg) {
        // in zyre we are sending ctrl and event in same. isEvent is ignored
        final String data = new String(msg);

        if (ValidatorUtility.isObjectNotNull(zyre)) {

            //creating a new thread as we have a delayed ms wait if zyre was not initialized and this wait cannot happen on the main thread..
            Runnable eventThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (!isZyreReady) {
                            // if the zyre was re-initialized but is in middle of delayed ms wake up time..
                            Thread.sleep(delayedInitTime + 1000L);
                            logger.debug("Sleeping for few seconds as Zyre is not yet initialized!!!!");
                        }
                        logger.debug("data in zyreCommsJni is  "+data);
                        zyre.shout("New Grouppp", data);
                        //logger.debug("Shouted message to group : >> " + getGroup());
                        //logger.debug("Multi-cast size : >> " + data.length());
                    } catch (Exception e) {
                        logger.error(TAG, "An Error has occurred during Zyre Shout!!!", e);
                    }
                }
            });
            if (!eventExecutor.isShutdown()) {
                eventExecutor.execute(eventThread);
            }
            return true;

        } else {
            logger.error("zyre not initialized");
        }

        return false;
    }

    // send zyre whisper
    public boolean sendToOneZyre(byte[] msg, final String nodeId) {

        // in zyre we are sending ctrl and event in same. isEvent is ignored
        final String data = new String(msg);
        if (ValidatorUtility.isObjectNotNull(zyre)) {

            /*Each event will be sent in a new thread.
            creating a new thread as we have a delayed ms wait if zyre was not initialized and this wait cannot happen on the main thread..*/
            Runnable eventThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (!isZyreReady) {
                            // if the zyre was re-initialized but is in middle of delayed ms wake up time..
                            Thread.sleep(delayedInitTime);
                            logger.debug("Sleeping for few seconds as Zyre is not yet initialized!!!!");
                        }
                        //send to the specific node
                        logger.debug("nodeId is "+nodeId);
                        zyre.whisper(nodeId, data);

                        //logger.debug("Unicast size : >> " + data.length() + " data >> " + data);
                    } catch (Exception e) {
                        logger.error(TAG, "An Error has occurred during Zyre Shout!!!", e);
                    }
                }
            });
            if (!eventExecutor.isShutdown()) {
                eventExecutor.execute(eventThread);
            }
        } else {
            logger.error("zyre not initialized");
        }
        return false;
    }

    @Override
    public void run() {
        logger.debug("insider run");
        if (group == null) {
            logger.error("group not set");
            return;
        }
        if (zyre == null) {
            logger.error("Zyre not set");
            return;
        }
        logger.debug("listenToEventsFlag is "+listenToEventsFlag);
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

