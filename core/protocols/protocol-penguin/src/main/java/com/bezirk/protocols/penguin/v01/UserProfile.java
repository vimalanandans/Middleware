package com.bezirk.protocols.penguin.v01;

import com.bezirk.middleware.messages.Event;

import java.util.ArrayList;
import java.util.List;

public class UserProfile extends Event {
    /**
     * topic
     */
    public static final String topic = "user-profile";

	/* properties */

    private String id = null;
    private List<ConditionalProfileSubset> hasConditionalProfileSubset = null;
    private List<DefaultProfileSubset> hasDefaultProfileSubset = null;

	/* constructors */

    public UserProfile() {
        super(Flag.REPLY, topic);
        this.hasConditionalProfileSubset = new ArrayList<ConditionalProfileSubset>();
        this.hasDefaultProfileSubset = new ArrayList<DefaultProfileSubset>();
    }
	
	/* getters and setters */

    /**
     * Use instead of the generic UhuMessage.fromJson()
     *
     * @param json
     * @return Profile
     */
    public static UserProfile deserialize(String json) {
        return Event.fromJson(json, UserProfile.class);
    }

    public String getId() {
        return this.id;
    }

    // id
    public void setId(String _v) {
        this.id = _v;
    }

    public List<ConditionalProfileSubset> getConditionalProfileSubset() {
        return this.hasConditionalProfileSubset;
    }

    // hasConditionalProfileSubset
    public void setConditionalProfileSubset(List<ConditionalProfileSubset> _v) {
        this.hasConditionalProfileSubset = _v;
    }

    public void addConditionalProfileSubset(ConditionalProfileSubset _v) {
        this.hasConditionalProfileSubset.add(_v);
    }

    public List<DefaultProfileSubset> getDefaultProfileSubset() {
        return this.hasDefaultProfileSubset;
    }

    // hasDefaultProfileSubset
    public void setDefaultProfileSubset(List<DefaultProfileSubset> _v) {
        this.hasDefaultProfileSubset = _v;
    }

    public void addDefaultProfileSubset(DefaultProfileSubset _v) {
        this.hasDefaultProfileSubset.add(_v);
    }

}
