package com.bezirk.protocols.penguin.v01;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;


/**
 * This testcase includes the following tests :
 * 1) Verifies the QueryUserProxyRole by checking the event and stream topics.
 * 2) Verifies the InitUserProxyUI by setting the properties and retrieving them after deserialization.
 *
 * @author RHR8KOR
 */
public class ProxyTest {

    @Test
    public void test() {

        QueryUserProxyRole queryUserProxy = new QueryUserProxyRole();

        assertNull("QueryUserProxy Role getdescription is not null",
                queryUserProxy.getDescription());
        assertNotNull("QueryProxy Role getEventTopics is null",
                queryUserProxy.getEventTopics());
        assertEquals("QueryUserProxyRole", queryUserProxy.getProtocolName());
        assertNull("QueryUserProxy Role getStreamTopic is not null",
                queryUserProxy.getStreamTopics());

        InitUserProxyUI initProxy = new InitUserProxyUI();

        initProxy.setUser("INIT USER USER");
        assertEquals("INIT USER USER", initProxy.getUser());

        String json = initProxy.toJson();

        InitUserProxyUI initSer = new InitUserProxyUI().deserialize(json);
        assertEquals("INIT USER USER", initSer.getUser());

    }

}
