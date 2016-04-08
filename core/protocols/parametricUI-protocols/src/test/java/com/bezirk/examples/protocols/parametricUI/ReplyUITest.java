package com.bezirk.examples.protocols.parametricUI;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

/**
 *This testcase verifies the ReplyUIChoices and ReplyUIValues events by retrieving the properties after deserialization.
 *
 * 
 * @author AJC6KOR
 *
 */
public class ReplyUITest {


	@Test
	public void test() {

		testReplyUIChoices();
		
		testReplyUIValues();
		

	}


	private void testReplyUIChoices() {
		int[] selectedChoices=new int[]{23,24,25};
		ReplyUIchoices replyUIChoices = new ReplyUIchoices(selectedChoices);
	
		String serializedReplyUIChoices = replyUIChoices.serialize();
		ReplyUIchoices deserializedReplyUIChoices = ReplyUIchoices.deserialize(serializedReplyUIChoices);
		
		int[] deserializedSelectedChoices = deserializedReplyUIChoices.getSelectedChoices();
		
		assertTrue("SelectedChoices is not equal to the set value.",Arrays.equals(selectedChoices, deserializedSelectedChoices));
	}
	
	private void testReplyUIValues() {
		InputValuesStringPair inputValuesStringPair = new InputValuesStringPair();
		inputValuesStringPair.value ="25";
		inputValuesStringPair.unit="cm";
		
		InputValuesStringPair[] values = new InputValuesStringPair[]{inputValuesStringPair};
		ReplyUIvalues replyUIValues = new ReplyUIvalues(values);
		
		String serializedReplyUIValues = replyUIValues.serialize();
		
		ReplyUIvalues deserializedReplyUIValues = ReplyUIvalues.deserialize(serializedReplyUIValues);
		
		assertEquals("ReplyUIValues is not equal to the set value.",inputValuesStringPair.value,deserializedReplyUIValues.getValues()[0].value);
		assertEquals("ReplyUIValues is not equal to the set value.",inputValuesStringPair.unit,deserializedReplyUIValues.getValues()[0].unit);
	}
	

}
