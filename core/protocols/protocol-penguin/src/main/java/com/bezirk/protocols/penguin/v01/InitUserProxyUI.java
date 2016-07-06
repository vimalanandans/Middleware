package com.bezirk.protocols.penguin.v01;

import com.bezirk.middleware.messages.Event;

public class InitUserProxyUI extends Event {
    /**
     * topic
     */
    public static final String topic = "init-user-proxy-ui";

	/* properties */

    private String user = null;

	/* constructors */

    public InitUserProxyUI() {
        super(Flag.REQUEST, topic);
    }
	
	/* getters and setters */

    public static InitUserProxyUI deserialize(String json) {
        return Event.fromJson(json, InitUserProxyUI.class);
    }

    public String getUser() {
        return this.user;
    }

    // user
    public void setUser(String _v) {
        this.user = _v;
    }

}