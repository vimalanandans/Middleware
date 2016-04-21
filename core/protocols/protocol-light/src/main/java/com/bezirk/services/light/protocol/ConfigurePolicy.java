package com.bezirk.services.light.protocol;

import com.bezirk.middleware.addressing.Location;
import com.bezirk.middleware.messages.Event;

/**
 * This Event is used to Configure policy to a Location
 *
 * @author Mansimar Aneja (mansimar.aneja@us.bosch.com)
 */
public class ConfigurePolicy extends Event {
    public final static String TOPIC = ConfigurePolicy.class.getSimpleName();

    private HueVocab.Policy policy;

    private Location location;

    private String presenceSentivity;

    private String king = "No-King";

    public ConfigurePolicy(Location loc, HueVocab.Policy policy, String presenceSentivity) {
        super(Stripe.NOTICE, TOPIC);
        this.location = loc;
        this.policy = policy;
        this.presenceSentivity = presenceSentivity;
    }

    public HueVocab.Policy getPolicy() {
        return policy;
    }

    public Location getLocation() {
        return location;
    }

    public String getPresenceSentivity() {
        return presenceSentivity;
    }

    public String getKing() {
        return king;
    }

    public void setKing(String king) {
        this.king = king;
    }
}
