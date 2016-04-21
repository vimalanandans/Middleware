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
 * If one of these objects represents the identity of a service s1, it may be used by s1 for
 * interactions with Bezirk, and may also be used by Bezirk for sphere management.
 */
public interface ServiceId {

    public boolean equals(Object obj);


}
