/**
 * This file is part of Bezirk-Middleware-API.
 * <p>
 * Bezirk-Middleware-API is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * </p>
 * <p>
 * Bezirk-Middleware-API is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * </p>
 * You should have received a copy of the GNU General Public License
 * along with Bezirk-Middleware-API.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.bezirk.middleware.messages;

/**
 * Base class for protocol role definitions. Bezirk uses topic-based pub-sub for distributed
 * communication, and roles are the unit of subscription. Protocols specify {@link Event Events} and
 * {@link Stream Streams} that will be sent to recipients subscribed to particular topics. A
 * <code>ProtocolRole</code> specifies those topics for a particular protocol and provides
 * metadata to (1) uniquely identify the protocol to the middleware and (2) describe the purpose of
 * the protocol to users.
 *
 * @see Event
 * @see Stream
 */
public abstract class ProtocolRole {
    /**
     * Returns the unique name of this protocol, used by the middleware to manage subscriptions
     * and discover Zirks subscribed to the role.
     * <p>
     * The implementation of this method should return the simple name of the implementing class,
     * for example:
     * </p>
     * <pre>
     * public class PartyProtocolRole implements ProtocolRole {
     *    {@literal @}Override
     *     public String getProtocolName() {
     *         return PartyProtocolRole.class.getSimpleName();
     *     }
     * }
     * </pre>
     *
     * @return the name of the protocol role for managing subscriptions and discovery
     */
    public abstract String getProtocolName();

    /**
     * Returns a human-readable description of this protocol. This description should be succinctly
     * written because the middleware may display it to the user in some contexts (e.g. when
     * asking the user to authorize a communication channel).
     *
     * @return a human-readable and user-friendly description of this protocol
     */
    public abstract String getDescription();

    /**
     * The specific pub-sub topics any Zirk subscribed to this role will subscribe to. In
     * particular, a Zirk will receive any <code>Event</code> sent in its sphere(s) whose topic is
     * listed in the array returned by this method.
     *
     * @return the set of topics this role subscribes to
     */
    public abstract String[] getEventTopics();


    /**
     * The specific pub-sub topics any Zirk subscribed to this role will subscribe to. In
     * particular, a Zirk will receive any <code>Stream</code> sent in its sphere(s) whose topic is
     * listed in the array returned by this method.
     *
     * @return the set of topics this role subscribes to
     */
    public abstract String[] getStreamTopics();

    @Override
    public boolean equals(Object p) {
        return p instanceof ProtocolRole &&
                this.getProtocolName().equals(((ProtocolRole) p).getProtocolName());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.getProtocolName() != null) ? this.getProtocolName().hashCode() : 0);
        return result;
    }
}
