/**
 * GetPreference
 *
 * @author Cory Henson
 * @modified 09/16/2014
 */
package com.bezirk.protocols.penguin.v01;

import com.bezirk.middleware.messages.Event;
import com.bezirk.protocols.context.Context;

public class GetPreference extends Event {
    /**
     * topic
     */
    public static final String topic = "get-preference";

	/* properties */

    String type = null;
    String user = null;
    Context context = null;
    String service = null;

    /*
     * @Himadri Sikhar Khargharia
     * This overloaded constructor is added for allowing GetPreferenceTest to extend GetPreference
     *
     */
    public GetPreference(String topic) {
        super(Flag.REQUEST, topic);
    }

    public GetPreference() {
        super(Flag.REQUEST, topic);
    }

    public GetPreference(String _user,
                         String _type,
                         Context _context) {
        super(Flag.REQUEST, topic);
        this.setUser(_user);
        this.setType(_type);
        this.setContext(_context);
    }
	
	
	/* getters and setters */

    /**
     * Use instead of the generic UhuMessage.fromJson()
     * @param json
     * @return GetPreference
     */
    public static GetPreference deserialize(String json) {
        return Event.fromJson(json, GetPreference.class);
    }

    public String getType() {
        return this.type;
    }

    // type
    public void setType(String _v) {
        this.type = _v;
    }

    public String getUser() {
        return this.user;
    }

    // user
    public void setUser(String _v) {
        this.user = _v;
    }

    public Context getContext() {
        return this.context;
    }

    // context
    public void setContext(Context _v) {
        this.context = _v;
    }

    public String getService() {
        return this.service;
    }

    // service
    public void setService(String _v) {
        this.service = _v;
    }
}