/**
 * Copyright (C) 2014 Robert Bosch, LLC. All Rights Reserved.
 * <p/>
 * Authors: Joao de Sousa, 2014
 * Mansimar Aneja, 2014
 * Vijet Badigannavar, 2014
 * Samarjit Das, 2014
 * Cory Henson, 2014
 * Sunil Kumar Meena, 2014
 * Adam Wynne, 2014
 * Jan Zibuschka, 2014
 */
package com.bezirk.middleware.addressing;

import com.bezirk.middleware.messages.Event;

/**
 * Represents a semantic address for publishing events and streams over Bezirk
 *
 * @see Event
 */
public class Address {
    private final boolean local, remote;
    private final Pipe pipe;
    private final Location location;

    /**
     * Address for local publishing only: no pipes.
     * Local publishing means publishing in all Spheres that the service is a member of, as defined
     * by the user via the Bezirk UIs.
     *
     * @param loc maybe null, which places no constraints on location
     */
    public Address(Location loc) {
        local = true;
        remote = false;
        pipe = null;
        location = loc;
    }

    /**
     * Address for publishing across pipes.
     *
     * @param loc
     * @param p         specific pipe, or NULL for ALL authorized pipes
     * @param alsoLocal whether local publishing is additionally targeted
     * @see #Address(Location)
     */
    public Address(Location loc, Pipe p, boolean alsoLocal) {
        local = alsoLocal;
        remote = true;
        pipe = p;
        location = loc;
    }

    public boolean isLocalTargeted() {
        return local;
    }

    /**
     * If this returns false, then {@link #getPipe()} is meaningless
     */
    public boolean isPipeTargeted() {
        return remote;
    }

    /**
     * @return either a specific pipe or NULL representing ALL authorized pipes
     * @see #isPipeTargeted()
     */
    public Pipe getPipe() {
        return pipe;
    }

    /**
     * @return either a target location, or NULL representing no location constraints
     */
    public Location getLocation() {
        return location;
    }
}
