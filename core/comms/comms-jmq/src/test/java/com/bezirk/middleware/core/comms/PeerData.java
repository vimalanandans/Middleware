package com.bezirk.middleware.core.comms;

public class PeerData {
    private final int firstValue; //first data value received from the peer
    private int lastValue; //last data value received from the peer
    private int totalValuesReceived;

    public PeerData(final int firstValue) {
        this.firstValue = firstValue;
        this.lastValue = firstValue;
    }

    public int getFirstValue() {
        return firstValue;
    }

    public int getLastValue() {
        return lastValue;
    }

    public int getTotalValuesReceived() {
        return totalValuesReceived;
    }

    public void setLastValue(int lastValue) {
        this.lastValue = lastValue;
        this.totalValuesReceived++;
    }

}
