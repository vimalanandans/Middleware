package com.bezirk.protocols.penguin.v01;

import com.bezirk.middleware.messages.Event;

import java.util.ArrayList;
import java.util.List;

public class GetUserProfile extends Event {
    /**
     * topic
     */
    public static final String topic = "get-user-profile";

	/* properties */

    private String user = null;
    private String service = null;
    //private Context context = null;
    private List<ContextValue> context = null;

	/* constructors */

    public GetUserProfile() {
        super(Flag.REQUEST, topic);
        context = new ArrayList<ContextValue>();
    }

    public GetUserProfile(String topic) {
        super(Flag.REQUEST, topic);
    }
	
	/* getters and setters */

    public static GetUserProfile deserialize(String json) {
        return Event.fromJSON(json, GetUserProfile.class);
    }

    public String getUser() {
        return this.user;
    }

    // user
    public void setUser(String _v) {
        this.user = _v;
    }

    public String getService() {
        return this.service;
    }

    // service
    public void setService(String _v) {
        this.service = _v;
    }

    public List<ContextValue> getContext() {
        return this.context;
    }

    // context
    //public void setContext (Context _v) { this.context = _v; }
    //public Context getContext () { return this.context; }
    public void setContext(List<ContextValue> _v) {
        this.context = _v;
    }

    public void addContext(ContextValue _v) {
        this.context.add(_v);
    }

}
