/**
 * @author Vijet Badigannavar(bvijet@in.bosch.com)
 */
package com.bezirk.proxy.messagehandler;

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
    public void onIncomingEvent(EventIncomingMessage eventIncomingMessage);

    /**
     * Method that fires the Unicast StreamDescriptor response to ProxyForBezirkLibrary.
     *
     * @param streamIncomingMessage the callback message that will be fired.
     */
    public void onIncomingStream(StreamIncomingMessage streamIncomingMessage);

    /**
     * Method that fires the StreamStatus for ProxyForBezirk
     *
     * @param streamStatusMessage callbackMessage that will be fired.
     */
    public void onStreamStatus(StreamStatusMessage streamStatusMessage);

    /**
     * Method that fires the onDiscoveryIncomingMessage for ProxyForBezirk
     *
     * @param discoveryCallback callback Message that will be fired.
     */
    public void onDiscoveryIncomingMessage(DiscoveryIncomingMessage discoveryCallback);
}
