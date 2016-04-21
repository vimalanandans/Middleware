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
package com.bezirk.middleware.messages;

/**
 * Super class for all protocol role definitions.  A role includes a set of events and streams that a given participant subscribes to.
 */
public abstract class ProtocolRole {
    /**
     * Concrete classes must implement this method to return the specific protocol label.
     *
     * @return protocol label, which may used for subscription and discovery
     */
    public abstract String getProtocolName();

    /**
     * Human readable name for the protocol. E.g. Bezirk may refer to it to explain pipe policy.
     *
     * @return The Protocol role description
     */
    public abstract String getDescription();

    /**
     * Concrete classes must implement this method to return the specific array of event topics.
     *
     * @return array of event topics
     */
    public abstract String[] getEventTopics();

    /**
     * Concrete classes must implement this method to return the specific array of stream topics.
     *
     * @return array of stream topics
     */
    public abstract String[] getStreamTopics();

    @Override
    public boolean equals(Object p) {
        if (p instanceof ProtocolRole) {
            return this.getProtocolName().equals(((ProtocolRole) p).getProtocolName());
        }
        return false;

    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.getProtocolName() != null) ? this.getProtocolName().hashCode() : 0);
        return result;
    }


    /**
     * @param p
     * @return whether p has the same label as this
     */

}
