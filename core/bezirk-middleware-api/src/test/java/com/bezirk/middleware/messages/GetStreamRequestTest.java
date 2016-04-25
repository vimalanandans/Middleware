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

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * This testcase verifies the GetStreamRequest by setting the properties and retrieving them after deserialization.
 *
 * @author AJC6KOR
 */
public class GetStreamRequestTest {

    @Test
    public void test() {

        String subTopic = "TestSubTopic";
        com.bezirk.middleware.messages.GetStreamRequest getStreamRequest = new GetStreamRequest(subTopic);
        String serializedgetStreamRequest = getStreamRequest.toJson();
        GetStreamRequest deserializedRequest = GetStreamRequest.deserialize(serializedgetStreamRequest);

        assertEquals("Subtopic is not equal to the set value.", subTopic, deserializedRequest.getSubTopic());

    }

}
