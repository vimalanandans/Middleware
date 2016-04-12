package com.bezirk.checksum;

import static org.junit.Assert.*;

import org.junit.Test;

public class CheckSumTest {

	@Test
	public void test() {

	 byte[] bytes ="Test".getBytes();
	 String hex = CheckSumUtil.bytesToHex(bytes);
	 assertNotNull("Hex returned by checksumutil is null. ",hex);
	 
	 byte[] dataToBeComputed="dataTobeComputed".getBytes();
	 byte[] checkSum = UhuCheckSum.computeCheckSum(dataToBeComputed);
	 assertNotNull("CheckSum returned by uhuchecksum is null. ",checkSum);
	 assertTrue("CheckSum returned by uhuchecksum is empy. ",checkSum.length>0);

	byte[] crc =  UhuCheckSum.computeCRC(dataToBeComputed);
	assertNotNull("CRC returned by uhuchecksum is null. ",crc);
	assertTrue("CRC returned by uhuchecksum is empy. ",crc.length>0);
	
	}

}
