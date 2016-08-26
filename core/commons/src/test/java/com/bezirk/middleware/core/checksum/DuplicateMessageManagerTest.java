//package com.bezirk.middleware.core.checksum;
//
//import org.junit.Test;
//
//import static org.junit.Assert.*;
//
//public class DuplicateMessageManagerTest {
//
//    @Test
//    public void test() {
//
//        byte[] message = "TestMessage".getBytes();
//        byte[] checksumOfMsg = BezirkCheckSum.computeCheckSum(message);
//        DuplicateMessageManager.checkDuplicateEvent(checksumOfMsg);
//    /*Same checksumMsg checked for duplicate*/
//        assertTrue("Old message is not considered as duplicate by duplicateMessageManager.", DuplicateMessageManager.checkDuplicateEvent(checksumOfMsg));
//
//        message = "NewMessage".getBytes();
//        checksumOfMsg = BezirkCheckSum.computeCheckSum(message);
//        assertFalse("New message is considered as duplicate by duplicateMessageManager.", DuplicateMessageManager.checkDuplicateEvent(checksumOfMsg));
//
//    }
//
//}
