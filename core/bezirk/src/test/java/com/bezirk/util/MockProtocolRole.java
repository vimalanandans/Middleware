/**
 * Copyright (C) 2014 Robert Bosch, LLC. All Rights Reserved.
 *
 * Authors: Joao de Sousa, 2014
 *          Mansimar Aneja, 2014
 *          Vijet Badigannavar, 2014
 *          Samarjit Das, 2014
 *          Cory Henson, 2014
 *          Sunil Kumar Meena, 2014
 *          Adam Wynne, 2014
 *          Jan Zibuschka, 2014
 */
package com.bezirk.util;

import com.bezirk.api.messages.ProtocolRole;

/**
 * This is mock protocol role used in unit testing.
 * @author AJC6KOR
 *
 */

public class MockProtocolRole extends ProtocolRole {
	
	
	private static final String description = "Protocol Used for testing.";
	private String protocolName = getClass().getSimpleName();
	private final String[] eventTopics = new String[] {"MockEvent1","MockEvent2"};
	private final String[] streamTopics = new String[] {"MockStream1","MockStream2"};
	

	/* (non-Javadoc)
	 * @see ProtocolRole#getProtocolName()
	 */
	@Override
	public String getProtocolName() {
		// TODO Auto-generated method stub
		return protocolName;
	}

	/* (non-Javadoc)
	 * @see ProtocolRole#getDescription()
	 */
	@Override
	public String getDescription() {
		return description;
	}

	/* (non-Javadoc)
	 * @see ProtocolRole#getEventTopics()
	 */
	@Override
	public String[] getEventTopics() {
		return eventTopics;
	}

	/* (non-Javadoc)
	 * @see ProtocolRole#getStreamTopics()
	 */
	@Override
	public String[] getStreamTopics() {
		return streamTopics;
	}

}
