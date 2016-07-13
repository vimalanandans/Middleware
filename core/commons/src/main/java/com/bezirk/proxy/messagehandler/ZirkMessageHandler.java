/**
 * @author Vijet Badigannavar(bvijet@in.bosch.com)
 */
package com.bezirk.proxy.messagehandler;

/**
 * Platform independent API used to give ZirkMessageHandler to the ProxyForBezirk Library.
 * Specific platforms should implement this interface and should inject it in BezirkCompManager.
 */
public interface ZirkMessageHandler {
    /**
     * Method fires the EventCallbackMessage to the ProxyForBezirkLibrary.
     *
     * @param eventIncomingMessage the callback message that will be fired.
     */
    public void onIncomingEvent(EventIncomingMessage eventIncomingMessage);

    /**
     * Method that fires the Unicast Stream response to ProxyForBezirkLibrary.
     *
     * @param streamIncomingMessage the callback message that will be fired.
     */
    public void onIncomingStream(StreamIncomingMessage streamIncomingMessage);
    /**
     * Method that fires the MulticastStream to ProxyForBezirk
     * @param multicastStreamCallbackMessage  the callback message that will be fired.
     */
    //public void fireMulticastStream(MulticastCallbackMessage multicastStreamCallbackMessage);

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

    /**
     * Method that fires the onPipeApprovedMessage for ProxyForBezirk
     *
     * @param pipeMsg callback message for pipe Approved
     */
    public void onPipeApprovedMessage(PipeRequestIncomingMessage pipeMsg);
}
