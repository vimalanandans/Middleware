/**
 * @author Vijet Badigannavar(bvijet@in.bosch.com)
 */
package com.bezirk.proxy;

import com.bezirk.actions.ReceiveFileStreamAction;
import com.bezirk.actions.UnicastEventAction;
import com.bezirk.actions.StreamStatusAction;

/**
 * Platform independent API used to give MessageHandler to the ProxyForBezirk Library.
 * Specific platforms should implement this interface and should inject .
 */
public interface MessageHandler {
    /**
     * Method fires the EventCallbackMessage to the ProxyForBezirkLibrary.
     *
     * @param eventIncomingMessage the callback message that will be fired.
     */
    void onIncomingEvent(UnicastEventAction eventIncomingMessage);

    /**
     * Method that fires the Unicast StreamDescriptor response to ProxyForBezirkLibrary.
     *
     * @param receiveFileStreamAction the callback message that will be fired.
     */
    void onIncomingStream(ReceiveFileStreamAction receiveFileStreamAction);

    /**
     * Method that fires the StreamStatus for ProxyForBezirk
     *
     * @param streamStatusAction callbackMessage that will be fired.
     */
    void onStreamStatus(StreamStatusAction streamStatusAction);
}
