package com.bezirk.spheremanager.ui.listitems;

import java.util.HashMap;


/**
 * Defines the reasons to allow each protocol.
 * UhU internally defines a subclass for managing protocol authorization.
 *
 */
public class PipePolicy {
	private final HashMap<ProtocolItem, String> reasonMap = new HashMap<ProtocolItem, String>();
	
	public HashMap<ProtocolItem, String> getProtocolRoles() {
		return reasonMap;
	}

	/**
	 * @param pRole
	 * @param reason describes the benefit for the user for allowing this protocol
	 */
	public void addProtocol(ProtocolItem pRole, String reason) {
		reasonMap.put(pRole, reason);
	}

	/**
	 * @param pRole
	 * @return the stated reason for allowing this protocol, or NULL if the protocol is not defined in the policy
	 */
	public String getReason(ProtocolItem pRole) {
		return reasonMap.get(pRole);
	}
	
	/**
	 * UhU internally defines a subclass for managing policies, which overrides this method.
	 * 
	 * @return false
	 */
	public boolean isAuthorized(ProtocolItem pRole) {
		return false;
	}
}