package com.bezirk.rest;

import com.bezirk.comms.IUhuComms;
import com.bezirk.control.messages.EventLedger;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class will be central hold of comms object between Android and Commons,
 * and hold the data of HTTP comms
 *
 * @author pik6kor
 */
public class BezirkRestCommsManager {

    //TODO Punith: move to commons

    private static volatile BezirkRestCommsManager commsManager;

    private IUhuComms uhuComms;

    //A rolling map of limited size of 100
    private Map<Integer, List<String>> responseMap;

    private String selectedSphere;

    private String selectedSphereName;

    private boolean isStarted = false;

    //message sending queue
    private Queue<EventLedger> requestQueue = new LinkedList<>();

    private Thread senderThread = null;


    private BezirkRestCommsManager() {
        //private constructor
        responseMap = new ConcurrentHashMap<Integer, List<String>>(100);

        //start the sender thread..
        senderThread = new Thread(new SenderThread());
        senderThread.start();
    }


    public static BezirkRestCommsManager getInstance() {
        if (commsManager == null) {
            commsManager = new BezirkRestCommsManager();
        }

        return commsManager;
    }

    public IUhuComms getBezirkComms() {
        return uhuComms;
    }

    public void setUhuComms(IUhuComms uhuComms) {
        this.uhuComms = uhuComms;
    }

    public Map<Integer, List<String>> getResponseMap() {
        return responseMap;
    }

    public String getSelectedSphere() {
        return selectedSphere;
    }

    public void setSelectedSphere(String selectedSphere) {
        this.selectedSphere = selectedSphere;
    }

    public String getSelectedSphereName() {
        return selectedSphereName;
    }

    public void setSelectedSphereName(String selectedSphereName) {
        this.selectedSphereName = selectedSphereName;
    }

    public Queue<EventLedger> getRequestQueue() {
        return requestQueue;
    }

    public boolean isStarted() {
        return isStarted;
    }

    public void setStarted(boolean isStarted) {
        this.isStarted = isStarted;
    }

    public Thread getSenderThread() {
        return senderThread;
    }

    /**
     * Append the response string to the map value.. This will be a list of Response.
     *
     * @param uniqueID
     * @param responseString
     */
    public void appendResponseToMap(Integer uniqueID, String responseString) {
        //re-insert the new String response to value.

        List<String> responseList = responseMap.get(uniqueID);

        //response list can be null also
        List<String> valueList = responseList == null ? new ArrayList<String>() : responseList;
        valueList.add(responseString);
        responseMap.put(uniqueID, valueList);

        //Think if you have to re-insert or you just want to append as above.
    }

    /**
     * sender thread, picks from the pool and sends the data accross comms
     *
     * @author PIK6KOR
     */
    class SenderThread implements Runnable {
        @Override
        public void run() {
            while (true) {
                if (requestQueue.peek() != null) {
                    uhuComms.sendMessage(requestQueue.poll());
                }

            }
        }
    }
}
