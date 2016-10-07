package com.bezirk.middleware.core.streaming;

import com.bezirk.middleware.messages.Event;

import java.util.Random;

public class StreamPublishEvent extends Event{

    //set the subscriberID
    private String subscriberId;

    public StreamPublishEvent(String subscriberId){
        Random rand = new Random();
        this.subscriberId = subscriberId + "-" + (rand.nextInt(50) + 1);
    }

    public String getSubscriberId() {
        return subscriberId;
    }

    public void setSubscriberId(String subscriberId) {
        this.subscriberId = subscriberId;
    }
}
