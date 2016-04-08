/**
 * Preference
 *
 * @author Cory Henson
 * @modified 09/16/2014 
 */
package com.bezirk.protocols.penguin.v01;

//import Event;

public class Preference /* extends Event */ 
{
	/**
	 * topic
	 */
	//public static final String topic = "preference";
	
	/* properties */
	
	// required properties
	private String format = null;	
	private String type = null; 	
	private String user = null;		
	private String value = null;	
	
	// optional properties
	private double confidence = -1;
	
	//Source as to which preference engine created the preference
	private String source;
	
	//Context Parameters introduced
	private String location;
    private String partOfDay;
    private String dateTime;
	
	/* constructors */

	public Preference () 
	{
		//super (Stripe.REPLY, topic);
	}
	
	public Preference ( String _user, 
						String _type, 
						String _value,
						String _format, 
						double _confidence ) 
	{
		//super (Stripe.REPLY, topic);
		this.setUser(_user);
		this.setType(_type);
		this.setFormat(_format);
		this.setValue(_value);
		this.setConfidence(_confidence);	
	}
	
	/* getters and setters */

	// confidence
	public void setConfidence (double _v) { this.confidence = _v; }
	public double getConfidence() { return this.confidence; }
	
	// source
	public String getSource() { return source; }
	public void setSource(String source) { this.source = source; }
	
	// format
	public void setFormat (String _v) { this.format = _v; }
	public String getFormat () { return this.format; }
	
	// type
	public void setType (String _v) { this.type = _v; }
	public String getType () { return this.type; }
	
	// user
	public void setUser (String _v) { this.user = _v; }
	public String getUser () { return this.user; }
	
	// value
	public void setValue (String _v) { this.value = _v; }
	public String getValue () { return this.value; }
	
	// location
	public String getLocation() { return location; }
	public void setLocation(String location) { this.location = location; }

	// partOfDay
	public String getPartOfDay() { return partOfDay; }
	public void setPartOfDay(String partOfDay) { this.partOfDay = partOfDay; }

	// dateTime
	public String getDateTime() { return dateTime; }
	public void setDateTime(String dateTime) { this.dateTime = dateTime; }
	
	/**
	 * Use instead of the generic UhuMessage.deserialize()
	 * @param json
	 * @return Preference
	 */
	/*
	public static Preference deserialize(String json) {
		return Event.deserialize(json, Preference.class);
	}
	*/
}

