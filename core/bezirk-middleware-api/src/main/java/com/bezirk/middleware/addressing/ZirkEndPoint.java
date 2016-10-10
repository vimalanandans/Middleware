package com.bezirk.middleware.addressing;

/**
 * An end point represents a Zirk that another Zirk can directly send messages to (e.g. unicast).
 * There are a couple ways to get a Zirk's <code>ZirkEndPoint</code> to directly send it messages:
 * <ul>
 * <li>Wait to receive a message from the Zirk, in which case the <code>ZirkEndPoint</code>
 * will be received by the appropriate listener (e.g.
 * {@link com.bezirk.middleware.messages.EventSet.EventReceiver} and
 * {@link com.bezirk.middleware.messages.StreamSet.StreamReceiver}.</li>
 * <li>Extend an <code>EventSet</code> and include a discovery message and a reply message.
 * Anyone subscribed to the set that receives the discovery message should use the reply to
 * notify the discovery sender of their existence and subscription to the set.</li>
 * </ul>
 * <p>
 * The Bezirk middleware implements this interface.
 * </p>
 */
public interface ZirkEndPoint {
// For now, this is a marker interface because there is nothing to offer to a Zirk API wise.
}
