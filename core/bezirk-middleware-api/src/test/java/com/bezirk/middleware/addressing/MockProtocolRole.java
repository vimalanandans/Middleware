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
package com.bezirk.middleware.addressing;

import com.bezirk.middleware.messages.ProtocolRole;

/**
 * This is a mockprotocol used for unit testing
 * 
 * @author AJC6KOR
 *
 */

class MockProtocolRole extends ProtocolRole {
	
	private String protocolName = getClass().getSimpleName();

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
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see ProtocolRole#getEventTopics()
	 */
	@Override
	public String[] getEventTopics() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see ProtocolRole#getStreamTopics()
	 */
	@Override
	public String[] getStreamTopics() {
		// TODO Auto-generated method stub
		return null;
	}

}
