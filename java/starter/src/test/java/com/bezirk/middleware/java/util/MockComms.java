package com.bezirk.middleware.java.util;

import com.bezirk.middleware.core.actions.SendFileStreamAction;
import com.bezirk.middleware.core.comms.Comms;
import com.bezirk.middleware.core.comms.CommsNotification;
import com.bezirk.middleware.core.comms.CtrlMsgReceiver;
import com.bezirk.middleware.core.comms.processor.EventMsgReceiver;
import com.bezirk.middleware.core.control.messages.ControlLedger;
import com.bezirk.middleware.core.control.messages.ControlMessage;
import com.bezirk.middleware.core.control.messages.ControlMessage.Discriminator;
import com.bezirk.middleware.core.control.messages.EventLedger;
import com.bezirk.middleware.core.control.messages.Ledger;
import com.bezirk.middleware.core.control.messages.streaming.StreamRequest;

import java.util.ArrayList;

public class MockComms implements Comms {

    private ArrayList<ControlLedger> ctrlList;

    private ArrayList<EventLedger> eventList;

    private ArrayList<StreamRequest> streamList;

    public ArrayList<ControlLedger> getCtrlList() {
        return ctrlList;
    }

    public void setCtrlList(ArrayList<ControlLedger> ctrlList) {
        this.ctrlList = ctrlList;
    }

    public ArrayList<EventLedger> getEventList() {
        return eventList;
    }

    public void setEventList(ArrayList<EventLedger> eventList) {
        this.eventList = eventList;
    }

    public ArrayList<StreamRequest> getStreamList() {
        return streamList;
    }

    public void setStreamList(ArrayList<StreamRequest> streamList) {
        this.streamList = streamList;
    }

    public MockComms() {
        ctrlList = new ArrayList<>();
        eventList = new ArrayList<>();
        streamList = new ArrayList<>();
    }

    @Override
    public boolean sendMessage(Ledger message) {

        if (message instanceof ControlLedger) {

            ControlLedger ctrlLedger = (ControlLedger) message;
            ControlMessage ledgerMessage = ctrlLedger.getMessage();

            if (ledgerMessage instanceof StreamRequest) {
                StreamRequest streamMessage = (StreamRequest) ledgerMessage;
                streamList.add(streamMessage);
                return true;
            } else {

                ctrlList.add(ctrlLedger);
                return true;
            }
        } else if (message instanceof EventLedger) {
            eventList.add((EventLedger) message);
            return true;
        }
        return false;
    }

    @Override
    public boolean sendEventLedger(EventLedger ledger) {
        return false;
    }

    @Override
    public boolean sendControlMessage(ControlMessage message) {
        return false;
    }

    /*@Override
    public boolean sendStream(String uniqueKey) {
        // TODO Auto-generated method stub
        return true;
    }*/

    @Override
    public boolean processStreamRecord(SendFileStreamAction streamAction, Iterable<String> sphereList) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean registerNotification(
            CommsNotification errNotificationCallback) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public String getNodeId() {
        return null;
    }

//    @Override
//    public boolean initComms(CommsProperties commsProperties, InetAddress addr, SphereSecurity sphereSecurity, Streaming streaming) {
//        return false;
//    }


    @Override
    public boolean registerControlMessageReceiver(Discriminator id,
                                                  CtrlMsgReceiver receiver) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean registerEventMessageReceiver(EventMsgReceiver receiver) {
        return false;
    }

    /*@Override
    public void setSphereSecurity(SphereSecurity sphereSecurity) {

    }*/


    public void clearQueues() {

        ctrlList.clear();
        eventList.clear();
        streamList.clear();

    }


}
