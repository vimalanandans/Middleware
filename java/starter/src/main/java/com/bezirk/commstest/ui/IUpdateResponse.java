package com.bezirk.commstest.ui;

/**
 * Interface that is used to update the UI
 *
 * @author VBD4KOR
 */
public interface IUpdateResponse {
    void updatePingResposne(String response);

    void updateUIPingSent(PingMessage msg);

    void updateUIPingReceived(PingMessage msg);

    void updateUIPongSent(PingMessage msg);

    void updateUIPongReceived(PongMessage msg, int size);
}
