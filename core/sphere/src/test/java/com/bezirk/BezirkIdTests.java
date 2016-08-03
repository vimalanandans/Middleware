//package com.bezirk;
//
//import com.bezirk.commons.BezirkId;
//
//import org.junit.Test;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNotNull;
//import static org.junit.Assert.assertTrue;
//
//public class BezirkIdTests {
//    private static final Logger logger = LoggerFactory.getLogger(BezirkIdTests.class);
//
//    @Test
//    // test the uniqueness of the short id
//    public final void testShortId() {
//
//        // create a map of hash id's as key
//        Map<String, Integer> data = new HashMap<String, Integer>();
//
//        // create a list of hash ids and push the data
//        // while pushing check if it already exist. if yes then it is not unique
//        int count;
//
//        // tested for count 10000000L. for faster build reduced to 100
//        for (count = 0; count < 100L; count++) {
//            String id = new BezirkId().getId();
//            if (data.containsKey(id)) {
//                logger.error("it is not unique id. Already added in " + data.get(id) + " id > " + id + " at " + count);
//                assertTrue(false);
//                return;
//            }
//
//            data.put(id, count);
//            //logger.info(".");
//            //logger.info(" short Id for "+count+" > " +id);
//        }
//        logger.info("id is unique with trails of " + count);
//
//        // logger.info(" short Id of 'BEZIRK' > "+new ShortUUID().getShortIdByHash("BEZIRK"));
//        //  logger.info(" short Id of 'BEZIRK' > "+new ShortUUID().getShortIdByHash("BEZIRK"));
//
//    }
//
//    @Test
//    // test the short id for the given name. (note this is short id not unique id)
//    // hence two different parities shall find the id based
//    public final void testShortIdConversion() {
//
//        BezirkId shortId = new BezirkId();
//        //String uniqueId = "87f78d90-4b95-49f5-b720-68b3f8add3ba";
//        String uniqueId = shortId.getId();
//
//        String shortId1 = shortId.getShortIdByHash(uniqueId);
//        String shortId2 = shortId.getShortIdByHash(uniqueId);
//
//        logger.info("short id for " + uniqueId + " .id1 > " + shortId1 + " id2 > " + shortId2);
//
//        String shortId3 = shortId.getShortIdByName(BezirkIdTests.class.getName());
//        String shortId4 = shortId.getShortIdByName(BezirkIdTests.class.getName());
//
//        logger.info("short id for " + BezirkIdTests.class.getName() + " > id3 > " + shortId3 + " > id4 >" + shortId4);
//
//        assertEquals(shortId1, shortId2);
//        assertEquals(shortId3, shortId4);
//    }
//
//    @Test
//    public final void testHexConversionAndGetShortId() {
//
//        String testString = "test";
//
//        BezirkId bezirkId = new BezirkId();
//
//        String hexValue = bezirkId.toHex(testString);
//
//        assertEquals("String cannot be retrieved from Hex format.", testString, bezirkId.convertHexToString(hexValue));
//
//        assertNotNull("ShortId is not generated", bezirkId.getShortId());
//    }
//}
