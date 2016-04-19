package com.bezirk.services.light.protocol;

import com.bezirk.middleware.addressing.Location;
import com.bezirk.middleware.messages.Event;
/**
 * 
 * @author Mansimar Aneja (mansimar.aneja@us.bosch.com)
 * 
 * This Event is used to configure Location of a given Bulb id
 */
public class ConfigureBulbLocation extends Event{
	public static final String TOPIC = ConfigureBulbLocation.class.getSimpleName();

	private Integer id;
	private Location location;
	public ConfigureBulbLocation(Integer id, Location location) {
		super(Stripe.NOTICE, TOPIC);
		this.id = id;
		this.location = location;
	}
	public Integer getId() {
		return id;
	}
	public Location getLocation() {
		return location;
	}	
}
