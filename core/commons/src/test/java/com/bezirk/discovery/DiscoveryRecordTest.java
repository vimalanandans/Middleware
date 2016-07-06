package com.bezirk.discovery;

import com.bezirk.proxy.api.impl.BezirkDiscoveredZirk;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.proxy.api.impl.ZirkId;

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
        com.bezirk.pubsubbroker.discovery.DiscoveryRecord discoveryRecord = new com.bezirk.pubsubbroker.discovery.DiscoveryRecord(timeout, max);

        assertEquals("Timeout is not equal to the set value.", timeout, discoveryRecord.getTimeout());
        assertEquals("Max is not equal to the set value.", max, discoveryRecord.getMax());

        assertNotNull("Unable to retrieve creation time.", discoveryRecord.getCreationTime());
        assertEquals("DiscoveredList size is not 0 at start.", 0, discoveryRecord.getDiscoveredListSize());
        assertNotNull("DiscoveredList is null.", discoveryRecord.getList());


        List<BezirkDiscoveredZirk> list = new ArrayList<BezirkDiscoveredZirk>();
        BezirkDiscoveredZirk discoveredZirk = new BezirkDiscoveredZirk();
        discoveredZirk.zirk = new BezirkZirkEndPoint(new ZirkId("ServiceA"));
        list.add(discoveredZirk);
        discoveryRecord.updateList(list);

        assertEquals("DiscoveredList size is not 1 after updation.", 1, discoveryRecord.getDiscoveredListSize());

        discoveryRecord.updateList(list);
        assertEquals("Duplicate zirk is added to discovered zirk list.", 1, discoveryRecord.getDiscoveredListSize());

    }


}
