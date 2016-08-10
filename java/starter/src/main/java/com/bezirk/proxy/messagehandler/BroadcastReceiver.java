package com.bezirk.proxy.messagehandler;

import com.bezirk.actions.ZirkAction;

public interface BroadcastReceiver {
    void onReceive(ZirkAction incomingMessage);
}
