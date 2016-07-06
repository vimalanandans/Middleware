package com.bezirk.protocols.penguin.v01.test;

import com.bezirk.middleware.messages.Event;
import com.bezirk.protocols.context.Context;
import com.bezirk.protocols.penguin.v01.GetUserProfile;

public class GetUserProfileTest extends GetUserProfile {
    /**
     * topic
     */
    public static final String topic = "get-user-profile-test";

    /* properties */
    private String testID = null;
    private String testSampleBatchID = null;
    private String testSampleID = null;
    private String dbFile = null;

	/* constructors */

    public GetUserProfileTest() {
        super(topic);
    }

    public GetUserProfileTest(String _user,
                              String _type,
                              Context _context, String testID, String testSampleBatchID, String testSampleID, String dbFile) {
        super(topic);
        this.setTestID(testID);
        this.setTestSampleBatchID(testSampleBatchID);
        this.setTestSampleID(testSampleID);
        this.setDbFile(dbFile);
    }

    /**
     * Use instead of the generic Message.fromJson()
     *
     * @param json
     * @return GetPreference
     */
    public static GetUserProfileTest deserialize(String json) {
        return Event.fromJson(json, GetUserProfileTest.class);
    }

    public String getDbFile() {
        return dbFile;
    }
	
	
	/* getters and setters */

    public void setDbFile(String dbFile) {
        this.dbFile = dbFile;
    }

    public String getTestID() {
        return testID;
    }

    public void setTestID(String testID) {
        this.testID = testID;
    }

    public String getTestSampleBatchID() {
        return testSampleBatchID;
    }

    public void setTestSampleBatchID(String testSampleBatchID) {
        this.testSampleBatchID = testSampleBatchID;
    }

    public String getTestSampleID() {
        return testSampleID;
    }

    public void setTestSampleID(String testSampleID) {
        this.testSampleID = testSampleID;
    }
}