package com.bezirk.aip;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

/**
 *	 This testcase verifies DisambiguationQuestion event by setting the properties and retrieving them after deserialization.
 * 
 * @author AJC6KOR
 *
 */
public class DisambiguationQuestionsTest {

	@Test
	public void test() {

		List<String> disambiguationQuestionList= new ArrayList<String>();
		disambiguationQuestionList.add("NEAR TO LAB OR ROOM1 ?");
		String id="QUESTION_3";
		String subTopic ="CONFUSED";
		
		DisambiguationQuestions disambiguationQuestions = new DisambiguationQuestions();
		disambiguationQuestions.setDisambiguationQuestions(disambiguationQuestionList);
		disambiguationQuestions.setId(id);
		disambiguationQuestions.setSubTopic(subTopic);
		
		String serializedDisambiguationQuestions = disambiguationQuestions.serialize();
		
		DisambiguationQuestions deserializedDisambiguationQuestions = DisambiguationQuestions.deserialize(serializedDisambiguationQuestions);
		assertEquals("DisambiguationQuestionList is not equal to the set value.",disambiguationQuestionList,deserializedDisambiguationQuestions.getDisambiguationQuestions());
		assertEquals("ID is not equal to the set value.",id,deserializedDisambiguationQuestions.getId());
		assertEquals("SubTopic is not equal to the set value.",subTopic,deserializedDisambiguationQuestions.getSubTopic());
				
	
	}

}
