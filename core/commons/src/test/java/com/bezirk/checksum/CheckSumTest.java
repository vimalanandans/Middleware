//package com.bezirk.checksum;
//
//import org.junit.Test;
//
//import static org.junit.Assert.*;
//
//public class CheckSumTest {
//
//    @Test
//    public void test() {
//
//        byte[] bytes = "Test".getBytes();
//        String hex = CheckSumUtil.bytesToHex(bytes);
//        assertNotNull("Hex returned by checksumutil is null. ", hex);
//
//        byte[] dataToBeComputed = "dataTobeComputed".getBytes();
//        byte[] checkSum = BezirkCheckSum.computeCheckSum(dataToBeComputed);
//        assertNotNull("CheckSum returned by bezirkchecksum is null. ", checkSum);
//        assertTrue("CheckSum returned by bezirkchecksum is empy. ", checkSum.length > 0);
//
//        byte[] crc = BezirkCheckSum.computeCRC(dataToBeComputed);
//        assertNotNull("CRC returned by bezirkchecksum is null. ", crc);
//        assertTrue("CRC returned by bezirkchecksum is empy. ", crc.length > 0);
//
//    }
//
//}
