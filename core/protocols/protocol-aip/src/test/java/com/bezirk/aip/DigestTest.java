package com.bezirk.aip;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

/**
 *	 This testcase verifies Digest event by setting the properties and retrieving them after deserialization.
 * 
 * @author AJC6KOR
 *
 */
public class DigestTest {


	@Test
	public void test() {
	
		String id="ID1";
		String subTopic="LOCATION";
		List<String> summaries= new ArrayList<>();
		summaries.add("navigation summary");
		
		Digest<String> digest = new Digest<>();
		digest.setId(id);
		digest.setSubTopic(subTopic);
		digest.setSummaries(summaries);
		
		String serializedDigest = digest.serialize();
		
		Digest<?> deserializedDigest = Digest.deserialize(serializedDigest,Digest.class);
		
		assertEquals("ID is not equal to the set value.",id,deserializedDigest.getId());
		assertEquals("SubTopic is not equal to the set value.",subTopic,deserializedDigest.getSubTopic());
		assertEquals("Summaries is not equal to the set value.",summaries,deserializedDigest.getSummaries());
		
	}

}
