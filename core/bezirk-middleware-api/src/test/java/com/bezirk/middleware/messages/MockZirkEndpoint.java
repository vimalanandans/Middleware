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
package com.bezirk.middleware.messages;

import com.bezirk.middleware.addressing.ZirkEndPoint;

/**
 * This is a mock Zirk endpoint used for unit testing.
 */
public class MockZirkEndpoint implements ZirkEndPoint {
    private String zirkId = null;

    /**
     * @param zirkId
     */
    public MockZirkEndpoint(String zirkId) {
        super();
        this.zirkId = zirkId;
    }

    public MockZirkEndpoint() {
        super();
    }

    public String getZirkId() {
        return zirkId;
    }

    /**
     * @param zirkId the zirkId to set
     */
    public void setZirkId(String zirkId) {
        this.zirkId = zirkId;
    }

}
