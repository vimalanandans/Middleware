package com.bezirk.services.light.protocol;

import java.util.Set;

import com.bezirk.middleware.addressing.Location;
import com.bezirk.middleware.messages.Event;

/**
 * This event is used to actuate the bulb 
 * Allowed operations : ON, OFF, TOGGLE 
 * @author Mansimar Aneja (mansimar.aneja@us.bosch.com)
 *
 */
public class ActuateBulb extends Event{
	public final static String TOPIC = ActuateBulb.class.getSimpleName();
	private HueVocab.Commands command;
	private HueVocab.Color color = HueVocab.Color.DEFAULT;
	private Set<Integer> lightNumber;
	private Location location;
	private String hubIP;
	private String hubMac;
	private Boolean isVoiceInitiated = false;
	private String id = "";
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Boolean getIsVoiceInitiated() {
		return isVoiceInitiated;
	}

	public void setIsVoiceInitiated(Boolean isVoiceInitiated) {
		this.isVoiceInitiated = isVoiceInitiated;
	}

	public String getHubIP() {
		return hubIP;
	}

	public Set<Integer> getLightNumber() {
		return lightNumber;
	}

	public void setLightNumber(Set<Integer> lightNumber) {
		this.lightNumber = lightNumber;
	}

	public void setHubIP(String hubIP) {
		this.hubIP = hubIP;
	}

	public String getHubMac() {
		return hubMac;
	}

	public void setHubMac(String hubMac) {
		this.hubMac = hubMac;
	}

	private Boolean byId = false;
	
	public ActuateBulb(Set<Integer> lightNumber, HueVocab.Commands command) {
		super(Stripe.NOTICE, TOPIC);
		this.command = command;
		this.lightNumber = lightNumber;
		this.location = null;
		this.byId = true;
		
	}
	
	public ActuateBulb(Location location, HueVocab.Commands command) {
		super(Stripe.NOTICE, TOPIC);
		this.command = command;
		this.lightNumber = null;
		this.location = location;
		this.byId = false;
		
	}
	
	public HueVocab.Commands getCommand() {
		return command;
	}
	public HueVocab.Color getColor() {
		return color;
	}
	public void setColor(HueVocab.Color color) {
		this.color = color;
	}

	public Boolean getById() {
		return byId;
	}

	public Location getLocation() {
		return location;
	}
	
}
