package com.bezirk.middleware.addressing;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class PipeTest {

    @Test
    public void test() {

        com.bezirk.middleware.addressing.Pipe pipe = new Pipe();
        String pipeName = "TestPipe";
        pipe.setName(pipeName);

        String serializedPipe = pipe.toJson();

        Pipe deserializedPipe = Pipe.fromJson(serializedPipe, Pipe.class);

        assertEquals("PipeName is not equal to the set value.", pipeName, deserializedPipe.getName());

        pipeName = "CheckPipe";
        assertNotEquals("PipeName is considered equal to the unkown value.", pipeName, deserializedPipe.getName());

    }

}
