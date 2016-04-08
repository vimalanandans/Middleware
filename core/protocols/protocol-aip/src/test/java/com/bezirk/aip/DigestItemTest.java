package com.bezirk.aip;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

/**
 *	 This testcase verifies DigestItem by setting the properties and retrieving them.
 * 
 * @author AJC6KOR
 *
 */
public class DigestItemTest {


	@Test
	public void test() {

		List<String> about= new ArrayList<>();
		about.add("TEST");
		double confidence =90.0;
		Context context=new Context();
		context.setUser("BOB");
		String image="Mock//File//Path";
		String link="http://mockurl.com";
		String source="book";
		String summary="summary";
		String title ="TITLE";
		
		DigestItem digestItem = new DigestItem();
		digestItem.setAbout(about);
		digestItem.setConfidence(confidence);
		digestItem.setContext(context);
		digestItem.setImage(image);
		digestItem.setLink(link);
		digestItem.setSource(source);
		digestItem.setSummary(summary);
		digestItem.setTitle(title);
		
		assertEquals("About is not equal to the set value.",about,digestItem.getAbout());
		assertEquals("ContextUser is not equal to the set value.",context.getUser(),digestItem.getContext().getUser());
		assertEquals("Image is not equal to the set value.",image,digestItem.getImage());
		assertEquals("Link is not equal to the set value.",link,digestItem.getLink());
		assertEquals("Source is not equal to the set value.",source,digestItem.getSource());
		assertEquals("Summary is not equal to the set value.",summary,digestItem.getSummary());
		assertEquals("Title is not equal to the set value.",title,digestItem.getTitle());
		assertEquals("Confidence is not equal to the set value.",Double.toString(confidence),Double.toString(digestItem.getConfidence()));
		
	
	}

}
