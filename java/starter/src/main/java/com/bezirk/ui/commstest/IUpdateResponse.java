package com.bezirk.ui.commstest;

/**
 * Interface that is used to update the UI
 *
 * @author VBD4KOR
 */
public interface IUpdateResponse {
    void updatePingResponse(String response);

    void updateUIPingSent(PingMessage msg);

    void updateUIPingReceived(PingMessage msg);

    void updateUIPongSent(PingMessage msg);

    void updateUIPongReceived(PongMessage msg, int size);
}
