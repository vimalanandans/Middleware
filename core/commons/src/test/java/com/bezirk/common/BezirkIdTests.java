package com.bezirk.common;

import com.bezirk.commons.BezirkId;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by vnd2kor on 2/25/2015.
 */
public class BezirkIdTests {
    private static final Logger log = LoggerFactory.getLogger(BezirkIdTests.class);

    @Test
    // test the uniqueness of the short id
    public final void testShortId() {

        // create a map of hash id's as key
        Map<String, Integer> data = new HashMap<String, Integer>();

        // create a list of hash ids and push the data
        // while pushing check if it already exist. if yes then it is not unique
        int count = 0;

        // tested for count 10000000L. for faster build reduced to 100
        for (count = 0; count < 100L; count++) {
            String id = new BezirkId().getId();
            if (data.containsKey(id)) {
                log.error("it is not unique id. Already added in " + data.get(id) + " id > " + id + " at " + count);
                assertTrue(false);
                return;
            }

            data.put(id, count);
            //logger.info(".");
            //logger.info(" short Id for "+count+" > " +id);
        }
        log.info("id is unique with trails of " + count);

        // logger.info(" short Id of 'UHU' > "+new ShortUUID().getShortIdByHash("UHU"));
        //  logger.info(" short Id of 'UHU' > "+new ShortUUID().getShortIdByHash("UHU"));

    }

    @Test
    // test the short id for the given name. (note this is short id not unique id)
    // hence two different parities shall find the id based
    public final void testShortIdConversion() {

        BezirkId shortId = new BezirkId();
        //String uniqueId = "87f78d90-4b95-49f5-b720-68b3f8add3ba";
        String uniqueId = shortId.getId();

        String shortId1 = shortId.getShortIdByHash(uniqueId);
        String shortId2 = shortId.getShortIdByHash(uniqueId);

        log.info("short id for " + uniqueId + " .id1 > " + shortId1 + " id2 > " + shortId2);

        String shortId3 = shortId.getShortIdByName(BezirkIdTests.class.getName());
        String shortId4 = shortId.getShortIdByName(BezirkIdTests.class.getName());

        log.info("short id for " + BezirkIdTests.class.getName() + " > id3 > " + shortId3 + " > id4 >" + shortId4);

        assertTrue(shortId1 != shortId2);
        assertTrue(shortId3 != shortId4);
        /*
        logger.info("short id for "+uniqueId+" > " + new UhuUUIID().getShortIdByHash(uniqueId));

        logger.info("short id for "+uniqueId+" > " + new UhuUUIID().getShortIdByHash(uniqueId));
        logger.info("short id for "+uniqueId+" > " + new UhuUUIID().getShortIdByHash(uniqueId));*/

    }

    @Test
    public final void testHexConversionAndGetShortId() {

        String testString = "test";

        BezirkId bezirkId = new BezirkId();

        String hexValue = bezirkId.convertStringtoHex(testString);

        assertEquals("String cannot be retrieved from Hex format.", testString, bezirkId.convertHexToString(hexValue));

        assertNotNull("ShortId is not generated", bezirkId.getShortId());
    }
}
