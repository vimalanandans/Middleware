package com.bezirk.rest;

public interface IHttpComms {

    public boolean startHttpComms();

    public boolean stopHttpComms();

    public boolean isServerRunning();

    //public String serveRequest(String request);

}
