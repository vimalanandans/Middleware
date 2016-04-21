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

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * This testcase verifies the Pipe by setting the properties and retrieving them after deserialization.
 *
 * @author AJC6KOR
 */
public class PipeTest {

    @Test
    public void test() {

        com.bezirk.middleware.addressing.Pipe pipe = new Pipe();
        String pipeName = "TestPipe";
        pipe.setName(pipeName);

        String serializedPipe = pipe.serialize();

        Pipe deserializedPipe = Pipe.deserialize(serializedPipe, Pipe.class);

        assertEquals("PipeName is not equal to the set value.", pipeName, deserializedPipe.getName());

        pipeName = "CheckPipe";
        assertNotEquals("PipeName is considered equal to the unkown value.", pipeName, deserializedPipe.getName());

    }

}
