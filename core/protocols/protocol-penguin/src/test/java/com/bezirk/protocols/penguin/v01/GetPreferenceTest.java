package com.bezirk.protocols.penguin.v01;

import com.bezirk.middleware.addressing.Location;
import com.bezirk.protocols.context.Context;
import com.bezirk.protocols.context.exception.UserPreferenceException;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * This testcase verifies the GetPreference event by setting the properties and retrieving them after deserialization.
 *
 * @author RHR8KOR
 */
public class GetPreferenceTest {

    @Test
    public void test() throws UserPreferenceException {

        GetPreference getPref = new GetPreference();

        Location loc = new Location("Office", "Cafe", "Gate");

        Context context = new Context();
        context.setDateTime("2001-07-04 12:08:56.235-0700");
        assertEquals("2001-07-04 12:08:56.235-0700", context.getDateTime());
        context.setLocation(loc);
        assertEquals("Office", context.getLocation().getRegion());
        context.setPartOfDay("Saturday");
        assertEquals("Saturday", context.getPartOfDay());

        getPref.setContext(context);
        assertEquals("Saturday", getPref.getContext().getPartOfDay());


        getPref.setService("Test Service 1");
        assertEquals("Test Service 1", getPref.getService());

        getPref.setType("Entertainment");
        assertEquals("Entertainment", getPref.getType());

        getPref.setUser("BOB");
        assertEquals("BOB", getPref.getUser());


        String jsonSer = getPref.serialize();
        GetPreference getPrefSer = GetPreference.deserialize(jsonSer);
        assertEquals("Saturday", getPrefSer.getContext().getPartOfDay());


        GetPreference getPrefN = new GetPreference("BOB", "Public", context);
        assertEquals("BOB", getPrefN.getUser());
        assertEquals("Public", getPrefN.getType());
        assertEquals("Saturday", getPrefN.getContext().getPartOfDay());


        com.bezirk.protocols.penguin.v01.test.GetPreferenceTest getPrefTestN = new com.bezirk.protocols.penguin.v01.test.GetPreferenceTest(null, null, null, "TEST ID",
                "TSB-ID", "TS-ID");


        assertEquals("TEST ID", getPrefTestN.getTestID());
        assertEquals("TSB-ID", getPrefTestN.getTestSampleBatchID());
        assertEquals("TS-ID", getPrefTestN.getTestSampleID());


        getPrefTestN = null;

        getPrefTestN = new com.bezirk.protocols.penguin.v01.test.GetPreferenceTest();


        getPrefTestN.setContext(context);
        assertEquals("2001-07-04 12:08:56.235-0700", getPrefTestN.getContext().getDateTime());


        getPrefTestN.setService("FM 101");
        assertEquals("FM 101", getPrefTestN.getService());


        getPrefTestN.setTestID("TEST 01 ID");
        assertEquals("TEST 01 ID", getPrefTestN.getTestID());


        getPrefTestN.setTestSampleBatchID("TEST S ID");
        assertEquals("TEST S ID", getPrefTestN.getTestSampleBatchID());


        getPrefTestN.setTestSampleID("SAMPLE ID 01");
        assertEquals("SAMPLE ID 01", getPrefTestN.getTestSampleID());


        getPrefTestN.setType("Private");
        assertEquals("Private", getPrefTestN.getType());


        getPrefTestN.setUser("UPA USER");
        assertEquals("UPA USER", getPrefTestN.getUser());

        String jsonSerr = getPrefTestN.serialize();
        getPrefTestN = null;
        getPrefTestN = new com.bezirk.protocols.penguin.v01.test.GetPreferenceTest();
        getPrefTestN = com.bezirk.protocols.penguin.v01.test.GetPreferenceTest.deserialize(jsonSerr);
        assertEquals("Private", getPrefTestN.getType());


    }

}
