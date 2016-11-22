package com.bezirk.middleware.streaming;

import com.bezirk.middleware.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.middleware.proxy.api.impl.ZirkId;

/**
 * Created by PIK6KOR on 11/3/2016.
 */

public abstract class Stream {

    private BezirkZirkEndPoint recipientEndPoint;

    //TODO punith: is this requdierd??
    private ZirkId zirkId;


    public Stream(BezirkZirkEndPoint recipientEndPoint, ZirkId zirkID){
        this.recipientEndPoint = recipientEndPoint;
        this.zirkId = zirkID;
    }

    public BezirkZirkEndPoint getRecipientEndPoint() {
        return recipientEndPoint;
    }

    public ZirkId getZirkId() {
        return zirkId;
    }
}
