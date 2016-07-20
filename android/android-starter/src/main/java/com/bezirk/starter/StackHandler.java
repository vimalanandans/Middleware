package com.bezirk.starter;

public interface StackHandler {
    void restartComms();

    void startStack(MainService mainService);

    void startStopRestServer(int startStopStatus);
}
