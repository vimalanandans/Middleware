/**
 * Copyright (C) 2014 Robert Bosch, LLC. All Rights Reserved.
 * <p/>
 * Authors: Joao de Sousa, 2014
 * Mansimar Aneja, 2014
 * Vijet Badigannavar, 2014
 * Samarjit Das, 2014
 * Cory Henson, 2014
 * Sunil Kumar Meena, 2014
 * Adam Wynne, 2014
 * Jan Zibuschka, 2014
 */
package com.bezirk.middleware.addressing;


/**
 * Bezirk generates objects that implement this interface.
 * These objects represent a network end point for a Zirk, which can be sent across the network and used in unicasts.
 */
public interface ZirkEndPoint {

    public boolean equals(Object obj);
}
