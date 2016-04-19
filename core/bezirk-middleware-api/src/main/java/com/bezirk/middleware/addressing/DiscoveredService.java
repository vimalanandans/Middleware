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

/**
 * A tuple that characterizes a discovered service.
 */
public interface DiscoveredService {	
	
	public boolean equals(Object obj);
	
	public ServiceEndPoint getServiceEndPoint();
	
	public String getServiceName();
	
	public String getProtocol();
	
	public Location getLocation();
}
