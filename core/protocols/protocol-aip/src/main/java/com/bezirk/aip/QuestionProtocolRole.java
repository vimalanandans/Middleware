/**
 * Question ProtocolRole
 *
 * @author Cory Henson
 * @modified 06/11/2014
 */
package com.bezirk.aip;

import com.bezirk.middleware.messages.ProtocolRole;

public class QuestionProtocolRole extends ProtocolRole {
    private static final String[] topics = {
            Question.topic,
            IncompleteQuestion.topic,
            InspireMe.topic,
            OpenQuestion.topic
    };
    private String role = this.getClass().getSimpleName();

    @Override
    public String getProtocolName() {
        return role;
    }

    @Override
    public String getDescription() {
        return "Protocol role for question-type messages";
    }

    @Override
    public String[] getEventTopics() {
        return topics == null ? null : topics.clone();
    }

    @Override
    public String[] getStreamTopics() {
        return null;
    }

}