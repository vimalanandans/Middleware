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
 * communication, and roles are the unit of subscription. Roles are conceptually a set of topics,
 * and a Zirk subscribes to roles rather than individual topics because the latter can be numerous
 * and too cumbersome to manage individually. A <code>ProtocolRole</code> implements the role
 * concept; it includes a set of topics and a human readable description of the role.
 *
 * @see Event
 * @see Stream
 */
public abstract class ProtocolRole {
    /**
     * Returns the unique name of this role, used by the middleware to manage subscriptions
     * and discover Zirks subscribed to the role.
     * <p>
     * The implementation of this method should return the simple name of the implementing class,
     * for example:
     * </p>
     * <pre>
     * public class PartyProtocolRole implements ProtocolRole {
     *    {@literal @}Override
     *     public String getRoleName() {
     *         return PartyProtocolRole.class.getSimpleName();
     *     }
     * }
     * </pre>
     *
     * @return the name of the role, for managing subscriptions and discovery
     */
    public abstract String getRoleName();

    /**
     * Returns a human-readable description of this role. This description should be succinctly
     * written because the middleware may display it to the user in some contexts (e.g. when
     * asking the user to authorize a communication channel).
     *
     * @return a human-readable and user-friendly description of this role
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
                this.getRoleName().equals(((ProtocolRole) p).getRoleName());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.getRoleName() != null) ? this.getRoleName().hashCode() : 0);
        return result;
    }
}
