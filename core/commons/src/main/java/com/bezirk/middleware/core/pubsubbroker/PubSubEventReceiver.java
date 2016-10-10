package com.bezirk.middleware.core.pubsubbroker;

import com.bezirk.middleware.core.actions.ReceiveFileStreamAction;

/**
 * Platform independent API's used by the pubsuber for handling incoming event pub sub Reception.
 * Also get the stream messages.
 */
public interface PubSubEventReceiver {
    /**
     * notify the stream data
     */
    boolean processNewStream(ReceiveFileStreamAction streamCallbackMessage);

}
