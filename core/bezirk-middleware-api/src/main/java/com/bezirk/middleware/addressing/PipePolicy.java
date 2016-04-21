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

import com.bezirk.middleware.messages.ProtocolRole;
import com.google.gson.Gson;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

/**
 * Defines the reasons to allow each protocol. UhU internally defines a subclass
 * for managing protocol authorization.
 *
 */
public class PipePolicy {
    // private final HashMap<ProtocolRole, String> reasonMap = new
    // HashMap<ProtocolRole, String>();

    // Map of protocal names and reasons
    private HashMap<String, String> reasonMap = new HashMap<String, String>();

    /**
     * @param json
     *            The Json String that is to be deserialized
     * @param dC
     *            class to deserialize into
     * @return object of class C
     */
    public static <C> C deserialize(String json, Class cL) {
        Gson gson = new Gson();
        return (C) gson.fromJson(json, cL);
    }

    /**
     * @param pRole
     * @param reason
     *            describes the benefit for the user for allowing this protocol
     */
    public void addProtocol(ProtocolRole pRole, String reason) {
        // reasonMap.put(pRole, reason);
        reasonMap.put(pRole.getProtocolName(), reason);

    }

    //
    // /**
    // * @param pRole
    // * @return the stated reason for allowing this protocol, or NULL if the
    // protocol is not defined in the policy
    // */
    // public String getReason(ProtocolRole pRole) {
    // return reasonMap.get(pRole);
    // }

    // returns list of protocol names
    public Set<String> getProtocolNames() {
        // reasonMap.put(pRole, reason);
        return reasonMap.keySet();

    }

    // /**
    // * UhU internally defines a subclass for managing policies, which
    // overrides this method.
    // *
    // * @return false
    // */
    // public boolean isAuthorized(ProtocolRole pRole) {
    // return false;
    // }

    /**
     * @param pRoleName
     * @return the stated reason for allowing this protocol, or NULL if the
     *         protocol is not defined in the policy
     */
    public String getReason(String pRoleName) {
        return reasonMap.get(pRoleName);
    }

    /**
     * UhU internally defines a subclass for managing policies, which overrides
     * this method.
     *
     * @return false
     */
    public boolean isAuthorized(String pRoleName) {
        return false;
    }

    /**
     * @return Json representation of the message as a String.
     */
    public String serialize() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public HashMap<String, String> getReasonMap() {
        return reasonMap;
    }

    public void setReasonMap(HashMap<String, String> map) {
        this.reasonMap = map;
    }

    /**
     * A Pipe
     */
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
