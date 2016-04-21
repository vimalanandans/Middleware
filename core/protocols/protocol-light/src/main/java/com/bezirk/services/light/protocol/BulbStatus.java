package com.bezirk.services.light.protocol;

import com.bezirk.middleware.addressing.Location;
import com.bezirk.middleware.messages.Event;
import com.bezirk.services.light.protocol.HueVocab.BulbStatusType;

public class BulbStatus extends Event {
    public final static String TOPIC = BulbStatus.class.getSimpleName();
    private HueVocab.Commands command;
    private HueVocab.Color color = HueVocab.Color.DEFAULT;
    private Integer lightNumber;
    private Location location;
    private String id = "";
    private Boolean byId = false;
    private HueVocab.BulbStatusType type = BulbStatusType.STATUS;

    public BulbStatus(Integer lightNumber, HueVocab.Commands command) {
        super(Stripe.NOTICE, TOPIC);
        this.command = command;
        this.lightNumber = lightNumber;
        this.location = null;
        this.byId = true;

    }

    public BulbStatus(Location location, HueVocab.Commands command) {
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

    public Integer getLightNumber() {
        return lightNumber;
    }

    public Boolean getById() {
        return byId;
    }

    public Location getLocation() {
        return location;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public HueVocab.BulbStatusType getType() {
        return type;
    }

    public void setType(HueVocab.BulbStatusType type) {
        this.type = type;
    }


}