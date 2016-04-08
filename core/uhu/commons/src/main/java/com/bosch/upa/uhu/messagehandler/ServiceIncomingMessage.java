package com.bosch.upa.uhu.messagehandler;

import com.bosch.upa.uhu.proxy.api.impl.UhuServiceId;
import com.google.gson.Gson;

/**
 * Base class of the Callback messages used in Uhu.
 */
public class ServiceIncomingMessage {
	/**
	 * Discriminator for the Callbacks
	 */
	public String callbackDiscriminator = null;;
	/**
	 * Recipeint of this msg
	 */
	protected UhuServiceId recipient;
	
	/**
	 * @return Json representation of the message as a String.
	 */
	public String serialize() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}
	
	/**
	 * @param json The Json String that is to be deserialized
	 * @param dC class to deserialize into
	 * @return object of class C 
	 */
	public static <C> C deserialize(String json, Class<C> dC) {
		Gson gson = new Gson();
		return (C) gson.fromJson(json, dC);
	}
	
	public String getCallbackType(){
		return callbackDiscriminator;
	}
	
	public UhuServiceId getRecipient(){
		return recipient;
	}
}
