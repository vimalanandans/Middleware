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

import com.bezirk.middleware.addressing.ServiceEndPoint;

/**
 * This is a mock service endpoint used for unit testing.
 *
 * @author AJC6KOR
 *
 */
public class MockServiceEndpoint implements ServiceEndPoint {

    private String serviceId = null;


    /**
     * @param serviceId
     */
    public MockServiceEndpoint(String serviceId) {
        super();
        this.serviceId = serviceId;
    }

    public MockServiceEndpoint() {
        super();
    }

    public String getServiceId() {
        return serviceId;
    }

    /**
     * @param serviceId the serviceId to set
     */
    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

}
