package com.bezirk.aip;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

/**
 *	 This testcase verifies the AnswerItem by setting the properties and retrieving them.
 * 
 * @author AJC6KOR
 *
 */
public class AnswerItemTest {


	@Test
	public void test() {

		List<String> about = new ArrayList<>();
		about.add("TOPIC");
		String answer ="Today's topic";
		double confidence =90.0;
		Context context= new Context();
		context.setUser("BOB");
		String format = "TEST_FORMAT";
		String source="TEST_SOURCE";
		
		AnswerItem answerItem = new AnswerItem();
		answerItem.setAbout(about);
		answerItem.setAnswer(answer);
		answerItem.setConfidence(confidence);
		answerItem.setContext(context);
		answerItem.setFormat(format);
		answerItem.setSource(source);
		
		assertEquals("About is not equal to the set value.",about,answerItem.getAbout());
		assertEquals("Answer is not equal to the set value.",answer,answerItem.getAnswer());
		assertEquals("ContextUser is not equal to the set value.",context.getUser(),answerItem.getContext().getUser());
		assertEquals("Format is not equal to the set value.",format,answerItem.getFormat());
		assertEquals("Source is not equal to the set value.",source,answerItem.getSource());
		assertEquals("Confidence is not equal to the set value.",Double.toString(confidence),Double.toString(answerItem.getConfidence()));

	}

}
