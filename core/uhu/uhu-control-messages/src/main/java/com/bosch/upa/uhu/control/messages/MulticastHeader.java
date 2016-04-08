package com.bosch.upa.uhu.control.messages;

import com.bezirk.api.addressing.Address;

/**
 * This Class reflects the Header for Multicast Events
 * Created by ANM1PI on 6/16/2014.
 */
public class MulticastHeader extends Header {
	private Address address;

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}
}
