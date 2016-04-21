package com.bezirk.messagehandler;

/**
 * Subclass of UhuCallbackMessage that is used to handle MulticastCallback
 */
public final class MulticastCallbackMessage extends ServiceIncomingMessage {

    public MulticastCallbackMessage() {
        callbackDiscriminator = "MULTICAST_STREAM";
    }


}
