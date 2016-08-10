package com.bezirk.spheremanager.ui.listitems;

import java.util.HashMap;


/**
 * Defines the reasons to allow each protocol.
 * Bezirk internally defines a subclass for managing protocol authorization.
 */
public class PipePolicy {
    private final HashMap<ProtocolItem, String> reasonMap = new HashMap<ProtocolItem, String>();

    public HashMap<ProtocolItem, String> getProtocolRoles() {
        return reasonMap;
    }

    /**
     * @param protocolItem
     * @param reason describes the benefit for the user for allowing this protocol
     */
    public void addProtocol(ProtocolItem protocolItem, String reason) {
        reasonMap.put(protocolItem, reason);
    }

    /**
     * @param protocolItem
     * @return the stated reason for allowing this protocol, or NULL if the protocol is not defined in the policy
     */
    public String getReason(ProtocolItem protocolItem) {
        return reasonMap.get(protocolItem);
    }

    /**
     * Bezirk internally defines a subclass for managing policies, which overrides this method.
     *
     * @return false
     */
    public boolean isAuthorized(ProtocolItem pRole) {
        return false;
    }
}