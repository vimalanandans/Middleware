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
 * The middleware-generated identify of a Zirk. This ID is returned by
 * {@link com.bezirk.middleware.Bezirk#registerZirk(String)} and is used to interact with
 * the {@link com.bezirk.middleware.Bezirk} API.
 * <p>
 * The Bezirk middleware implements this interface.
 * </p>
 */
public interface ZirkId {
// For now, this is a marker interface because there is nothing to offer to a Zirk API wise.
}
