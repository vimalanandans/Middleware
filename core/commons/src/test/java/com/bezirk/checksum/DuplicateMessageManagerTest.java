package com.bezirk.checksum;

import static org.junit.Assert.*;

import org.junit.Test;

public class DuplicateMessageManagerTest {

	@Test
	public void test() {

	byte[] message = "TestMessage".getBytes();
	byte[] checksumOfMsg = UhuCheckSum.computeCheckSum(message);
	DuplicateMessageManager.checkDuplicateEvent(checksumOfMsg);
	/*Same checksumMsg checked for duplicate*/
	assertTrue("Old message is not considered as duplicate by duplicateMessageManager.",DuplicateMessageManager.checkDuplicateEvent(checksumOfMsg));
	
	message = "NewMessage".getBytes();
	checksumOfMsg = UhuCheckSum.computeCheckSum(message);
	assertFalse("New message is considered as duplicate by duplicateMessageManager.",DuplicateMessageManager.checkDuplicateEvent(checksumOfMsg));

	}

}
