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

import com.bezirk.middleware.BezirkListener;
import com.bezirk.middleware.messages.ProtocolRole;
import com.google.gson.Gson;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * <b style="color:darkred;">This JavaDoc is for an API class that is currently incomplete.</b>
 * </p>
 * Base class for (1) defining the {@link ProtocolRole ProtocolRoles} that may be transmitted via
 * a pipe and (2) documenting the rationale for allowing each role. These policies may be displayed
 * to the user to help them decide whether or not they want to allow the use of a particular pipe.
 * Polices are directional, meaning typically a policy will be created to specify what roles are
 * allowed to send data out of a sphere using a pipe and another policy will define the roles for
 * sending data into a sphere using the pipe. The authorization process is initiated by calling
 * {@link com.bezirk.middleware.Bezirk#requestPipeAuthorization(ZirkId, Pipe, PipePolicy, PipePolicy, BezirkListener)}.
 */
public abstract class PipePolicy {
    // Map of protocal names and reasons
    private Map<String, String> reasonMap = new HashMap<String, String>();

    private static final Gson gson = new Gson();

    /**
     * Serialize the policy to a JSON string.
     *
     * @return JSON representation of the policy
     */
    public String toJson() {
        return gson.toJson(this);
    }

    /**
     * Deserialize the <code>json</code> string to create an object of type <code>objectType</code>.
     *
     * @param <C>        the type of the object represented by <code>json</code>, set by
     *                   <code>objectType</code>
     * @param json       the JSON String that is to be deserialized
     * @param objectType the type of the object represented by <code>json</code>
     * @return an object of type <code>objectType</code> deserialized from <code>json</code>
     */
    public static <C> C fromJson(String json, Class objectType) {
        return (C) gson.fromJson(json, objectType);
    }

    /**
     * Whitelist a role to allow it to transmit data using a particular pipe, and document
     * the rationale for why the role should be allowed with a user-friendly message.
     *
     * @param protocolRole a new role that should be allowed to send data using aa pipe
     * @param reason       describes the benefit for the user for allowing this role
     */
    public void addAllowedProtocol(ProtocolRole protocolRole, String reason) {
        reasonMap.put(protocolRole.getProtocolName(), reason);
    }

    /**
     * Returns the names of protocol roles allowed to send data on any pipe associated with this
     * policy.
     *
     * @return the names of protocol roles allowed to send data on any pipe associated with this
     * policy.
     */
    public Set<String> getProtocolNames() {
        // reasonMap.put(pRole, reason);
        return reasonMap.keySet();

    }

    /**
     * Returns the rationale for why a particular protocol role is allowed to send data
     * on pipes associated with this policy.
     *
     * @param protocolRoleName the name of the protocol role whose rationale for why the role is
     *                         allowed should be returned
     * @return the stated reason for allowing <code>protocolRoleName</code>, or <code>null</code> if
     * the protocol is not defined in the policy
     */
    public String getReason(String protocolRoleName) {
        return reasonMap.get(protocolRoleName);
    }

    /**
     * Returns <code>true</code> if this policy authorizes the use of <code>protocolRoleName</code>.
     *
     * @param protocolRoleName the name of the protocol role whose authorizations state is to be
     *                         checked
     * @return <code>true</code> if this policy authorizes the use of <code>protocolRoleName</code>
     */
    public abstract boolean isAuthorized(String protocolRoleName);

    public Map<String, String> getReasonMap() {
        return reasonMap;
    }

    public void setReasonMap(Map<String, String> map) {
        this.reasonMap = map;
    }

    @Override
    public boolean equals(Object that) {
        if (that instanceof PipePolicy) {
            Collection<String> thoseProtocols = ((PipePolicy) that)
                    .getReasonMap().keySet();
            Collection<String> theseProtocols = reasonMap.keySet();

            return thoseProtocols.containsAll(theseProtocols)
                    && theseProtocols.containsAll(thoseProtocols);
        }

        return false;
    }

    @Override
    public int hashCode() {

        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((reasonMap != null) ? reasonMap.hashCode() : 0);
        return result;
    }

}
