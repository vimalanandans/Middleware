package com.bezirk.starter;

/**
 * Created by pik6kor on 1/7/2016.
 */
public interface IUhuStackHandler {
    public void restartComms();

    public void startStack(MainService mainService);

    public void startStopRestServer(int startStopStatus);
}
