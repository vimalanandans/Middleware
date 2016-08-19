package com.bezirk.middleware.identity;

/**
 * The identity manager for Bezirk-specific identities. This interface exposes methods useful to
 * Zirks that are making use of {@link com.bezirk.middleware.messages.IdentifiedEvent identified messages}.
 */
public interface IdentityManager {
    /**
     * Determines whether or not <code>alias</code> belongs to the current user of the middleware.
     * This is used when an identified event is received to determine whether or not the attached
     * alias belongs to the current user. This is useful in determining how to handle the data
     * in identified messages. For example, if the message is an observation about an action the
     * user performed and the alias belongs to the current user of the middleware it is a primary
     * observation. Otherwise, the observation is potentially not of interest to the receiving
     * Zirk or it may be used as social context.
     *
     * @param alias an alias attached to a received identified message
     * @return <code>true</code> if the alias belongs to the current user of the middleware
     */
    boolean isMiddlewareUser(Alias alias);
}
