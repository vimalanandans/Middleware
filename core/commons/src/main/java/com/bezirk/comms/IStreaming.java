package com.bezirk.comms;

import com.bezirk.sphere.api.IUhuSphereForSadl;
import com.bezirk.control.messages.Ledger;
import com.bezirk.streaming.control.Objects.StreamRecord;


/**
 * This interface is introduced to separate the streaming functionalities from UhuCommsManager.
 * 
 * @author ajc6kor
 *
 */
public interface IStreaming {
	

	/** Initialize the streaming queue, streaming thread,stream store and register the receivers with the message dispatcher. */
	public boolean initStreams();
	
	/** Start the streaming thread*/
	public boolean startStreams();
	
	/** Interrupt the streaming thread*/
	public boolean endStreams();
	
    /** send the stream message based on unique key*/
    public boolean sendStream(final String uniqueKey);
    
	/** send the stream message */
	public boolean sendStreamMessage(final Ledger message);
	
	/** Retrieve the portfactory instance*/
    public IPortFactory getPortFactory() ;

    /** Registers the stream record with stream store*/
    public boolean registerStreamBook(final String key, final StreamRecord sRecord);
    
    /** Set the sphere for sadl*/
    public void setSphereForSadl(final IUhuSphereForSadl uhuSphere);
}
