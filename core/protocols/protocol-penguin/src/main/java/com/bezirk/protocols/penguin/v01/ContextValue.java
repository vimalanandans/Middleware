package com.bezirk.protocols.penguin.v01;

public class ContextValue 
{
	private String type = null;
	private String value = null;
	
	public ContextValue (String _type, String _value)
	{
		this.type = _type;
		this.value = _value;
	}
	
	// type
	public void setType (String _v) { this.type = _v; }
	public String getType () { return this.type; }
	
	// value
	public void setValue (String _v) { this.value = _v; }
	public String getValue () { return this.value; }

	@Override
	public String toString() {
		return "ContextValue [type=" + type + ", value=" + value + "]";
	}
	
	
}
