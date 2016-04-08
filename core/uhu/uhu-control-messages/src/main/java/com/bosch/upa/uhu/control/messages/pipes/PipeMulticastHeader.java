package com.bosch.upa.uhu.control.messages.pipes;

import com.bezirk.api.addressing.Address;
import com.bezirk.api.addressing.Pipe;
import com.bezirk.api.serialization.InterfaceAdapter;
import com.bosch.upa.uhu.control.messages.MulticastHeader;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class PipeMulticastHeader extends PipeHeader {
	
	private Address address;
	
	public MulticastHeader toMulticastHeader() {
		MulticastHeader multicastHeader = new MulticastHeader();
		multicastHeader.setAddress(this.getAddress());
		multicastHeader.setSenderSEP(this.getSenderSEP());
		multicastHeader.setTopic(this.getTopic());

		// TODO: Do these need to be set??
		//multicastHeader.setMessageId(?);
		//multicastHeader.setSphereName(sphereName);
		
		return multicastHeader;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}
	
	public static <C> C deserialize(String json, Class<C> clazz) {
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(Pipe.class, new InterfaceAdapter<Pipe>());
		Gson gson = builder.create();
		return (C) gson.fromJson(json, clazz);
	}
	
	public String serialize() {
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(Pipe.class, new InterfaceAdapter<Pipe>());
		Gson gson = builder.create();
		return gson.toJson(this);
	}
}
