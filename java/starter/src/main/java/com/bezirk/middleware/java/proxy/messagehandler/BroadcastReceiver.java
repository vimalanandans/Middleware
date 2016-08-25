package com.bezirk.middleware.java.proxy.messagehandler;

import com.bezirk.middleware.core.actions.ZirkAction;

public interface BroadcastReceiver {
    void onReceive(ZirkAction incomingMessage);
}
