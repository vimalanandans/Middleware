package com.bezirk.discovery;

import com.bezirk.proxy.api.impl.UhuDiscoveredZirk;
import com.bezirk.proxy.api.impl.UhuZirkEndPoint;
import com.bezirk.proxy.api.impl.UhuZirkId;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author ajc6kor
 */
public class DiscoveryRecordTest {

    @Test
    public void test() {

        long timeout = 60000;
        int max = 5;
        DiscoveryRecord discoveryRecord = new DiscoveryRecord(timeout, max);

        assertEquals("Timeout is not equal to the set value.", timeout, discoveryRecord.getTimeout());
        assertEquals("Max is not equal to the set value.", max, discoveryRecord.getMax());

        assertNotNull("Unable to retrieve creation time.", discoveryRecord.getCreationTime());
        assertEquals("DiscoveredList size is not 0 at start.", 0, discoveryRecord.getDiscoveredListSize());
        assertNotNull("DiscoveredList is null.", discoveryRecord.getList());


        List<UhuDiscoveredZirk> list = new ArrayList<UhuDiscoveredZirk>();
        UhuDiscoveredZirk discoveredService = new UhuDiscoveredZirk();
        discoveredService.service = new UhuZirkEndPoint(new UhuZirkId("ServiceA"));
        list.add(discoveredService);
        discoveryRecord.updateList(list);

        assertEquals("DiscoveredList size is not 1 after updation.", 1, discoveryRecord.getDiscoveredListSize());

        discoveryRecord.updateList(list);
        assertEquals("Duplicate service is added to discovered service list.", 1, discoveryRecord.getDiscoveredListSize());

    }


}
