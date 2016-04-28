package com.bezirk.middleware.objects;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * This testcase verifies the BezirkPipeInfo by setting the properties and retrieving them.
 *
 * @author AJC6KOR
 */

public class BezirkPipeInfoTest {

    @Test
    public void test() {

        String pipeId = "Pipe24";
        String pipeName = "TestPipe";
        String pipeURL = "http://test.com";
        BezirkPipeInfo bezirkPipeInfo = new BezirkPipeInfo(pipeId, pipeName, pipeURL);

        assertEquals("PipeId is not equal to the set value.", pipeId, bezirkPipeInfo.getPipeId());
        assertEquals("PipeName is not equal to the set value.", pipeName, bezirkPipeInfo.getPipeName());
        assertEquals("PipeURL is not equal to the set value.", pipeURL, bezirkPipeInfo.getPipeURL());

        pipeName = "TestPipeTemp";
        BezirkPipeInfo bezirkPipeInfoTemp = new BezirkPipeInfo(pipeId, pipeName, pipeURL);
        assertFalse("Different bezirkPipeInfo has same string representation.", bezirkPipeInfo.toString().equalsIgnoreCase(bezirkPipeInfoTemp.toString()));
    }

}
