package com.bezirk.api.objects;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 *	 This testcase verifies the UhuPipeInfo by setting the properties and retrieving them.
 * 
 * @author AJC6KOR
 *
 */

public class UhuPipeInfoTest {

	@Test
	public void test() {
		
		String pipeId ="Pipe24";
		String pipeName="TestPipe";
		String pipeURL="http://test.com";
		com.bezirk.api.objects.UhuPipeInfo uhuPipeInfo = new com.bezirk.api.objects.UhuPipeInfo(pipeId, pipeName, pipeURL);
	
		assertEquals("PipeId is not equal to the set value.",pipeId,uhuPipeInfo.getPipeId());
		assertEquals("PipeName is not equal to the set value.",pipeName,uhuPipeInfo.getPipeName());
		assertEquals("PipeURL is not equal to the set value.",pipeURL,uhuPipeInfo.getPipeURL());
		
		pipeName ="TestPipeTemp";
		com.bezirk.api.objects.UhuPipeInfo uhuPipeInfoTemp = new com.bezirk.api.objects.UhuPipeInfo(pipeId, pipeName, pipeURL);
		assertFalse("Different uhuPipeInfo has same string representation.",uhuPipeInfo.toString().equalsIgnoreCase(uhuPipeInfoTemp.toString()));
	}

}
