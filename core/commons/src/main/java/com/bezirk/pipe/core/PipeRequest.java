package com.bezirk.pipe.core;

import com.bezirk.middleware.BezirkListener;
import com.bezirk.middleware.addressing.CloudPipe;
import com.bezirk.middleware.addressing.PipePolicy;
import com.bezirk.middleware.addressing.ZirkId;

public class PipeRequest {

    private ZirkId requestingService = null;

    // NOTE: we only support CloudPipes for now
    private CloudPipe pipe = null;
    private PipePolicy allowedIn = null;
    private PipePolicy allowedOut = null;
    private BezirkListener listener = null;
    private String id;

    public PipeRequest(String id) {
        this.id = id;
    }

    public ZirkId getRequestingService() {
        return requestingService;
    }

    public void setRequestingService(ZirkId requestingService) {
        this.requestingService = requestingService;
    }

    public CloudPipe getPipe() {
        return pipe;
    }

    public void setPipe(CloudPipe requestedPipe) {
        this.pipe = requestedPipe;
    }

    public PipePolicy getAllowedIn() {
        return allowedIn;
    }

    public void setAllowedIn(PipePolicy allowedIn) {
        this.allowedIn = allowedIn;
    }

    public PipePolicy getAllowedOut() {
        return allowedOut;
    }

    public void setAllowedOut(PipePolicy allowedOut) {
        this.allowedOut = allowedOut;
    }

    public BezirkListener getListener() {
        return listener;
    }

    public void setListener(BezirkListener listener) {
        this.listener = listener;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
