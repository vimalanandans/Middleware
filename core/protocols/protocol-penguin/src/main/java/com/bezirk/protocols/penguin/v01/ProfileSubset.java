package com.bezirk.protocols.penguin.v01;

public class ProfileSubset {
    /* properties */

    private UserModel hasUserModel = null;
    //private List<Preference> hasUserPreference = null;
    private String isSpecificTo = null; // specificService
	
	/* constructors */

    public ProfileSubset() {
        this.hasUserModel = new UserModel();
        //this.hasUserPreference = new ArrayList<Preference>();
    }
	
	/* getters and setters */

    public UserModel getUserModel() {
        return this.hasUserModel;
    }

    // hasUserModel
    public void setUserModel(UserModel _v) {
        this.hasUserModel = _v;
    }

    // hasUserPreference
    //public void setUserPreferences (List<Preference> _v) { this.hasUserPreference = _v; }
    //public List<Preference> getUserPreferences () { return this.hasUserPreference; }
    //public void addUserPreference (Preference _v) { this.hasUserPreference.add(_v); }

    public String getSpecificService() {
        return this.isSpecificTo;
    }

    // isSpecificTo
    public void setSpecificService(String _v) {
        this.isSpecificTo = _v;
    }
}
