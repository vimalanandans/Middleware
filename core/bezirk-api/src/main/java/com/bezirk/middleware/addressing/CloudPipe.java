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

import java.net.URI;

/**
 * Represents a pipe to a UhU-cloud instance hosted at URL - which may be redirected by the user via UhU UIs
 *
 */
public class CloudPipe extends Pipe {
	
	private URI uri;

	public CloudPipe(String name, URI uri) {
		super(name);
		this.uri = uri;
	}
	
	public boolean equals(Object thatObject) {
		if (thatObject instanceof CloudPipe) {
			URI thatUri = ((CloudPipe)thatObject).getURI();
			if (this.uri.equals(thatUri)) {
				return true;
			}
			else {
				return false;
			}
		}
		else  {
			return false;
		}
	}
	
	public int hashCode() {
		return uri.hashCode();
	}
	
	public URI getURI() {
		return uri;
	}
}
