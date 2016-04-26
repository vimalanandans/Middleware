package com.bezirk.control.messages.pipes;

import com.bezirk.proxy.api.impl.UhuZirkEndPoint;
import com.google.gson.Gson;

public class PipeHeader {

    public static final String KEY_UHU_HEADER = "Uhu-Header";

    protected UhuZirkEndPoint senderSEP;

    protected String topic;

    public UhuZirkEndPoint getSenderSEP() {
        return senderSEP;
    }

    public void setSenderSEP(UhuZirkEndPoint senderSEP) {
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
