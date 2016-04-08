package com.bezirk.protocols.penguin.v01;


public class Condition 
{
	/* properties */
	
	private ContextValue hasContextValue = null;
	private String hasOperator = null;
	
	/* constructors */
	
	/* getters and setters */
	
	// hasContextValue
	public void setContextValue (ContextValue _v) { this.hasContextValue = _v; }
	public ContextValue getContextValue () { return this.hasContextValue; }	
	
	// hasOperator
	public void setOperator (String _v) { this.hasOperator = _v; }
	public String getOperator () { return this.hasOperator; }
	
	
	@Override
	public String toString() {
		return "Condition [hasContextValue=" + hasContextValue
				+ ", hasOperator=" + hasOperator + "]";
	}
	
	
}
