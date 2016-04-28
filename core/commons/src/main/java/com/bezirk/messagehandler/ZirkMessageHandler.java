/**
 * @author Vijet Badigannavar(bvijet@in.bosch.com)
 */
package com.bezirk.messagehandler;

/**
 * Platform independent API used to give ZirkMessageHandler to the ProxyForUhu Library.
 * Specific platforms should implement this interface and should inject it in BezirkCompManager.
 */
public interface ZirkMessageHandler {
    /**
     * Method fires the EventCallbackMessage to the ProxyForUhuLibrary.
     *
     * @param eventIncomingMessage the callback message that will be fired.
     */
    public void onIncomingEvent(EventIncomingMessage eventIncomingMessage);

    /**
     * Method that fires the Unicast Stream response to ProxyForUhuLibrary.
     *
     * @param streamIncomingMessage the callback message that will be fired.
     */
    public void onIncomingStream(StreamIncomingMessage streamIncomingMessage);
    /**
     * Method that fires the MulticastStream to ProxyForUhu
     * @param multicastStreamCallbackMessage  the callback message that will be fired.
     */
    //public void fireMulticastStream(MulticastCallbackMessage multicastStreamCallbackMessage);

    /**
     * Method that fires the StreamStatus for ProxyForUhu
     *
     * @param streamStatusMessage callbackMessage that will be fired.
     */
    public void onStreamStatus(StreamStatusMessage streamStatusMessage);

    /**
     * Method that fires the onDiscoveryIncomingMessage for ProxyForUhu
     *
     * @param discoveryCallback callback Message that will be fired.
     */
    public void onDiscoveryIncomingMessage(DiscoveryIncomingMessage discoveryCallback);

    /**
     * Method that fires the onPipeApprovedMessage for ProxyForUhu
     *
     * @param pipeMsg callback message for pipe Approved
     */
    public void onPipeApprovedMessage(PipeRequestIncomingMessage pipeMsg);
}
