package com.bezirk.protocols.penguin.v01.test;

import com.bezirk.middleware.messages.Event;
import com.bezirk.protocols.context.Context;
import com.bezirk.protocols.penguin.v01.GetPreference;

/**
 * This class is a subclass of the GetPreference Class
 * and is used in the testing flow
 *
 * @author Himadri Sikhar Khargharia
 * @created 27/10/2014
 */
public class GetPreferenceTest extends GetPreference {
    /**
     * topic
     */
    public static final String topic = "get-preference-test";

    /* properties */
    private String testID = null;
    private String testSampleBatchID = null;
    private String testSampleID = null;

	/* constructors */

    public GetPreferenceTest() {
        super(topic);
    }

    public GetPreferenceTest(String _user,
                             String _type,
                             Context _context, String testID, String testSampleBatchID, String testSampleID) {
        super(topic);
        this.setTestID(testID);
        this.setTestSampleBatchID(testSampleBatchID);
        this.setTestSampleID(testSampleID);
    }
	
	
	/* getters and setters */

    /**
     * Use instead of the generic Message.fromJson()
     *
     * @param json
     * @return GetPreference
     */
    public static GetPreferenceTest deserialize(String json) {
        return Event.fromJson(json, GetPreferenceTest.class);
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
