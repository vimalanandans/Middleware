/**
 * @author Vijet Badigannavar(bvijet@in.bosch.com)
 */
package com.bezirk.proxy;

import com.bezirk.actions.UnicastEventAction;
import com.bezirk.proxy.messagehandler.StreamIncomingMessage;
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
     * @param streamIncomingMessage the callback message that will be fired.
     */
    void onIncomingStream(StreamIncomingMessage streamIncomingMessage);

    /**
     * Method that fires the StreamStatus for ProxyForBezirk
     *
     * @param streamStatusAction callbackMessage that will be fired.
     */
    void onStreamStatus(StreamStatusAction streamStatusAction);
}
