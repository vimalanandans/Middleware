package com.bezirk.protocols.penguin.v01;

import java.util.ArrayList;
import java.util.List;

public class UserModel {
    /* properties */

    private String hasName = null;
    private List<ContextValue> hasContext = null;
    private List<Preference> hasPreference = null;
	
	/* constructors */

    public UserModel() {
        this.hasContext = new ArrayList<ContextValue>();
        this.hasPreference = new ArrayList<Preference>();
    }
	
	/* getters and setters */

    public String getName() {
        return this.hasName;
    }

    // hasName
    public void setName(String _v) {
        this.hasName = _v;
    }

    public List<ContextValue> getContext() {
        return this.hasContext;
    }

    // context
    public void setContext(List<ContextValue> _v) {
        this.hasContext = _v;
    }

    public void addContext(ContextValue _v) {
        this.hasContext.add(_v);
    }

    public List<Preference> getPreferences() {
        return this.hasPreference;
    }

    // hasPreference
    public void setPreferences(List<Preference> _v) {
        this.hasPreference = _v;
    }

    public void addPreference(Preference _v) {
        this.hasPreference.add(_v);
    }
}
