/**
 * @author Cory Henson
 */
package com.bezirk.aip;

import com.bezirk.middleware.messages.ProtocolRole;

public class AnswerProtocolRole extends ProtocolRole {

    private static final String[] topics = {
            Answer.topic,
            Digest.topic,
            DisambiguationQuestions.topic,
            RelatedQuestions.topic,
            SingleAnswer.topic
    };
    private String name = this.getClass().getSimpleName();

    @Override
    public String getProtocolName() {
        return name;
    }

    @Override
    public String getDescription() {
        return "Protocol role for answer-type messages";
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
