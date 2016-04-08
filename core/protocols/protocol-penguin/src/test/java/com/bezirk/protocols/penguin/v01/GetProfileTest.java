package com.bezirk.protocols.penguin.v01;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.bezirk.protocols.context.Context;
import com.bezirk.protocols.context.exception.UserPreferenceException;
import com.bezirk.protocols.penguin.v01.test.GetUserProfileTest;
import com.bezirk.api.addressing.Location;

/**
 * This testcase verifies the GetUserProfileTest event by setting the properties and retrieving them after deserialization.
 * 
 * @author RHR8KOR
 *
 */
public class GetProfileTest {

	@Test
	public void test() throws UserPreferenceException {
		
		Location loc = new Location("Office", "Canteen", "Desk 1");
		
		//2008-03-01T13:00:00+01:00
		Context context = new Context();
		context.setDateTime("2001-07-04 12:08:56.235-0700");
		context.setLocation(loc);		
		context.setPartOfDay("Staturday");
		
		String serviceName = "Service AB";
		String testID = "ID-1";
		String testSampleID = "SB-1";
		String testSampleBatchID = "SB";
		String dbFile = "DB_FILE_PATH";
		
		GetUserProfileTest getProfTest = new GetUserProfileTest("BOB", "Public", context, testID, testSampleID, testSampleBatchID,dbFile);	 
		
		String jsonSer = getProfTest.serialize();
		GetUserProfileTest getUserProfSer = GetUserProfileTest.deserialize(jsonSer);
		assertEquals(testID, getUserProfSer.getTestID());
		
		
		GetUserProfileTest getProfTestN = new GetUserProfileTest();
		
		getProfTestN.setService(serviceName);
		
		getProfTestN.setTestID(testID);
		
		getProfTestN.setTestSampleID(testSampleID);
		
		getProfTestN.setTestSampleBatchID(testSampleBatchID);
		
		getProfTestN.setDbFile(dbFile);
		
		ContextValue contextValue1 = new ContextValue("Type 1", "Value 1");
		ContextValue contextValue2 = new ContextValue("Type 2", "Value 2");
		
		List<ContextValue> contextValueList = new ArrayList<ContextValue>();
		contextValueList.add(contextValue1);
		contextValueList.add(contextValue2);
		
		getProfTestN.setContext(contextValueList);
		
		ContextValue contextValue3 = new ContextValue("Type 3", "Value 3");
		getProfTestN.addContext(contextValue3);
		
		String serializedProfTest = getProfTestN.serialize();
		GetUserProfileTest deserializedGetProfTest = GetUserProfileTest.deserialize(serializedProfTest);
		
		assertEquals("Service Name is not equal to the set name.", serviceName,deserializedGetProfTest.getService());
		assertEquals("TestID is not equal to the set TestID.",testID, deserializedGetProfTest.getTestID());
		assertEquals("SampleID is not equal to the set SampleID.", testSampleID,deserializedGetProfTest.getTestSampleID());
		assertEquals("SampleBatchID is not equal to the set SampleBatchID.",testSampleBatchID ,deserializedGetProfTest.getTestSampleBatchID());
		assertEquals("DBFile is not equal to the set DBFile.",dbFile, deserializedGetProfTest.getDbFile());
		assertEquals("Context List size is not equal to the set context size.",3, deserializedGetProfTest.getContext().size());
		
		
 	}

}
