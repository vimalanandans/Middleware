/**
 * The MIT License (MIT)
 * Copyright (c) 2016 Bezirk http://bezirk.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.bezirk.middleware.java.util;

import com.bezirk.middleware.core.comms.Comms;
import com.bezirk.middleware.core.comms.CtrlMsgReceiver;
import com.bezirk.middleware.core.comms.processor.EventMsgReceiver;
import com.bezirk.middleware.core.control.messages.ControlLedger;
import com.bezirk.middleware.core.control.messages.ControlMessage;
import com.bezirk.middleware.core.control.messages.ControlMessage.Discriminator;
import com.bezirk.middleware.core.control.messages.EventLedger;

import java.util.ArrayList;

public class MockComms implements Comms {
    private ArrayList<ControlLedger> ctrlList;

    private ArrayList<EventLedger> eventList;

    public MockComms() {
        ctrlList = new ArrayList<>();
        eventList = new ArrayList<>();
    }

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

    @Override
    public boolean sendEventLedger(EventLedger ledger) {
        return false;
    }

    @Override
    public boolean sendControlLedger(ControlLedger controlLedger) {
        return false;
    }

    @Override
    public boolean sendControlMessage(ControlMessage message) {
        return false;
    }

    @Override
    public String getNodeId() {
        return null;
    }

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

    public void clearQueues() {

        ctrlList.clear();
        eventList.clear();

    }


}
