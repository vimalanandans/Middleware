package com.bosch.upa.uhu.proxy.api.impl;

import java.io.Serializable;

import com.bezirk.api.addressing.ServiceId;

public final class UhuServiceId implements ServiceId,Serializable {
	private final String uhuServiceId;
	private String uhuEventId;

	public UhuServiceId (String serviceId){
		this.uhuServiceId = serviceId;
	}
	
	public UhuServiceId (String serviceId, String uhuEventId){
		this.uhuServiceId = serviceId;
		this.uhuEventId = uhuEventId;
	}

	public String getUhuServiceId() {
		return uhuServiceId;
	}
	
	public String getUhuEventId() {
		return uhuEventId;
	}

	@Override
	public String toString() {
		return "UhuServiceId{" +
				"uhuServiceId='" + uhuServiceId + '\'' +
				"uhuEventId='" + uhuEventId + '\'' +
				'}';
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((uhuEventId == null) ? 0 : uhuEventId.hashCode());
		result = prime * result
				+ ((uhuServiceId == null) ? 0 : uhuServiceId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UhuServiceId other = (UhuServiceId) obj;
		if (uhuEventId == null) {
			if (other.uhuEventId != null)
				return false;
		} else if (!uhuEventId.equals(other.uhuEventId))
			return false;
		if (uhuServiceId == null) {
			if (other.uhuServiceId != null)
				return false;
		} else if (!uhuServiceId.equals(other.uhuServiceId))
			return false;
		return true;
	}

	

	
}