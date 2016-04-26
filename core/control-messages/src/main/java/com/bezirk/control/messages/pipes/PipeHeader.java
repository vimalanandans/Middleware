package com.bezirk.control.messages.pipes;

import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.google.gson.Gson;

public class PipeHeader {

    public static final String KEY_UHU_HEADER = "Uhu-Header";

    protected BezirkZirkEndPoint senderSEP;

    protected String topic;

    public BezirkZirkEndPoint getSenderSEP() {
        return senderSEP;
    }

    public void setSenderSEP(BezirkZirkEndPoint senderSEP) {
        this.senderSEP = senderSEP;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String serialize() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
