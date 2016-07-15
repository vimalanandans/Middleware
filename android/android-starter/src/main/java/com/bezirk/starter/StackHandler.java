package com.bezirk.starter;

public interface StackHandler {
    public void restartComms();

    public void startStack(MainService mainService);

    public void startStopRestServer(int startStopStatus);
}
