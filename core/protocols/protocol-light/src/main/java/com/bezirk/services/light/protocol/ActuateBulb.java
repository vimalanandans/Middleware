package com.bezirk.services.light.protocol;

import com.bezirk.middleware.addressing.Location;
import com.bezirk.middleware.messages.Event;

import java.util.Set;

/**
 * This event is used to actuate the bulb
 * Allowed operations : ON, OFF, TOGGLE
 *
 * @author Mansimar Aneja (mansimar.aneja@us.bosch.com)
 */
public class ActuateBulb extends Event {
    public final static String TOPIC = ActuateBulb.class.getSimpleName();
    private final HueVocab.Commands command;
    private HueVocab.Color color = HueVocab.Color.DEFAULT;
    private Set<Integer> lightNumber;
    private final Location location;
    private String hubIP;
    private String hubMac;
    private Boolean isVoiceInitiated = false;
    private String id = "";
    private Boolean byId = false;

    public ActuateBulb(Set<Integer> lightNumber, HueVocab.Commands command) {
        super(Flag.NOTICE, TOPIC);
        this.command = command;
        this.lightNumber = lightNumber;
        this.location = null;
        this.byId = true;

    }

    public ActuateBulb(Location location, HueVocab.Commands command) {
        super(Flag.NOTICE, TOPIC);
        this.command = command;
        this.lightNumber = null;
        this.location = location;
        this.byId = false;

    }

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

    public void setHubIP(String hubIP) {
        this.hubIP = hubIP;
    }

    public Set<Integer> getLightNumber() {
        return lightNumber;
    }

    public void setLightNumber(Set<Integer> lightNumber) {
        this.lightNumber = lightNumber;
    }

    public String getHubMac() {
        return hubMac;
    }

    public void setHubMac(String hubMac) {
        this.hubMac = hubMac;
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
