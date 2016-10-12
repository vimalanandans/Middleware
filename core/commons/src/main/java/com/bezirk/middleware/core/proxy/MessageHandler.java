/**
 * @author Vijet Badigannavar(bvijet@in.bosch.com)
 */
package com.bezirk.middleware.core.proxy;

import com.bezirk.middleware.core.actions.UnicastEventAction;

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
}
