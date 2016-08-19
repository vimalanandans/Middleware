package com.bezirk.middleware.messages;

import com.bezirk.middleware.identity.Alias;

/**
 * Identified events have the alias assigned to the sending middleware attached as the message
 * is placed on the wire. This allows recipients to determine who the message is about. Zirks
 * do not need to do anything special to send an identified message. The middleware recognizes
 * outgoing identified messages and attaches the current user's alias.
 *
 * Zirks determine who a message is about by calling {@link #getAlias()}, then using
 * {@link com.bezirk.middleware.identity.IdentityManager#isMiddlewareUser(Alias)} to determine
 * whether or not the message is about the current user of the middleware or someone else:
 * <pre>
 *     // ...
 *
 *     // Receiving Zirk
 *     carObservations.setEventListener((event) -&gt; {
 *         if (event instanceof HvacObservation) {
 *             // HvacObservation is an IdentifiedEvent
 *             HvacObservation o = (HvacObservation) event;
 *             Alias a = o.getAlias();
 *
 *             if (bezirk.getIdentityManager().isMiddlewareUser(a)) {
 *                 // Do something with the observation knowing it is
 *                 // about the user of this Zirk
 *             } else {
 *                 // Do something with the observation knowing it is
 *                 // about someone in the same sphere as this Zirk's
 *                 // user, but it is not about this Zirk's user.
 *             }
 *             // ...
 *         }
 *         // ...
 *     });
 * </pre>
 */
public class IdentifiedEvent extends Event {
    private Alias alias;

    /**
     * Return the alias for the middleware user this event is about.
     *
     * @return the alias for the middleware user this event is about.
     */
    public Alias getAlias() {
        return alias;
    }

    /**
     * The middleware calls this method when an identified event is sent to attach an identity. If
     * a Zirk calls this method before sending a message, the middleware will overwrite the
     * Zirk-specified identity. This prevents Zirks from spoofing identities.
     *
     * @param alias the identity to attach
     */
    public void setAlias(Alias alias) {
        this.alias = alias;
    }
}
