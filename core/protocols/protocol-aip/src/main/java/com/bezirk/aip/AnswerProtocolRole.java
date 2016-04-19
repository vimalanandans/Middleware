/**
 * Answer ProtocolRole
 *
 * @author Cory Henson
 * @modified 06/11/2014 
 */
package com.bezirk.aip;

import com.bezirk.middleware.messages.ProtocolRole;

public class AnswerProtocolRole extends ProtocolRole {

	private String name = this.getClass().getSimpleName();
	private String desc = "Protocol role for answer-type messages";

	private static final String[] topics = {
		Answer.topic,
		Digest.topic,
		DisambiguationQuestions.topic,
		RelatedQuestions.topic,
		SingleAnswer.topic
	};

	@Override
	public String getProtocolName() {
		return name;
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return desc;
	}

	@Override
	public String[] getEventTopics() {
		return topics==null ?null:topics.clone();
	}

	@Override
	public String[] getStreamTopics() {
		return null;
	}
	
}
