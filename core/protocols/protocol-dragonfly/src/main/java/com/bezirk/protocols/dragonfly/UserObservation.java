/**
 * UserObservation
 *
 * @author Cory Henson
 * @modified 10/27/2014 
 */
package com.bezirk.protocols.dragonfly;

public class UserObservation extends Observation 
{
	/**
	 * topic
	 */
	public static final String topic = "user-observation";
	
	/* constructors */
	
	public UserObservation () 
	{
		super(topic);
	}
	
	public UserObservation (String topic) 
	{
		super(topic);
	}
}
