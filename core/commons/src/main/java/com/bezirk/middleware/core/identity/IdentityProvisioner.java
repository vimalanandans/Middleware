package com.bezirk.middleware.core.identity;

import com.bezirk.middleware.identity.Alias;

/**
 * Internal API for creating and managing identities. This interface must not be exposed to
 * Zirks, otherwise they can spoof the identities of other Bezirk users whose identities they have
 * seen.
 */
public interface IdentityProvisioner {


    /**
     * Create a new identity with the human-readable identifier <code>name</code>.
     *
     * @param name the human-readable identifier for this alias
     * @return the new identity or <code>null</code> if required cryptography algorithms do not exist
     */
    Alias createIdentity(String name);

    /**
     * Set the identity the middleware instance is using to <code>identity</code>. After this
     * method is called, any {@link com.bezirk.middleware.messages.IdentifiedEvent identified event}
     * sent by a zirk will have this identity attached.
     *
     * @param identity the identity this middleware instance should use
     */
    void setIdentity(Alias identity);

    /**
     * Get the current alias set for the middleware so that it can be added to a message
     * by the Zirk proxy server.
     */
    Alias getAlias();
}
